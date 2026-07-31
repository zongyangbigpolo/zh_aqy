@echo off
setlocal

set "SCRIPT_DIR=%~dp0"

echo ============================================================
echo Zh_AqY WINDOWS ENVIRONMENT PREFLIGHT CHECK
echo ============================================================
echo This check does not change your machine. It only reports
echo missing runtime tools and backend configuration.
echo.

powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%windows-preflight-check.ps1"

echo.
echo Finished. Press any key to close this window.
pause >nul
