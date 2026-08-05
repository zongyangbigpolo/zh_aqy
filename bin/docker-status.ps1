[CmdletBinding()]
param()

. (Join-Path $PSScriptRoot "docker-common.ps1")

Require-Docker
$EnvValues = Load-ExistingDockerEnv

Invoke-ComposeMigrate -Arguments @("ps")

$WebPort = if ($EnvValues.ContainsKey("WEB_PORT")) { $EnvValues["WEB_PORT"] } else { "8080" }
$BackendPort = if ($EnvValues.ContainsKey("BACKEND_PORT")) { $EnvValues["BACKEND_PORT"] } else { "7070" }

Write-Host ""
Write-Host "Frontend probe:"
try {
    Invoke-WebRequest -UseBasicParsing -TimeoutSec 10 -Method Head -Uri "http://127.0.0.1:$WebPort/" | Select-Object -First 1 | Format-List
}
catch {
    Write-Host $_.Exception.Message
}

Write-Host ""
Write-Host "Backend probe:"
try {
    $Response = Invoke-WebRequest -UseBasicParsing -TimeoutSec 10 -Uri "http://127.0.0.1:$BackendPort/prod-api/captchaImage"
    Write-Host $Response.Content.Substring(0, [Math]::Min(200, $Response.Content.Length))
}
catch {
    Write-Host $_.Exception.Message
}
