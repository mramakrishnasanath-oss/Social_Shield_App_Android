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


async def save_scan_result(result: dict):
    """Save scan result to Firestore"""
    try:
        db = get_db()
        db.collection("scan_history").add(result)
    except Exception as e:
        logger.error(f"Failed to save scan result: {e}")
