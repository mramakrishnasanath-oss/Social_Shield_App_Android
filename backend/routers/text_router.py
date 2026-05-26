"""
Text Scam Detection Router
Uses DistilBERT for phishing/fraud intent classification
"""
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
import torch
import torch.nn.functional as F
from transformers import pipeline, AutoTokenizer, AutoModelForSequenceClassification
import re
import uuid
import logging
from datetime import datetime

from utils.auth import get_current_user
from models.scan_result import ScanResult, MediaType, VerdictLevel

router = APIRouter()
logger = logging.getLogger(__name__)

# Load HuggingFace pipeline for text classification
_text_classifier = None

def get_text_classifier():
    global _text_classifier
    if _text_classifier is None:
        try:
            _text_classifier = pipeline(
                "text-classification",
                model="mrm8488/bert-tiny-finetuned-sms-spam-detection",
                device=-1  # CPU
            )
        except Exception as e:
            logger.error(f"Failed to load text classifier: {e}")
    return _text_classifier


# Regex patterns for rule-based scam detection
PHISHING_PATTERNS = [
    (r'(?i)(click here|click now|click immediately)', 0.3, "Urgency-triggering language detected"),
    (r'(?i)(verify your account|confirm your identity|update your information)', 0.35, "Account verification phishing attempt"),
    (r'(?i)(you have won|congratulations.*prize|claim your reward)', 0.4, "Lottery/prize scam pattern"),
    (r'(?i)(act now|limited time|expires in|urgent)', 0.25, "False urgency tactics"),
    (r'(?i)(send.*bitcoin|transfer.*crypto|wire transfer)', 0.45, "Cryptocurrency/wire transfer scam"),
    (r'(?i)(OTP|one.time.pass|verification code)', 0.3, "OTP phishing attempt"),
    (r'(?i)(your account.*suspended|locked|blocked)', 0.35, "Account threat scam pattern"),
    (r'(?i)(social security|SSN|tax.refund)', 0.4, "Government impersonation scam"),
    (r'(?i)(password|login credentials|username)', 0.3, "Credential harvesting attempt"),
    (r'(?i)(free.*gift|free.*money|₹\d+.*free)', 0.35, "Free money scam pattern"),
]

class TextScanRequest(BaseModel):
    text: str


@router.post("/text")
async def scan_text(
    request: TextScanRequest,
    user_id: str = Depends(get_current_user)
):
    """Scan text for scam, phishing, or fraud intent"""
    text = request.text.strip()
    
    if not text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    
    if len(text) > 10000:
        raise HTTPException(status_code=400, detail="Text too long (max 10000 chars)")
    
    classifier = get_text_classifier()
    
    ml_fake_prob = 0.5
    if classifier:
        try:
            result = classifier(text[:512])
            label = result[0]['label'].upper()
            score = result[0]['score']
            if 'SPAM' in label or 'FAKE' in label or 'PHISH' in label:
                ml_fake_prob = score
            else:
                ml_fake_prob = 1.0 - score
        except Exception as e:
            logger.error(f"Text classifier error: {e}")
    
    # Rule-based pattern matching
    pattern_score = 0.0
    pattern_reasons = []
    for pattern, weight, reason in PHISHING_PATTERNS:
        if re.search(pattern, text):
            pattern_score += weight
            pattern_reasons.append(reason)
    
    pattern_score = min(pattern_score, 0.9)
    
    # Combine ML + rule-based
    final_score = (ml_fake_prob * 0.6 + pattern_score * 0.4)
    final_score = min(final_score, 1.0)
    
    if final_score >= 0.65:
        verdict = VerdictLevel.FAKE
        risk = "HIGH"
    elif final_score >= 0.35:
        verdict = VerdictLevel.SUSPICIOUS
        risk = "MEDIUM"
    else:
        verdict = VerdictLevel.REAL
        risk = "LOW"
    
    explanations = pattern_reasons.copy()
    if ml_fake_prob > 0.6:
        explanations.append("NLP model detected high fraud intent probability")
    if not explanations:
        explanations.append("Text appears legitimate — no scam patterns detected")
    
    scan_id = str(uuid.uuid4())
    result = ScanResult(
        scan_id=scan_id,
        user_id=user_id,
        media_type=MediaType.TEXT,
        verdict=verdict,
        confidence=round(max(final_score, 1 - final_score) * 100, 2),
        fake_probability=round(final_score * 100, 2),
        real_probability=round((1 - final_score) * 100, 2),
        risk_level=risk,
        explanations=explanations,
        metadata={
            "text_length": len(text),
            "ml_score": round(ml_fake_prob * 100, 2),
            "pattern_score": round(pattern_score * 100, 2),
            "patterns_matched": len(pattern_reasons)
        },
        timestamp=datetime.utcnow().isoformat()
    )
    
    return result.dict()
