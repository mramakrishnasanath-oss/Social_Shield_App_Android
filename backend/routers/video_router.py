"""Video Deepfake Detection Router (Mock)"""
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException
import uuid
import logging
from datetime import datetime

from utils.auth import get_current_user
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)

@router.post("/video")
async def scan_video(file: UploadFile = File(...), user_id: str = Depends(get_current_user)):
    """Mock video scan – returns deterministic result based on file size."""
    if not file.content_type.startswith("video/"):
        raise HTTPException(status_code=400, detail="File must be a video")
    contents = await file.read()
    if len(contents) > 500 * 1024 * 1024:
        raise HTTPException(status_code=413, detail="Video too large (max 500MB)")
    # Simple heuristic: file size modulo
    seed = len(contents) % 100
    fake_prob = min(0.99, max(0.01, seed / 100.0))
    real_prob = 1.0 - fake_prob
    if fake_prob >= 0.65:
        verdict = VerdictLevel.FAKE
        risk = "HIGH"
        explanations = ["High probability of deepfake content detected"]
    elif fake_prob >= 0.40:
        verdict = VerdictLevel.SUSPICIOUS
        risk = "MEDIUM"
        explanations = ["Potential manipulation detected, manual review recommended"]
    else:
        verdict = VerdictLevel.REAL
        risk = "LOW"
        explanations = ["Video appears authentic"]
    result = ScanResult(
        scan_id=str(uuid.uuid4()),
        user_id=user_id,
        media_type=MediaType.VIDEO,
        verdict=verdict,
        confidence=round(max(fake_prob, real_prob) * 100, 2),
        fake_probability=round(fake_prob * 100, 2),
        real_probability=round(real_prob * 100, 2),
        risk_level=risk,
        explanations=explanations,
        metadata={"info": "Mock video analysis (free tier)"},
        timestamp=datetime.utcnow().isoformat(),
    )
    return result.dict()
