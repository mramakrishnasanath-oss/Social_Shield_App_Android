"""
Video Deepfake Detection Router
Frame-by-frame CNN analysis + temporal consistency check
"""
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException, BackgroundTasks
import torch
import torch.nn.functional as F
import numpy as np
import cv2
from PIL import Image
import io
import tempfile
import os
import uuid
import asyncio
import logging
from datetime import datetime

from ml.models import model_registry, IMAGE_TRANSFORM
from utils.auth import get_current_user
from utils.face_utils import detect_and_crop_faces
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)

MAX_FRAMES = 30  # Max frames to analyze
FRAME_SAMPLE_RATE = 10  # Sample every N frames


def extract_frames(video_path: str, max_frames: int = MAX_FRAMES) -> list:
    """Extract evenly spaced frames from video"""
    cap = cv2.VideoCapture(video_path)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    fps = cap.get(cv2.CAP_PROP_FPS)
    duration = total_frames / fps if fps > 0 else 0
    
    frames = []
    indices = np.linspace(0, total_frames - 1, min(max_frames, total_frames), dtype=int)
    
    for idx in indices:
        cap.set(cv2.CAP_PROP_POS_FRAMES, idx)
        ret, frame = cap.read()
        if ret:
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            frames.append({
                'frame': frame_rgb,
                'frame_index': int(idx),
                'timestamp': float(idx / fps) if fps > 0 else 0.0
            })
    
    cap.release()
    return frames, duration, fps


def analyze_temporal_consistency(frame_scores: list[float]) -> dict:
    """Analyze temporal consistency of per-frame scores"""
    if len(frame_scores) < 2:
        return {"consistent": True, "variance": 0.0, "trend": "stable"}
    
    scores = np.array(frame_scores)
    variance = float(np.var(scores))
    
    # Check for sudden changes (potential edit points)
    diffs = np.abs(np.diff(scores))
    sudden_changes = int(np.sum(diffs > 0.3))
    
    # Trend analysis
    if np.mean(scores[:len(scores)//2]) < np.mean(scores[len(scores)//2:]):
        trend = "increasing_fakeness"
    else:
        trend = "stable"
    
    return {
        "consistent": variance < 0.05,
        "variance": variance,
        "trend": trend,
        "sudden_changes": sudden_changes,
        "high_variance_frames": int(np.sum(diffs > 0.3))
    }


def check_blink_patterns(frames: list) -> dict:
    """Detect unnatural blinking patterns using eye aspect ratio"""
    # Simplified blink detection using OpenCV
    eye_cascade = cv2.CascadeClassifier(
        cv2.data.haarcascades + 'haarcascade_eye.xml'
    )
    blink_data = []
    
    for frame_data in frames[::3]:  # Check every 3rd frame
        gray = cv2.cvtColor(frame_data['frame'], cv2.COLOR_RGB2GRAY)
        eyes = eye_cascade.detectMultiScale(gray, 1.1, 4)
        blink_data.append(len(eyes))
    
    if len(blink_data) < 2:
        return {"abnormal_blink": False, "blink_rate": 0}
    
    # Deepfakes often have very few blinks
    avg_eyes_detected = np.mean(blink_data)
    
    return {
        "abnormal_blink": avg_eyes_detected < 0.5,  # Few/no eyes = potential deepfake
        "blink_rate": float(avg_eyes_detected),
        "blink_consistency": float(np.std(blink_data))
    }


@router.post("/video")
async def scan_video(
    file: UploadFile = File(...),
    user_id: str = Depends(get_current_user)
):
    """
    Scan a video for deepfake manipulation.
    Analyzes frames + temporal consistency + blink patterns.
    """
    if not file.content_type.startswith("video/"):
        raise HTTPException(status_code=400, detail="File must be a video")
    
    contents = await file.read()
    if len(contents) > 500 * 1024 * 1024:  # 500MB limit
        raise HTTPException(status_code=413, detail="Video too large (max 500MB)")
    
    # Save to temp file
    with tempfile.NamedTemporaryFile(suffix='.mp4', delete=False) as tmp:
        tmp.write(contents)
        tmp_path = tmp.name
    
    try:
        model = model_registry.get('image')
        device = model_registry.device
        
        if model is None:
            raise HTTPException(status_code=503, detail="Model not available")
        
        # Extract frames
        frames, duration, fps = extract_frames(tmp_path)
        
        if not frames:
            raise HTTPException(status_code=400, detail="Could not extract frames from video")
        
        # Analyze each frame
        frame_results = []
        fake_scores = []
        
        for frame_data in frames:
            np_frame = frame_data['frame']
            pil_frame = Image.fromarray(np_frame)
            
            # Detect face in frame
            faces, face_count = detect_and_crop_faces(np_frame)
            if face_count > 0:
                pil_frame = Image.fromarray(faces[0])
            
            tensor = IMAGE_TRANSFORM(pil_frame).unsqueeze(0).to(device)
            
            with torch.no_grad():
                logits = model(tensor)
                probs = F.softmax(logits, dim=1)
                fake_prob = probs[0][1].item()
            
            fake_scores.append(fake_prob)
            frame_results.append({
                "frame_index": frame_data['frame_index'],
                "timestamp": frame_data['timestamp'],
                "fake_probability": round(fake_prob * 100, 2),
                "face_detected": face_count > 0
            })
        
        # Aggregate results
        avg_fake_prob = float(np.mean(fake_scores))
        max_fake_prob = float(np.max(fake_scores))
        
        # Temporal analysis
        temporal = analyze_temporal_consistency(fake_scores)
        
        # Blink pattern analysis
        blink_analysis = check_blink_patterns(frames)
        
        # Final verdict
        # Use weighted combination of mean and max
        final_score = (avg_fake_prob * 0.6 + max_fake_prob * 0.4)
        
        if final_score >= 0.65:
            verdict = VerdictLevel.FAKE
            risk = "HIGH"
        elif final_score >= 0.40:
            verdict = VerdictLevel.SUSPICIOUS
            risk = "MEDIUM"
        else:
            verdict = VerdictLevel.REAL
            risk = "LOW"
        
        # Build explanations
        explanations = []
        if final_score > 0.65:
            explanations.append(f"Deepfake patterns detected in {int(sum(s > 0.5 for s in fake_scores))}/{len(fake_scores)} frames")
        if blink_analysis.get("abnormal_blink"):
            explanations.append("Unnatural or absent blinking pattern detected")
        if not temporal.get("consistent"):
            explanations.append(f"Inconsistent manipulation across frames (variance: {temporal['variance']:.3f})")
        if temporal.get("sudden_changes", 0) > 2:
            explanations.append("Sudden authenticity shifts suggest edited segments")
        if not explanations:
            explanations.append("Video appears authentic — no significant deepfake artifacts")
        
        scan_id = str(uuid.uuid4())
        result = ScanResult(
            scan_id=scan_id,
            user_id=user_id,
            media_type=MediaType.VIDEO,
            verdict=verdict,
            confidence=round(max(final_score, 1 - final_score) * 100, 2),
            fake_probability=round(final_score * 100, 2),
            real_probability=round((1 - final_score) * 100, 2),
            risk_level=risk,
            explanations=explanations,
            metadata={
                "duration_seconds": duration,
                "fps": fps,
                "frames_analyzed": len(frames),
                "frame_results": frame_results[:10],  # Return first 10 frame details
                "temporal_analysis": temporal,
                "blink_analysis": blink_analysis
            },
            timestamp=datetime.utcnow().isoformat()
        )
        
        return result.dict()
    
    finally:
        os.unlink(tmp_path)
