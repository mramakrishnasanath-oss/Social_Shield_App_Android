"""JWT auth utilities"""
import os
import logging
from fastapi import HTTPException, Security
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import firebase_admin
from firebase_admin import credentials, auth as firebase_auth

logger = logging.getLogger(__name__)

security = HTTPBearer()
_firebase_initialized = False

def init_firebase():
    global _firebase_initialized
    if not _firebase_initialized:
        try:
            cred_path = os.getenv("FIREBASE_CREDENTIALS_PATH", "firebase-credentials.json")
            if os.path.exists(cred_path):
                cred = credentials.Certificate(cred_path)
                firebase_admin.initialize_app(cred)
                _firebase_initialized = True
                logger.info("Firebase initialized")
            else:
                logger.warning("Firebase credentials not found — auth disabled in dev mode")
        except Exception as e:
            logger.error(f"Firebase init error: {e}")


async def get_current_user(credentials: HTTPAuthorizationCredentials = Security(security)) -> str:
    """Verify Firebase JWT token and return user_id"""
    if not _firebase_initialized:
        # Dev mode: accept any token, return it as user_id
        return credentials.credentials[:28] if len(credentials.credentials) > 28 else "dev_user"
    
    try:
        decoded = firebase_auth.verify_id_token(credentials.credentials)
        return decoded['uid']
    except Exception as e:
        raise HTTPException(status_code=401, detail=f"Invalid token: {str(e)}")


def verify_token(token: str) -> dict:
    if not _firebase_initialized:
        return {"uid": "dev_user"}
    try:
        return firebase_auth.verify_id_token(token)
    except Exception:
        raise HTTPException(status_code=401, detail="Invalid token")
