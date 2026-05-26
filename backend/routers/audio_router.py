"""
Audio Deepfake / Voice Clone Detection Router
Converts audio to mel-spectrogram → CNN classification
"""
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException
import torch
import torch.nn.functional as F
import numpy as np
import librosa
import librosa.display
from PIL import Image
import io
import tempfile
import os
import uuid
import logging
from datetime import datetime

from ml.models import model_registry
from utils.auth import get_current_user
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)

SR = 22050
DURATION = 5.0  # Analyze in 5-second windows
N_MELS = 128
HOP_LENGTH = 512


def audio_to_melspectrogram(audio_path: str, sr: int = SR) -> np.ndarray:
    """Convert audio file to mel-spectrogram tensor"""
    y, _ = librosa.load(audio_path, sr=sr, duration=30.0)
    
    # Segment into windows
    window_size = int(DURATION * sr)
    segments = []
    
    for start in range(0, len(y) - window_size, window_size // 2):
        segment = y[start:start + window_size]
        mel = librosa.feature.melspectrogram(
            y=segment, sr=sr, n_mels=N_MELS, hop_length=HOP_LENGTH
        )
        mel_db = librosa.power_to_db(mel, ref=np.max)
        # Normalize to [0, 1]
        mel_norm = (mel_db - mel_db.min()) / (mel_db.max() - mel_db.min() + 1e-8)
        segments.append(mel_norm)
    
    if not segments:
        # Handle short audio
        mel = librosa.feature.melspectrogram(y=y, sr=sr, n_mels=N_MELS, hop_length=HOP_LENGTH)
        mel_db = librosa.power_to_db(mel, ref=np.max)
        mel_norm = (mel_db - mel_db.min()) / (mel_db.max() - mel_db.min() + 1e-8)
        segments.append(mel_norm)
    
    return segments


def extract_audio_features(audio_path: str) -> dict:
    """Extract statistical features for rule-based analysis"""
    y, sr = librosa.load(audio_path, sr=SR)
    
    # MFCCs
    mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
    
    # Spectral features
    spectral_centroids = librosa.feature.spectral_centroid(y=y, sr=sr)
    spectral_rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    zero_crossing = librosa.feature.zero_crossing_rate(y)
    
    # Chroma features
    chroma = librosa.feature.chroma_stft(y=y, sr=sr)
    
    # Pitch analysis
    pitches, magnitudes = librosa.piptrack(y=y, sr=sr)
    pitch_values = pitches[magnitudes > np.median(magnitudes)]
    
    return {
        "mfcc_mean": float(np.mean(mfcc)),
        "mfcc_std": float(np.std(mfcc)),
        "spectral_centroid_mean": float(np.mean(spectral_centroids)),
        "spectral_rolloff_mean": float(np.mean(spectral_rolloff)),
        "zero_crossing_rate": float(np.mean(zero_crossing)),
        "chroma_mean": float(np.mean(chroma)),
        "pitch_variance": float(np.var(pitch_values)) if len(pitch_values) > 0 else 0.0,
        "duration": float(len(y) / sr),
        "unnatural_pitch_variance": float(np.var(pitch_values)) > 5000 if len(pitch_values) > 0 else False
    }


def heuristic_voice_analysis(features: dict) -> float:
    """Rule-based score boost based on acoustic features"""
    score_adjustment = 0.0
    
    # TTS/cloned voices often have very uniform MFCCs
    if features.get("mfcc_std", 0) < 5.0:
        score_adjustment += 0.15
    
    # Unnatural pitch patterns
    if features.get("unnatural_pitch_variance"):
        score_adjustment += 0.10
    
    # Too-perfect zero crossing rate
    if 0.05 < features.get("zero_crossing_rate", 0) < 0.08:
        score_adjustment += 0.05
    
    return min(score_adjustment, 0.30)  # Cap adjustment at 30%


@router.post("/audio")
async def scan_audio(
    file: UploadFile = File(...),
    user_id: str = Depends(get_current_user)
):
    """
    Detect AI-cloned or synthetic voice in audio.
    Uses mel-spectrogram CNN + acoustic feature analysis.
    """
    allowed_types = ["audio/mpeg", "audio/wav", "audio/mp4", "audio/ogg", "audio/x-wav"]
    if file.content_type not in allowed_types and not file.content_type.startswith("audio/"):
        raise HTTPException(status_code=400, detail="File must be audio")
    
    contents = await file.read()
    if len(contents) > 100 * 1024 * 1024:
        raise HTTPException(status_code=413, detail="Audio too large (max 100MB)")
    
    suffix = '.wav' if 'wav' in (file.content_type or '') else '.mp3'
    with tempfile.NamedTemporaryFile(suffix=suffix, delete=False) as tmp:
        tmp.write(contents)
        tmp_path = tmp.name
    
    try:
        model = model_registry.get('audio')
        device = model_registry.device
        
        # Extract mel-spectrograms
        mel_segments = audio_to_melspectrogram(tmp_path)
        
        # Extract acoustic features
        features = extract_audio_features(tmp_path)
        
        segment_scores = []
        
        if model is not None:
            for mel in mel_segments:
                # Resize to fixed size for CNN
                mel_resized = np.array(
                    Image.fromarray((mel * 255).astype(np.uint8)).resize((128, 128))
                ) / 255.0
                
                tensor = torch.FloatTensor(mel_resized).unsqueeze(0).unsqueeze(0).to(device)
                
                with torch.no_grad():
                    logits = model(tensor)
                    probs = F.softmax(logits, dim=1)
                    fake_prob = probs[0][1].item()
                
                segment_scores.append(fake_prob)
        else:
            # Fallback: pure heuristic
            logger.warning("Audio model not available, using heuristic only")
            segment_scores = [0.5]
        
        avg_fake_prob = float(np.mean(segment_scores))
        
        # Apply heuristic adjustment
        heuristic_boost = heuristic_voice_analysis(features)
        final_fake_prob = min(avg_fake_prob + heuristic_boost, 1.0)
        
        # Verdict
        if final_fake_prob >= 0.65:
            verdict = VerdictLevel.FAKE
            risk = "HIGH"
        elif final_fake_prob >= 0.40:
            verdict = VerdictLevel.SUSPICIOUS
            risk = "MEDIUM"
        else:
            verdict = VerdictLevel.REAL
            risk = "LOW"
        
        # Explanations
        explanations = []
        if final_fake_prob > 0.65:
            explanations.append("Voice patterns consistent with AI synthesis or cloning")
        if features.get("mfcc_std", 0) < 5.0:
            explanations.append("Unnaturally uniform vocal characteristics (possible TTS)")
        if features.get("unnatural_pitch_variance"):
            explanations.append("Abnormal pitch variation patterns detected")
        if heuristic_boost > 0.1:
            explanations.append("Multiple acoustic anomalies indicate synthetic voice")
        if not explanations:
            explanations.append("Voice appears natural — no synthetic voice patterns detected")
        
        scan_id = str(uuid.uuid4())
        result = ScanResult(
            scan_id=scan_id,
            user_id=user_id,
            media_type=MediaType.AUDIO,
            verdict=verdict,
            confidence=round(max(final_fake_prob, 1 - final_fake_prob) * 100, 2),
            fake_probability=round(final_fake_prob * 100, 2),
            real_probability=round((1 - final_fake_prob) * 100, 2),
            risk_level=risk,
            explanations=explanations,
            metadata={
                "segments_analyzed": len(segment_scores),
                "audio_features": features,
                "heuristic_boost": round(heuristic_boost * 100, 2)
            },
            timestamp=datetime.utcnow().isoformat()
        )
        
        return result.dict()
    
    finally:
        os.unlink(tmp_path)
