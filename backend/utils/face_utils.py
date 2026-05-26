"""Face detection utilities using OpenCV"""
import cv2
import numpy as np
from typing import Tuple

face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')


def detect_and_crop_faces(image: np.ndarray, padding: float = 0.2) -> Tuple[list, int]:
    """Detect faces and return cropped face images"""
    gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    faces_rect = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(48, 48))
    
    if len(faces_rect) == 0:
        return [], 0
    
    crops = []
    h, w = image.shape[:2]
    
    for (x, y, fw, fh) in faces_rect:
        # Add padding
        pad_x = int(fw * padding)
        pad_y = int(fh * padding)
        x1 = max(0, x - pad_x)
        y1 = max(0, y - pad_y)
        x2 = min(w, x + fw + pad_x)
        y2 = min(h, y + fh + pad_y)
        crops.append(image[y1:y2, x1:x2])
    
    return crops, len(crops)
