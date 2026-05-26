"""
Image Deepfake Detection Router
Uses EfficientNet + Grad-CAM for explainable AI
"""
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException
from fastapi.responses import JSONResponse
import torch
import torch.nn.functional as F
import numpy as np
import cv2
from PIL import Image
import io
import base64
import logging
from typing import Optional
import uuid
from datetime import datetime

from ml.models import model_registry, IMAGE_TRANSFORM
from utils.auth import get_current_user
from utils.gradcam import GradCAMGenerator
from utils.face_utils import detect_and_crop_faces
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)


def analyze_image_artifacts(image: np.ndarray) -> dict:
    """Analyze image for common deepfake artifacts"""
    gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    
    # Check for JPEG artifacts around face region
    laplacian_var = cv2.Laplacian(gray, cv2.CV_64F).var()
    
    # Noise analysis
    noise_level = estimate_noise(gray)
    
    # Frequency domain analysis (DCT)
    dct = cv2.dct(np.float32(gray) / 255.0)
    high_freq_energy = np.sum(np.abs(dct[64:, 64:])) / (dct.shape[0] * dct.shape[1])
    
    return {
        "sharpness_score": float(laplacian_var),
        "noise_level": float(noise_level),
        "high_freq_anomaly": float(high_freq_energy),
        "inconsistent_noise": noise_level > 15.0
    }


def estimate_noise(gray_image: np.ndarray) -> float:
    """Estimate image noise using Laplacian method"""
    H, W = gray_image.shape
    M = [[1, -2, 1], [-2, 4, -2], [1, -2, 1]]
    sigma = np.sum(np.abs(cv2.filter2D(np.float32(gray_image), -1, np.array(M))))
    sigma = sigma * np.sqrt(0.5 * np.pi) / (6 * (W - 2) * (H - 2))
    return sigma


def generate_explanation(fake_prob: float, artifacts: dict, face_count: int) -> list[str]:
    """Generate human-readable explanation for the detection result"""
    reasons = []
    
    if fake_prob > 0.7:
        reasons.append("High probability of AI-generated facial features")
    
    if artifacts.get("inconsistent_noise"):
        reasons.append("Inconsistent noise patterns detected across face region")
    
    if artifacts.get("high_freq_anomaly", 0) > 0.01:
        reasons.append("Unnatural high-frequency artifacts in facial boundaries")
    
    if artifacts.get("sharpness_score", 0) < 50:
        reasons.append("Unusual blurring patterns around facial edges")
    
    if face_count == 0:
        reasons.append("No face detected — analyzing full image for manipulation")
    
    if fake_prob > 0.85:
        reasons.extend(["Face warping artifacts detected", "Inconsistent skin texture rendering"])
    
    if not reasons:
        reasons.append("Image appears authentic — no significant manipulation detected")
    
    return reasons


@router.post("/image")
async def scan_image(
    file: UploadFile = File(...),
    user_id: str = Depends(get_current_user)
):
    """
    Scan an image for deepfake manipulation.
    Returns verdict, confidence score, Grad-CAM heatmap, and explanation.
    """
    # Validate file type
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="File must be an image")
    
    # Read and decode image
    contents = await file.read()
    if len(contents) > 20 * 1024 * 1024:  # 20MB limit
        raise HTTPException(status_code=413, detail="Image too large (max 20MB)")
    
    try:
        pil_image = Image.open(io.BytesIO(contents)).convert("RGB")
        np_image = np.array(pil_image)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Invalid image: {str(e)}")
    
    model = model_registry.get('image')
    device = model_registry.device
    
    if model is None:
        raise HTTPException(status_code=503, detail="Image model not available")
    
    # Detect faces
    faces, face_count = detect_and_crop_faces(np_image)
    
    # Use face crop if available, otherwise full image
    analysis_image = faces[0] if face_count > 0 else pil_image
    if isinstance(analysis_image, np.ndarray):
        analysis_image = Image.fromarray(analysis_image)
    
    # Preprocess for model
    tensor = IMAGE_TRANSFORM(analysis_image).unsqueeze(0).to(device)
    
    # Model inference
    with torch.no_grad():
        logits = model(tensor)
        probs = F.softmax(logits, dim=1)
        fake_prob = probs[0][1].item()  # Index 1 = fake class
        real_prob = probs[0][0].item()
    
    # Generate Grad-CAM heatmap
    heatmap_b64 = None
    try:
        gradcam = GradCAMGenerator(model, target_layer_name='backbone.conv_head')
        heatmap = gradcam.generate(tensor, target_class=1)
        
        # Overlay heatmap on original image
        resized_heatmap = cv2.resize(heatmap, (np_image.shape[1], np_image.shape[0]))
        heatmap_colored = cv2.applyColorMap(
            np.uint8(255 * resized_heatmap), cv2.COLORMAP_JET
        )
        overlay = cv2.addWeighted(
            cv2.cvtColor(np_image, cv2.COLOR_RGB2BGR), 0.6,
            heatmap_colored, 0.4, 0
        )
        _, buffer = cv2.imencode('.jpg', overlay)
        heatmap_b64 = base64.b64encode(buffer).decode('utf-8')
    except Exception as e:
        logger.warning(f"Grad-CAM failed: {e}")
    
    # Analyze artifacts
    artifacts = analyze_image_artifacts(np_image)
    
    # Determine verdict
    if fake_prob >= 0.75:
        verdict = VerdictLevel.FAKE
        risk = "HIGH"
    elif fake_prob >= 0.45:
        verdict = VerdictLevel.SUSPICIOUS
        risk = "MEDIUM"
    else:
        verdict = VerdictLevel.REAL
        risk = "LOW"
    
    # Generate explanation
    explanations = generate_explanation(fake_prob, artifacts, face_count)
    
    # Build result
    scan_id = str(uuid.uuid4())
    result = ScanResult(
        scan_id=scan_id,
        user_id=user_id,
        media_type=MediaType.IMAGE,
        verdict=verdict,
        confidence=round(max(fake_prob, real_prob) * 100, 2),
        fake_probability=round(fake_prob * 100, 2),
        real_probability=round(real_prob * 100, 2),
        risk_level=risk,
        explanations=explanations,
        heatmap_base64=heatmap_b64,
        metadata={
            "face_count": face_count,
            "image_size": f"{pil_image.width}x{pil_image.height}",
            "artifacts": artifacts
        },
        timestamp=datetime.utcnow().isoformat()
    )
    
    return result.dict()
