"""
ML Model Loader - Pre-trained deepfake detection models
Uses EfficientNet-B4 fine-tuned on FaceForensics++ dataset
"""
import torch
import torch.nn as nn
import torchvision.transforms as transforms
import timm
import numpy as np
import logging
from pathlib import Path
import os

logger = logging.getLogger(__name__)

# Model weights can be downloaded from HuggingFace or trained locally
MODEL_DIR = Path(os.getenv("MODEL_DIR", "./ml/weights"))

class DeepfakeImageDetector(nn.Module):
    """EfficientNet-B4 based deepfake detector"""
    def __init__(self, num_classes=2):
        super().__init__()
        self.backbone = timm.create_model('efficientnet_b4', pretrained=True, num_classes=0)
        in_features = self.backbone.num_features
        self.classifier = nn.Sequential(
            nn.Dropout(0.4),
            nn.Linear(in_features, 512),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(512, num_classes)
        )

    def forward(self, x):
        features = self.backbone(x)
        return self.classifier(features)


class XceptionDeepfakeDetector(nn.Module):
    """Xception-based detector (alternative backbone)"""
    def __init__(self, num_classes=2):
        super().__init__()
        self.backbone = timm.create_model('xception', pretrained=True, num_classes=0)
        in_features = self.backbone.num_features
        self.classifier = nn.Sequential(
            nn.Dropout(0.5),
            nn.Linear(in_features, 256),
            nn.ReLU(),
            nn.Linear(256, num_classes)
        )

    def forward(self, x):
        features = self.backbone(x)
        return self.classifier(features)


class AudioDeepfakeDetector(nn.Module):
    """CNN-based audio deepfake detector using mel-spectrograms"""
    def __init__(self, num_classes=2):
        super().__init__()
        self.features = nn.Sequential(
            nn.Conv2d(1, 32, 3, padding=1), nn.BatchNorm2d(32), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(32, 64, 3, padding=1), nn.BatchNorm2d(64), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(64, 128, 3, padding=1), nn.BatchNorm2d(128), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(128, 256, 3, padding=1), nn.BatchNorm2d(256), nn.ReLU(), nn.AdaptiveAvgPool2d((4, 4)),
        )
        self.classifier = nn.Sequential(
            nn.Flatten(),
            nn.Linear(256 * 4 * 4, 512),
            nn.ReLU(),
            nn.Dropout(0.5),
            nn.Linear(512, num_classes)
        )

    def forward(self, x):
        x = self.features(x)
        return self.classifier(x)


class ModelRegistry:
    """Singleton registry for all loaded models"""
    _instance = None
    _models = {}
    _device = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self):
        if not self._device:
            self._device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
            logger.info(f"Using device: {self._device}")

    def load_all(self):
        """Load all models into memory"""
        self._load_image_model()
        self._load_audio_model()
        logger.info("All models loaded successfully")

    def _load_image_model(self):
        model = DeepfakeImageDetector()
        weight_path = MODEL_DIR / "efficientnet_b4_deepfake.pth"
        if weight_path.exists():
            state = torch.load(weight_path, map_location=self._device)
            model.load_state_dict(state)
            logger.info("Loaded fine-tuned EfficientNet weights")
        else:
            logger.warning("No fine-tuned weights found, using ImageNet pretrained (reduce accuracy)")
        model.eval()
        model.to(self._device)
        self._models['image'] = model

    def _load_audio_model(self):
        model = AudioDeepfakeDetector()
        weight_path = MODEL_DIR / "audio_deepfake_cnn.pth"
        if weight_path.exists():
            state = torch.load(weight_path, map_location=self._device)
            model.load_state_dict(state)
        else:
            logger.warning("No audio model weights found, using random init")
        model.eval()
        model.to(self._device)
        self._models['audio'] = model

    def get(self, name: str):
        return self._models.get(name)

    @property
    def device(self):
        return self._device


# Image preprocessing transforms
IMAGE_TRANSFORM = transforms.Compose([
    transforms.Resize((380, 380)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])

model_registry = ModelRegistry()
