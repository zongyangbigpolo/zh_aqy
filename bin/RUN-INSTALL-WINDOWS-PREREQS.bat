@echo off
setlocal

set "SCRIPT_DIR=%~dp0"

echo ============================================================
echo Zh_AqY WINDOWS PREREQUISITE INSTALLER
echo ============================================================
echo This helper installs or guides installation of:
echo   - Java 8
echo   - MySQL
echo   - Redis
echo Optional:
echo   - Nginx
echo.
echo Some installers may require Administrator permission.
echo.
set /p INCLUDE_NGINX=Install optional Nginx too? Type Y to include it: 

if /I "%INCLUDE_NGINX%"=="Y" (
  powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%windows-install-prerequisites.ps1" -IncludeNginx
) else (
  powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%windows-install-prerequisites.ps1"
)

echo.
echo Finished. Press any key to close this window.
pause >nul
