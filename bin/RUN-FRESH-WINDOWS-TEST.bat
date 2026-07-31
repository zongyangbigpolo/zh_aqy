@echo off
setlocal

set "SCRIPT_DIR=%~dp0"

echo ============================================================
echo Zh_AqY FRESH WINDOWS TEST DEPLOY
echo ============================================================
echo This script is ONLY for a new test machine or an empty test DB.
echo It imports initialization SQL into an empty/disposable database.
echo.
echo DO NOT use this script to upgrade an old server with real data.
echo For an existing old Windows server, use:
echo   bin\RUN-UPGRADE-EXISTING-WINDOWS.bat
echo.
echo If Java 8, MySQL, or Redis are not installed yet, run:
echo   bin\RUN-INSTALL-WINDOWS-PREREQS.bat
echo.
echo Press Ctrl+C now if this is an existing production/old server.
pause

powershell -ExecutionPolicy Bypass -File "%SCRIPT_DIR%windows-fresh-test-deploy.ps1" -MysqlAdminUser root

echo.
echo Finished. Press any key to close this window.
pause >nul
