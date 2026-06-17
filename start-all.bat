@echo off
title SocialShield - Full Stack
echo.
echo  ============================================================
echo    SocialShield - Starting Full Stack (Backend + Frontend)
echo  ============================================================
echo.

:: Start Backend in new window
echo [1/2] Starting Backend on http://localhost:8000 ...
start "SocialShield Backend" cmd /k "cd /d %~dp0backend && start.bat"

:: Wait a moment for backend to boot
timeout /t 3 /nobreak >nul

:: Start Frontend in new window
echo [2/2] Starting Frontend on http://localhost:5173 ...
start "SocialShield Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo  Both servers are starting in separate windows.
echo  Backend  -> http://localhost:8000
echo  Frontend -> http://localhost:5173
echo  API Docs -> http://localhost:8000/docs
echo.
echo  Press any key to open the app in browser...
pause >nul
start http://localhost:5173
