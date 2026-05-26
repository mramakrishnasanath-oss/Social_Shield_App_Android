"""ScanResult Pydantic models"""
from pydantic import BaseModel
from typing import Optional, Any
from enum import Enum


class VerdictLevel(str, Enum):
    REAL = "REAL"
    FAKE = "FAKE"
    SUSPICIOUS = "SUSPICIOUS"


class MediaType(str, Enum):
    IMAGE = "IMAGE"
    VIDEO = "VIDEO"
    AUDIO = "AUDIO"
    TEXT = "TEXT"
    URL = "URL"
    PROFILE = "PROFILE"


class ScanResult(BaseModel):
    scan_id: str
    user_id: str
    media_type: MediaType
    verdict: VerdictLevel
    confidence: float
    fake_probability: float
    real_probability: float
    risk_level: str  # LOW / MEDIUM / HIGH
    explanations: list[str]
    heatmap_base64: Optional[str] = None
    metadata: Optional[dict[str, Any]] = None
    timestamp: str
