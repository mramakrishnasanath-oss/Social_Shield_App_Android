"""History Router - Fetch user scan history from Firestore"""
from fastapi import APIRouter, Depends, HTTPException, Query
from typing import Optional
import logging

from utils.auth import get_current_user
from utils.database import get_db

router = APIRouter()
logger = logging.getLogger(__name__)


@router.get("/history")
async def get_history(
    user_id: str = Depends(get_current_user),
    media_type: Optional[str] = Query(None),
    limit: int = Query(20, ge=1, le=100),
    offset: int = Query(0, ge=0)
):
    """Get paginated scan history for current user"""
    try:
        db = get_db()
        query = db.collection("scan_history").where("user_id", "==", user_id)
        
        if media_type:
            query = query.where("media_type", "==", media_type.upper())
        
        query = query.order_by("timestamp", direction="DESCENDING").limit(limit).offset(offset)
        docs = query.stream()
        
        results = []
        for doc in docs:
            data = doc.to_dict()
            data['id'] = doc.id
            # Remove large fields for list view
            data.pop('heatmap_base64', None)
            results.append(data)
        
        return {"items": results, "count": len(results), "offset": offset}
    
    except Exception as e:
        logger.error(f"History fetch error: {e}")
        raise HTTPException(status_code=500, detail="Failed to fetch history")


@router.get("/history/{scan_id}")
async def get_scan_detail(
    scan_id: str,
    user_id: str = Depends(get_current_user)
):
    """Get full details for a specific scan"""
    try:
        db = get_db()
        docs = db.collection("scan_history").where("scan_id", "==", scan_id).where("user_id", "==", user_id).limit(1).stream()
        
        for doc in docs:
            return doc.to_dict()
        
        raise HTTPException(status_code=404, detail="Scan not found")
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Scan detail error: {e}")
        raise HTTPException(status_code=500, detail="Failed to fetch scan")


@router.delete("/history/{scan_id}")
async def delete_scan(
    scan_id: str,
    user_id: str = Depends(get_current_user)
):
    """Delete a scan record"""
    try:
        db = get_db()
        docs = list(db.collection("scan_history").where("scan_id", "==", scan_id).where("user_id", "==", user_id).limit(1).stream())
        
        if not docs:
            raise HTTPException(status_code=404, detail="Scan not found")
        
        docs[0].reference.delete()
        return {"message": "Scan deleted successfully"}
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail="Failed to delete scan")

@router.delete("")
async def clear_history(user_id: str = Depends(get_current_user)):
    """Delete all scan history records for the current user"""
    try:
        db = get_db()
        docs = list(db.collection("scan_history").where("user_id", "==", user_id).stream())
        for doc in docs:
            doc.reference.delete()
        return {"message": "All scan history cleared successfully", "count": len(docs)}
    except Exception as e:
        logger.error(f"Clear history error: {e}")
        raise HTTPException(status_code=500, detail="Failed to clear scan history")



@router.get("/stats")
async def get_user_stats(user_id: str = Depends(get_current_user)):
    """Get aggregated stats for dashboard trust score"""
    try:
        db = get_db()
        docs = list(db.collection("scan_history").where("user_id", "==", user_id).stream())
        
        total = len(docs)
        fake_count = sum(1 for d in docs if d.to_dict().get('verdict') == 'FAKE')
        suspicious = sum(1 for d in docs if d.to_dict().get('verdict') == 'SUSPICIOUS')
        
        trust_score = 100
        if total > 0:
            threat_rate = (fake_count + suspicious * 0.5) / total
            trust_score = max(0, int(100 - threat_rate * 100))
        
        return {
            "total_scans": total,
            "fake_detected": fake_count,
            "suspicious_detected": suspicious,
            "trust_score": trust_score
        }
    except Exception as e:
        return {"total_scans": 0, "fake_detected": 0, "suspicious_detected": 0, "trust_score": 100}
