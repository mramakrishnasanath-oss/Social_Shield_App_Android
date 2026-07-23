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
            import json
            cred_json = os.getenv("FIREBASE_CREDENTIALS_JSON")
            if cred_json:
                cred_dict = json.loads(cred_json)
                cred = credentials.Certificate(cred_dict)
                firebase_admin.initialize_app(cred)
                _firebase_initialized = True
                logger.info("Firebase initialized from env json")
                return

            cred_path = os.getenv("FIREBASE_CREDENTIALS_PATH", "firebase-credentials.json")
            if os.path.exists(cred_path):
                cred = credentials.Certificate(cred_path)
                firebase_admin.initialize_app(cred)
                _firebase_initialized = True
                logger.info("Firebase initialized from file")
            else:
                logger.warning("Firebase credentials not found — auth disabled in dev mode")
        except Exception as e:
            logger.error(f"Firebase init error: {e}")


async def get_current_user(credentials: HTTPAuthorizationCredentials = Security(security)) -> str:
    """Verify Firebase JWT token and return user_id"""
    if not _firebase_initialized:
        # Dev mode: accept any token, return it as user_id
        token = credentials.credentials
        if token.startswith("demo_"):
            return token.replace("demo_", "")
        return token[:28] if len(token) > 28 else "dev_user"
    
    try:
        decoded = firebase_auth.verify_id_token(credentials.credentials)
        return decoded['uid']
    except Exception as e:
        raise HTTPException(status_code=401, detail=f"Invalid token: {str(e)}")


def verify_token(token: str) -> dict:
    if not _firebase_initialized:
        uid = token.replace("demo_", "") if token.startswith("demo_") else "dev_user"
        return {"uid": uid}
    try:
        return firebase_auth.verify_id_token(token)
    except Exception:
        raise HTTPException(status_code=401, detail="Invalid token")
