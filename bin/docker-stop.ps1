[CmdletBinding()]
param()

. (Join-Path $PSScriptRoot "docker-common.ps1")

Require-Docker
$null = Load-ExistingDockerEnv

Invoke-ComposeMigrate -Arguments @("down")
Write-Host "Docker services stopped. Database, Redis, and upload volumes were preserved."
