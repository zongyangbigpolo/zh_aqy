@echo off
setlocal

set "SCRIPT_DIR=%~dp0"

echo ============================================================
echo Zh_AqY EXISTING WINDOWS SERVER UPGRADE
echo ============================================================
echo This script upgrades an existing deployment by replacing:
echo   - backend jar
echo   - frontend static files
echo.
echo It does NOT initialize MySQL, does NOT run SQL, and does NOT
echo delete or recreate your old database.
echo.
echo Before continuing, make sure the server already has required
echo environment variables, especially DB_URL, DB_USERNAME,
echo DB_PASSWORD, TOKEN_SECRET, Redis, MQTT, and SMS settings.
echo.
echo Press Ctrl+C now if you have not backed up the database.
pause

powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%deploy-release.ps1" -DeployRoot D:\aqy -StartBackend -RestartNginx

echo.
echo Finished. Press any key to close this window.
pause >nul
