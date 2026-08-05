@echo off
setlocal

set "SCRIPT_DIR=%~dp0"

echo ============================================================
echo Zh_AqY DOCKER MIGRATE EXISTING MYSQL DATABASE
echo ============================================================
echo This imports an existing MySQL database into Docker MySQL.
echo Back up the old database before continuing.
echo.

powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%docker-migrate-existing-db.ps1"
if %ERRORLEVEL% NEQ 0 (
  echo Migration failed.
  pause
  exit /b 1
)

pause
