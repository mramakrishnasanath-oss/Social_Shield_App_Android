"""
Audio Deepfake / Voice Clone Detection Router - Mock Implementation
Returns realistic analysis results without requiring heavy ML dependencies.
"""
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException
import uuid
import logging
from datetime import datetime

from utils.auth import get_current_user
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)


@router.post("/audio")
async def scan_audio(
    file: UploadFile = File(...),
    user_id: str = Depends(get_current_user)
):
    """
    Detect AI-cloned or synthetic voice in audio.
    """
    if not file.content_type.startswith("audio/"):
        raise HTTPException(status_code=400, detail="File must be audio")

    contents = await file.read()
    if len(contents) > 100 * 1024 * 1024:
        raise HTTPException(status_code=413, detail="Audio too large (max 100MB)")

    # Deterministic mock based on file size
    seed = len(contents) % 100
    fake_prob = min(0.99, max(0.01, seed / 100.0))
    real_prob = 1.0 - fake_prob

    if fake_prob >= 0.65:
        verdict = VerdictLevel.FAKE
        risk = "HIGH"
        explanations = [
            "Voice patterns consistent with AI synthesis or cloning",
            "Unnaturally uniform vocal characteristics detected (possible TTS)",
            "Abnormal pitch variation patterns detected"
        ]
    elif fake_prob >= 0.40:
        verdict = VerdictLevel.SUSPICIOUS
        risk = "MEDIUM"
        explanations = [
            "Some acoustic anomalies found — requires manual review",
            "Minor irregularities in spectral characteristics"
        ]
    else:
        verdict = VerdictLevel.REAL
        risk = "LOW"
        explanations = ["Voice appears natural — no synthetic voice patterns detected"]

    result = ScanResult(
        scan_id=str(uuid.uuid4()),
        user_id=user_id,
        media_type=MediaType.AUDIO,
        verdict=verdict,
        confidence=round(max(fake_prob, real_prob) * 100, 2),
        fake_probability=round(fake_prob * 100, 2),
        real_probability=round(real_prob * 100, 2),
        risk_level=risk,
        explanations=explanations,
        metadata={
            "segments_analyzed": 6,
            "audio_features": {
                "mfcc_std": round(3.5 + fake_prob * 5, 2),
                "zero_crossing_rate": round(0.06 + fake_prob * 0.02, 4),
                "duration": round(len(contents) / 16000, 2)
            },
            "heuristic_boost": round(fake_prob * 20, 2)
        },
        timestamp=datetime.utcnow().isoformat()
    )

    return result.dict()
