"""Firestore database utilities"""
import os
import logging
import firebase_admin
from firebase_admin import firestore

logger = logging.getLogger(__name__)
_db = None


async def init_db():
    global _db
    try:
        from utils.auth import init_firebase
        init_firebase()
        _db = firestore.client()
        logger.info("Firestore connected")
    except Exception as e:
        logger.warning(f"Firestore not available: {e}. Running without persistence.")


def get_db():
    if _db is None:
        raise Exception("Database not initialized")
    return _db


async def save_scan_result_and_update_stats(user_id: str, scan_id: str, result: dict):
    """Save scan result to subcollection and transactionally update user stats"""
    try:
        db = get_db()
        
        # Map snake_case to camelCase for Android and shared database compatibility
        result_camel = {
            "scanId": result.get("scan_id") or scan_id,
            "userId": result.get("user_id") or user_id,
            "mediaType": result.get("media_type") or "IMAGE",
            "verdict": result.get("verdict") or "SAFE",
            "confidence": result.get("confidence") or 100.0,
            "fakeProbability": result.get("fake_probability") or 0.0,
            "realProbability": result.get("real_probability") or 100.0,
            "riskLevel": result.get("risk_level") or "LOW",
            "explanations": result.get("explanations") or [],
            "recommendations": result.get("recommendations") or [],
            "heatmapBase64": result.get("heatmap_base64"),
            "metadata": result.get("metadata"),
            "timestamp": result.get("timestamp")
        }
        
        # 1. Save scan to subcollection
        scan_ref = db.collection("users").document(user_id).collection("scans").document(scan_id)
        scan_ref.set(result_camel)
        
        # 2. Update user stats document transactionally
        user_ref = db.collection("users").document(user_id)
        
        @firestore.transactional
        def update_stats_transaction(transaction, user_reference):
            snapshot = user_reference.get(transaction=transaction)
            
            total_scans = 0
            fake_detected = 0
            suspicious_detected = 0
            safe_detected = 0
            
            if snapshot.exists:
                user_data = snapshot.to_dict()
                total_scans = user_data.get("totalScans", 0) or 0
                fake_detected = user_data.get("fakeDetected", 0) or 0
                suspicious_detected = user_data.get("suspiciousDetected", 0) or 0
                safe_detected = user_data.get("safeDetected", 0) or 0
                
            verdict = result_camel.get("verdict", "SAFE").upper()
            total_scans += 1
            if verdict == "SAFE":
                safe_detected += 1
            elif verdict == "SUSPICIOUS":
                suspicious_detected += 1
            elif verdict == "FAKE":
                fake_detected += 1
                
            trust_score = 100
            if total_scans > 0:
                trust_score = int(((safe_detected + 0.5 * suspicious_detected) / total_scans) * 100)
                
            transaction.set(user_reference, {
                "totalScans": total_scans,
                "fakeDetected": fake_detected,
                "suspiciousDetected": suspicious_detected,
                "safeDetected": safe_detected,
                "trustScore": trust_score
            }, merge=True)
            
        transaction = db.transaction()
        update_stats_transaction(transaction, user_ref)
        logger.info(f"Scan {scan_id} saved and stats updated successfully for user {user_id}")
        
    except Exception as e:
        logger.error(f"Failed to save scan result or update stats: {e}")
