<#
.SYNOPSIS
Pulls/clones Zh_AqY source code, builds backend/frontend, then deploys artifacts.

.DESCRIPTION
This script is intended for Windows servers that have Git, Maven, Node.js, npm,
and Java installed. It does not connect to MySQL and does not run SQL files.
Database backup/migration remains a manual operational step.
#>

[CmdletBinding()]
param(
    [string]$RepoUrl = "git@github.com:zongyangbigpolo/zh_aqy.git",
    [string]$Branch = "main",
    [string]$SourceDir = "D:\source\zh_aqy",
    [string]$DeployRoot = "D:\aqy",
    [string]$BackendDir,
    [string]$FrontendDir,
    [string]$BackendJarName = "aqy-admin.jar",
    [string]$ServiceName,
    [int]$BackendPort = 7070,
    [switch]$SkipNpmInstall,
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

function Invoke-External {
    param(
        [string]$Command,
        [string[]]$Arguments,
        [string]$WorkingDirectory
    )

    Push-Location $WorkingDirectory
    try {
        Write-Host ("> " + $Command + " " + ($Arguments -join " "))
        & $Command @Arguments
        if ($LASTEXITCODE -ne 0) {
            throw "Command failed with exit code $LASTEXITCODE: $Command $($Arguments -join ' ')"
        }
    }
    finally {
        Pop-Location
    }
}

$SourceDir = Resolve-FullPath $SourceDir
$DeployRoot = Resolve-FullPath $DeployRoot

Write-Step "Checking required commands"
$Git = Get-Command git -ErrorAction Stop
$Maven = Get-Command mvn -ErrorAction Stop
$Npm = Get-Command npm -ErrorAction Stop
Get-Command java -ErrorAction Stop | Out-Null

Write-Host "git : $($Git.Source)"
Write-Host "mvn : $($Maven.Source)"
Write-Host "npm : $($Npm.Source)"

Write-Step "Preparing source code"
if (-not (Test-Path $SourceDir -PathType Container)) {
    $ParentDir = Split-Path $SourceDir -Parent
    New-Item -ItemType Directory -Force -Path $ParentDir | Out-Null
    Invoke-External $Git.Source @("clone", "--branch", $Branch, $RepoUrl, $SourceDir) $ParentDir
}
elseif (Test-Path (Join-Path $SourceDir ".git") -PathType Container) {
    Invoke-External $Git.Source @("fetch", "origin", $Branch) $SourceDir
    Invoke-External $Git.Source @("checkout", $Branch) $SourceDir
    Invoke-External $Git.Source @("pull", "--ff-only", "origin", $Branch) $SourceDir
}
else {
    throw "SourceDir exists but is not a Git repository: $SourceDir"
}

Write-Step "Building backend jar"
Invoke-External $Maven.Source @("clean", "package", "-DskipTests") $SourceDir
$BackendJar = Join-Path $SourceDir "aqy-admin\target\aqy-admin.jar"
if (-not (Test-Path $BackendJar -PathType Leaf)) {
    throw "Backend build did not create expected jar: $BackendJar"
}

Write-Step "Building frontend dist"
$FrontendSourceDir = Join-Path $SourceDir "aqy-ui"
if (-not $SkipNpmInstall) {
    Invoke-External $Npm.Source @("install") $FrontendSourceDir
}
Invoke-External $Npm.Source @("run", "build:prod") $FrontendSourceDir
$FrontendDist = Join-Path $FrontendSourceDir "dist"
if (-not (Test-Path (Join-Path $FrontendDist "index.html") -PathType Leaf)) {
    throw "Frontend build did not create expected dist/index.html: $FrontendDist"
}

Write-Step "Deploying built artifacts"
$UpgradeScript = Join-Path $SourceDir "bin\windows-upgrade.ps1"
if (-not (Test-Path $UpgradeScript -PathType Leaf)) {
    throw "Upgrade script not found: $UpgradeScript"
}

$UpgradeArgs = @{
    BackendJar = $BackendJar
    FrontendDist = $FrontendDist
    DeployRoot = $DeployRoot
    BackendJarName = $BackendJarName
    BackendPort = $BackendPort
}

if (-not [string]::IsNullOrWhiteSpace($BackendDir)) {
    $UpgradeArgs.BackendDir = $BackendDir
}

if (-not [string]::IsNullOrWhiteSpace($FrontendDir)) {
    $UpgradeArgs.FrontendDir = $FrontendDir
}

if (-not [string]::IsNullOrWhiteSpace($ServiceName)) {
    $UpgradeArgs.ServiceName = $ServiceName
}

if ($SkipStop) {
    $UpgradeArgs.SkipStop = $true
}

if ($StartBackend) {
    $UpgradeArgs.StartBackend = $true
}

if ($RestartNginx) {
    $UpgradeArgs.RestartNginx = $true
}

& $UpgradeScript @UpgradeArgs

Write-Step "Build and deployment finished"
Write-Host "Source directory : $SourceDir"
Write-Host "Deploy directory : $DeployRoot"
Write-Host "Database was not touched by this script." -ForegroundColor Green
