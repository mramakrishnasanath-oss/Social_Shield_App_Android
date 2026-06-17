@echo off
title SocialShield Backend
echo.
echo  ==============================================
echo    SocialShield Backend - FastAPI Server
echo  ==============================================
echo.

cd /d "%~dp0"

:: Check Python
where python >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Python not found. Install Python 3.11+ from https://python.org
    pause
    exit /b 1
)

:: Create venv if missing
if not exist "venv\Scripts\activate.bat" (
    echo [INFO] Creating virtual environment...
    python -m venv venv
)

:: Activate venv
call venv\Scripts\activate.bat

:: Install / upgrade dependencies
echo [INFO] Installing dependencies...
pip install -r requirements.txt -q

:: Copy .env.example if .env doesn't exist
if not exist ".env" (
    if exist ".env.example" (
        copy ".env.example" ".env" >nul
        echo [INFO] Created .env from .env.example - please configure your API keys
    )
)

echo.
echo  [INFO] Starting SocialShield API on http://localhost:8000
echo  [INFO] API Docs available at http://localhost:8000/docs
echo  [INFO] Press Ctrl+C to stop
echo.

python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload

pause
