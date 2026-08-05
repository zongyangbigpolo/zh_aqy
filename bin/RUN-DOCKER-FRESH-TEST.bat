@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "ROOT_DIR=%SCRIPT_DIR%.."

echo ============================================================
echo Zh_AqY DOCKER FRESH TEST DEPLOY
echo ============================================================
echo This starts MySQL, Redis, backend, and frontend with Docker Compose.
echo It is intended for new test/demo environments.
echo.

powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%docker-generate-env.ps1"
cd /d "%ROOT_DIR%"

set "WEB_PORT=8080"
set "BACKEND_PORT=7070"
for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
  if "%%A"=="WEB_PORT" set "WEB_PORT=%%B"
  if "%%A"=="BACKEND_PORT" set "BACKEND_PORT=%%B"
)

docker compose version >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  docker compose -f docker-compose.yml -f docker-compose.fresh.yml up -d --build
) else (
  docker-compose --version >nul 2>nul
  if %ERRORLEVEL% EQU 0 (
    docker-compose -f docker-compose.yml -f docker-compose.fresh.yml up -d --build
  ) else (
    echo Docker Compose was not found. Install Docker Desktop first.
    pause
    exit /b 1
  )
)

echo.
echo Docker fresh-test deployment started.
echo Frontend: http://127.0.0.1:%WEB_PORT%/
echo Backend : http://127.0.0.1:%BACKEND_PORT%/prod-api/captchaImage
echo Login   : admin / ChangeMe@123456
echo Change the admin password immediately after first login.
pause
