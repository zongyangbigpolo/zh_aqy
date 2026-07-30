<#
.SYNOPSIS
Non-destructive Windows upgrade helper for Zh_AqY.

.DESCRIPTION
Copies a built backend jar and frontend dist into an existing Windows deployment.
The script backs up existing artifacts before replacing them. It never connects
to MySQL, never drops/recreates databases, and never runs SQL files.
#>

[CmdletBinding()]
param(
    [string]$BackendJar = ".\aqy-admin\target\aqy-admin.jar",
    [string]$FrontendDist = ".\aqy-ui\dist",
    [string]$DeployRoot = "D:\aqy",
    [string]$BackendDir,
    [string]$FrontendDir,
    [string]$BackendJarName = "aqy-admin.jar",
    [string]$BackendProcessMatch = "aqy-admin.jar",
    [string]$ServiceName,
    [int]$BackendPort = 7070,
    [switch]$SkipStop,
    [switch]$StartBackend,
    [switch]$RestartNginx
)

$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Resolve-FullPath {
    param([string]$Path)
    return $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($Path)
}

if ([string]::IsNullOrWhiteSpace($BackendDir)) {
    $BackendDir = Join-Path $DeployRoot "server"
}

if ([string]::IsNullOrWhiteSpace($FrontendDir)) {
    $FrontendDir = Join-Path $DeployRoot "web"
}

$BackendJar = Resolve-FullPath $BackendJar
$FrontendDist = Resolve-FullPath $FrontendDist
$DeployRoot = Resolve-FullPath $DeployRoot
$BackendDir = Resolve-FullPath $BackendDir
$FrontendDir = Resolve-FullPath $FrontendDir
$BackupDir = Join-Path $DeployRoot ("backups\" + (Get-Date -Format "yyyyMMdd-HHmmss"))
$BackendDest = Join-Path $BackendDir $BackendJarName

Write-Step "Checking build artifacts"
if (-not (Test-Path $BackendJar -PathType Leaf)) {
    throw "Backend jar not found: $BackendJar"
}

$FrontendIndex = Join-Path $FrontendDist "index.html"
if (-not (Test-Path $FrontendIndex -PathType Leaf)) {
    throw "Frontend dist is invalid, index.html not found: $FrontendIndex"
}

Write-Host "Backend jar : $BackendJar"
Write-Host "Frontend    : $FrontendDist"
Write-Host "Backend dir : $BackendDir"
Write-Host "Frontend dir: $FrontendDir"
Write-Host "Backup dir  : $BackupDir"

Write-Step "Creating deployment and backup directories"
New-Item -ItemType Directory -Force -Path $DeployRoot, $BackendDir, $FrontendDir, $BackupDir | Out-Null

if (-not $SkipStop) {
    Write-Step "Stopping existing backend process"
    if (-not [string]::IsNullOrWhiteSpace($ServiceName)) {
        $Service = Get-Service -Name $ServiceName -ErrorAction SilentlyContinue
        if ($null -eq $Service) {
            throw "Windows service not found: $ServiceName"
        }

        if ($Service.Status -ne "Stopped") {
            Write-Host "Stopping Windows service: $ServiceName"
            Stop-Service -Name $ServiceName -Force
            $Service.WaitForStatus("Stopped", [TimeSpan]::FromSeconds(60))
        }
        else {
            Write-Host "Windows service is already stopped: $ServiceName"
        }
    }

    $Processes = @(Get-CimInstance Win32_Process | Where-Object {
        $_.CommandLine -and (
            $_.CommandLine -like "*$BackendProcessMatch*" -or
            $_.CommandLine -like "*$BackendJarName*"
        )
    })

    if ($Processes.Count -eq 0) {
        Write-Host "No matching backend process found."
    }
    else {
        foreach ($Process in $Processes) {
            Write-Host "Stopping PID $($Process.ProcessId): $($Process.CommandLine)"
            Stop-Process -Id $Process.ProcessId -Force
        }
        Start-Sleep -Seconds 2
    }
}
else {
    Write-Warning "SkipStop was specified. Existing backend process was not stopped."
}

Write-Step "Backing up current backend jar"
if (Test-Path $BackendDest -PathType Leaf) {
    Copy-Item $BackendDest (Join-Path $BackupDir $BackendJarName) -Force
    Write-Host "Backed up: $BackendDest"
}
else {
    Write-Host "No existing backend jar found at $BackendDest"
}

Write-Step "Backing up current frontend files"
$FrontendBackupDir = Join-Path $BackupDir "web"
New-Item -ItemType Directory -Force -Path $FrontendBackupDir | Out-Null
$ExistingFrontendFiles = @(Get-ChildItem $FrontendDir -Force -ErrorAction SilentlyContinue)
if ($ExistingFrontendFiles.Count -gt 0) {
    Copy-Item (Join-Path $FrontendDir "*") $FrontendBackupDir -Recurse -Force
    Write-Host "Backed up frontend files to: $FrontendBackupDir"
}
else {
    Write-Host "No existing frontend files found."
}

Write-Step "Deploying backend jar"
Copy-Item $BackendJar $BackendDest -Force
Write-Host "Deployed: $BackendDest"

Write-Step "Deploying frontend dist"
Remove-Item (Join-Path $FrontendDir "*") -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item (Join-Path $FrontendDist "*") $FrontendDir -Recurse -Force
Write-Host "Deployed frontend files to: $FrontendDir"

if ($StartBackend) {
    Write-Step "Starting backend"
    if (-not [string]::IsNullOrWhiteSpace($ServiceName)) {
        Write-Host "Starting Windows service: $ServiceName"
        Start-Service -Name $ServiceName
    }
    else {
        $Java = Get-Command java -ErrorAction Stop
        $JavaArgs = @(
            "-Duser.timezone=Asia/Shanghai",
            "-Xms512m",
            "-Xmx1024m",
            "-jar",
            $BackendDest
        )
        Start-Process -FilePath $Java.Source -ArgumentList $JavaArgs -WorkingDirectory $BackendDir -WindowStyle Hidden
    }
    Start-Sleep -Seconds 8

    $HealthUrl = "http://127.0.0.1:$BackendPort/prod-api/captchaImage"
    try {
        Invoke-WebRequest -Uri $HealthUrl -UseBasicParsing -TimeoutSec 10 | Out-Null
        Write-Host "Backend responded: $HealthUrl" -ForegroundColor Green
    }
    catch {
        Write-Warning "Backend start command was issued, but health check failed: $HealthUrl"
        Write-Warning $_.Exception.Message
    }
}
else {
    Write-Host "StartBackend was not specified. Start backend manually when ready."
}

if ($RestartNginx) {
    Write-Step "Reloading nginx"
    $Nginx = Get-Command nginx -ErrorAction SilentlyContinue
    if ($null -eq $Nginx) {
        Write-Warning "nginx command was not found in PATH. Reload nginx manually."
    }
    else {
        & $Nginx.Source -s reload
        Write-Host "nginx reload command executed."
    }
}

Write-Step "Upgrade finished"
Write-Host "Database was not touched by this script." -ForegroundColor Green
Write-Host "Backup location: $BackupDir"
