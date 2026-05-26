"""
Model Download & Setup Script
Downloads or trains deepfake detection models for SocialShield

Usage:
    python setup_models.py --download    # Download pre-trained weights
    python setup_models.py --train       # Fine-tune on FaceForensics++
    python setup_models.py --verify      # Verify models are working
"""

import os
import sys
import argparse
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, Dataset
import torchvision.transforms as transforms
from PIL import Image
import numpy as np
from pathlib import Path
import requests
import hashlib
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

MODEL_DIR = Path("ml/weights")
MODEL_DIR.mkdir(parents=True, exist_ok=True)

# ─── HuggingFace Model Hub sources ───────────────────────────────────────────
# These point to open-source deepfake detection weights on HuggingFace
HUGGINGFACE_MODELS = {
    "efficientnet_b4_deepfake": {
        "url": "https://huggingface.co/spaces/dvilasuero/deepfake-detection/resolve/main/model.pth",
        "filename": "efficientnet_b4_deepfake.pth",
        "description": "EfficientNet-B4 fine-tuned on FaceForensics++"
    }
}

# ─── Synthetic Training Dataset ──────────────────────────────────────────────

class DeepfakeDataset(Dataset):
    """
    Dataset for deepfake training.
    Expects directory structure:
        data/
            real/   <- real face images
            fake/   <- deepfake images
    """
    def __init__(self, root_dir: str, transform=None):
        self.samples = []
        self.transform = transform

        real_dir = Path(root_dir) / "real"
        fake_dir = Path(root_dir) / "fake"

        if real_dir.exists():
            for img_path in real_dir.glob("*.jpg"):
                self.samples.append((str(img_path), 0))
            for img_path in real_dir.glob("*.png"):
                self.samples.append((str(img_path), 0))

        if fake_dir.exists():
            for img_path in fake_dir.glob("*.jpg"):
                self.samples.append((str(img_path), 1))
            for img_path in fake_dir.glob("*.png"):
                self.samples.append((str(img_path), 1))

        logger.info(f"Dataset: {len([s for s in self.samples if s[1]==0])} real, "
                    f"{len([s for s in self.samples if s[1]==1])} fake")

    def __len__(self):
        return len(self.samples)

    def __getitem__(self, idx):
        path, label = self.samples[idx]
        image = Image.open(path).convert("RGB")
        if self.transform:
            image = self.transform(image)
        return image, label


def download_model(model_info: dict) -> bool:
    """Download a pre-trained model from URL"""
    dest = MODEL_DIR / model_info["filename"]
    if dest.exists():
        logger.info(f"Model already exists: {dest}")
        return True

    logger.info(f"Downloading {model_info['description']}...")
    try:
        response = requests.get(model_info["url"], stream=True, timeout=60)
        response.raise_for_status()

        total = int(response.headers.get('content-length', 0))
        downloaded = 0

        with open(dest, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                f.write(chunk)
                downloaded += len(chunk)
                if total > 0:
                    pct = downloaded / total * 100
                    print(f"\r  Progress: {pct:.1f}%", end="", flush=True)

        print()
        logger.info(f"Downloaded: {dest}")
        return True

    except Exception as e:
        logger.error(f"Download failed: {e}")
        logger.info("Will use ImageNet pretrained weights (lower accuracy)")
        return False


def train_image_model(data_dir: str = "data/deepfake", epochs: int = 10):
    """Fine-tune EfficientNet on deepfake dataset"""
    import sys
    sys.path.insert(0, ".")
    from ml.models import DeepfakeImageDetector, IMAGE_TRANSFORM

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    logger.info(f"Training on: {device}")

    # Augmented training transforms
    train_transform = transforms.Compose([
        transforms.RandomHorizontalFlip(),
        transforms.RandomRotation(10),
        transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.1),
        transforms.Resize((380, 380)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
    ])

    dataset = DeepfakeDataset(data_dir, transform=train_transform)
    if len(dataset) == 0:
        logger.error(f"No data found in {data_dir}")
        logger.info("Please download FaceForensics++ dataset:")
        logger.info("  https://github.com/ondyari/FaceForensics")
        return

    train_size = int(0.8 * len(dataset))
    val_size = len(dataset) - train_size
    train_set, val_set = torch.utils.data.random_split(dataset, [train_size, val_size])

    train_loader = DataLoader(train_set, batch_size=16, shuffle=True, num_workers=4)
    val_loader = DataLoader(val_set, batch_size=16, shuffle=False, num_workers=4)

    model = DeepfakeImageDetector(num_classes=2).to(device)
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.AdamW(model.parameters(), lr=1e-4, weight_decay=1e-4)
    scheduler = optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=epochs)

    best_val_acc = 0.0

    for epoch in range(epochs):
        # Training
        model.train()
        train_loss, correct, total = 0, 0, 0
        for images, labels in train_loader:
            images, labels = images.to(device), labels.to(device)
            optimizer.zero_grad()
            outputs = model(images)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()
            train_loss += loss.item()
            _, predicted = outputs.max(1)
            total += labels.size(0)
            correct += predicted.eq(labels).sum().item()

        train_acc = 100. * correct / total

        # Validation
        model.eval()
        val_correct, val_total = 0, 0
        with torch.no_grad():
            for images, labels in val_loader:
                images, labels = images.to(device), labels.to(device)
                outputs = model(images)
                _, predicted = outputs.max(1)
                val_total += labels.size(0)
                val_correct += predicted.eq(labels).sum().item()

        val_acc = 100. * val_correct / val_total
        scheduler.step()

        logger.info(f"Epoch {epoch+1}/{epochs} | Train Acc: {train_acc:.2f}% | Val Acc: {val_acc:.2f}%")

        if val_acc > best_val_acc:
            best_val_acc = val_acc
            save_path = MODEL_DIR / "efficientnet_b4_deepfake.pth"
            torch.save(model.state_dict(), save_path)
            logger.info(f"  New best! Saved to {save_path}")

    logger.info(f"Training complete. Best validation accuracy: {best_val_acc:.2f}%")


def verify_models():
    """Run inference test to verify models are working"""
    logger.info("Verifying models...")
    import sys
    sys.path.insert(0, ".")
    from ml.models import model_registry, IMAGE_TRANSFORM

    model_registry.load_all()

    # Test image model
    img_model = model_registry.get('image')
    if img_model:
        dummy = torch.randn(1, 3, 380, 380).to(model_registry.device)
        with torch.no_grad():
            out = img_model(dummy)
        logger.info(f"✓ Image model OK — output shape: {out.shape}")
    else:
        logger.error("✗ Image model not loaded")

    # Test audio model
    audio_model = model_registry.get('audio')
    if audio_model:
        dummy = torch.randn(1, 1, 128, 128).to(model_registry.device)
        with torch.no_grad():
            out = audio_model(dummy)
        logger.info(f"✓ Audio model OK — output shape: {out.shape}")
    else:
        logger.error("✗ Audio model not loaded")

    logger.info("Verification complete.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="SocialShield Model Setup")
    parser.add_argument("--download", action="store_true", help="Download pre-trained weights")
    parser.add_argument("--train", action="store_true", help="Train on local dataset")
    parser.add_argument("--verify", action="store_true", help="Verify model inference")
    parser.add_argument("--data-dir", default="data/deepfake", help="Training data directory")
    parser.add_argument("--epochs", type=int, default=10, help="Training epochs")
    args = parser.parse_args()

    if args.download:
        for name, info in HUGGINGFACE_MODELS.items():
            download_model(info)

    if args.train:
        train_image_model(args.data_dir, args.epochs)

    if args.verify:
        verify_models()

    if not any([args.download, args.train, args.verify]):
        parser.print_help()
