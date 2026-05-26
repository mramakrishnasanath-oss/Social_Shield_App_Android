"""
Fake Social Media Profile Detection Router
"""
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
import httpx
import re
import uuid
import logging
from datetime import datetime
from typing import Optional

from utils.auth import get_current_user
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)


class ProfileScanRequest(BaseModel):
    username: Optional[str] = None
    display_name: Optional[str] = None
    bio: Optional[str] = None
    followers: Optional[int] = None
    following: Optional[int] = None
    post_count: Optional[int] = None
    account_age_days: Optional[int] = None
    profile_pic_url: Optional[str] = None
    platform: Optional[str] = "unknown"


def analyze_profile_signals(data: ProfileScanRequest) -> tuple[float, list[str]]:
    score = 0.0
    reasons = []

    # Follower/following ratio (bots follow many, have few followers)
    if data.followers is not None and data.following is not None:
        if data.following > 0 and data.followers / data.following < 0.1:
            score += 0.3
            reasons.append("Extremely low follower/following ratio — bot-like behavior")
        if data.following > 5000 and data.followers < 100:
            score += 0.25
            reasons.append("Mass following with minimal followers")

    # New account with many posts
    if data.account_age_days is not None and data.post_count is not None:
        if data.account_age_days < 30 and data.post_count > 200:
            score += 0.3
            reasons.append("Unusually high post frequency for new account")

    # Generic username patterns
    if data.username:
        if re.match(r'^[a-z]+\d{4,}$', data.username):
            score += 0.2
            reasons.append("Username matches bot-generation pattern (letters + numbers)")
        if len(data.username) > 20:
            score += 0.1
            reasons.append("Unusually long username")

    # Empty or generic bio
    if data.bio is not None:
        if len(data.bio) < 5:
            score += 0.15
            reasons.append("Empty or minimal bio")
        spam_words = ['crypto', 'investment', 'dm for', 'follow back', 'forex']
        matched = [w for w in spam_words if w in data.bio.lower()]
        if matched:
            score += 0.2 * len(matched)
            reasons.append(f"Spam keywords in bio: {', '.join(matched)}")

    return min(score, 1.0), reasons


@router.post("/profile")
async def scan_profile(
    request: ProfileScanRequest,
    user_id: str = Depends(get_current_user)
):
    """Analyze social media profile for fake/bot indicators"""
    fake_score, reasons = analyze_profile_signals(request)

    if fake_score >= 0.6:
        verdict = VerdictLevel.FAKE
        risk = "HIGH"
    elif fake_score >= 0.3:
        verdict = VerdictLevel.SUSPICIOUS
        risk = "MEDIUM"
    else:
        verdict = VerdictLevel.REAL
        risk = "LOW"

    if not reasons:
        reasons.append("Profile appears legitimate — no bot/fake indicators detected")

    scan_id = str(uuid.uuid4())
    result = ScanResult(
        scan_id=scan_id,
        user_id=user_id,
        media_type=MediaType.PROFILE,
        verdict=verdict,
        confidence=round(max(fake_score, 1 - fake_score) * 100, 2),
        fake_probability=round(fake_score * 100, 2),
        real_probability=round((1 - fake_score) * 100, 2),
        risk_level=risk,
        explanations=reasons,
        metadata={"platform": request.platform, "username": request.username},
        timestamp=datetime.utcnow().isoformat()
    )
    return result.dict()
