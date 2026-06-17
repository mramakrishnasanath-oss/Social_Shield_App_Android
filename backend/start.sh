#!/bin/bash
# SocialShield Backend Startup Script (Linux / macOS)
set -e

cd "$(dirname "$0")"

echo ""
echo "  =============================================="
echo "    SocialShield Backend - FastAPI Server"
echo "  =============================================="
echo ""

# Check Python
if ! command -v python3 &>/dev/null; then
    echo "[ERROR] python3 not found. Install Python 3.11+"
    exit 1
fi

# Create virtualenv if missing
if [ ! -d "venv" ]; then
    echo "[INFO] Creating virtual environment..."
    python3 -m venv venv
fi

source venv/bin/activate

echo "[INFO] Installing dependencies..."
pip install -r requirements.txt -q

# Create .env from example if missing
if [ ! -f ".env" ] && [ -f ".env.example" ]; then
    cp .env.example .env
    echo "[INFO] Created .env from .env.example — configure your API keys"
fi

echo ""
echo "  [INFO] Starting SocialShield API on http://localhost:8000"
echo "  [INFO] API Docs at http://localhost:8000/docs"
echo "  [INFO] Press Ctrl+C to stop"
echo ""

python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
