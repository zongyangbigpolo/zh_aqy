<#
.SYNOPSIS
Deploys a downloaded Zh_AqY release package on Windows.

.DESCRIPTION
Run this script from the extracted release package. It deploys the packaged
backend jar and frontend static files by calling windows-upgrade.ps1. It does
not connect to MySQL, does not drop databases, and does not run SQL files.
#>

[CmdletBinding()]
param(
    [string]$DeployRoot = "D:\aqy",
    [string]$BackendDir,
    [string]$FrontendDir,
    [string]$ServiceName,
    [int]$BackendPort = 7070,
    [switch]$SkipStop,
    [switch]$StartBackend,
    [switch]$RestartNginx
)

$ErrorActionPreference = "Stop"

$PackageRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$BackendJar = Join-Path $PackageRoot "server\aqy-admin.jar"
$FrontendDist = Join-Path $PackageRoot "web"
$UpgradeScript = Join-Path $PSScriptRoot "windows-upgrade.ps1"

if (-not (Test-Path $BackendJar -PathType Leaf)) {
    throw "Packaged backend jar not found: $BackendJar"
}

if (-not (Test-Path (Join-Path $FrontendDist "index.html") -PathType Leaf)) {
    throw "Packaged frontend dist not found: $FrontendDist"
}

if (-not (Test-Path $UpgradeScript -PathType Leaf)) {
    throw "Upgrade script not found: $UpgradeScript"
}

$Args = @{
    BackendJar = $BackendJar
    FrontendDist = $FrontendDist
    DeployRoot = $DeployRoot
    BackendPort = $BackendPort
}

if (-not [string]::IsNullOrWhiteSpace($BackendDir)) {
    $Args.BackendDir = $BackendDir
}

if (-not [string]::IsNullOrWhiteSpace($FrontendDir)) {
    $Args.FrontendDir = $FrontendDir
}

if (-not [string]::IsNullOrWhiteSpace($ServiceName)) {
    $Args.ServiceName = $ServiceName
}

if ($SkipStop) {
    $Args.SkipStop = $true
}

if ($StartBackend) {
    $Args.StartBackend = $true
}

if ($RestartNginx) {
    $Args.RestartNginx = $true
}

& $UpgradeScript @Args
