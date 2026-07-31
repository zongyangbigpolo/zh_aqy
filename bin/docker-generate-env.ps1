<#
.SYNOPSIS
Generates a local .env file for Docker Compose fresh-test deployment.
#>

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
$EnvFile = Join-Path $RootDir ".env"

function New-RandomSecret {
    param([int]$ByteCount = 32)
    $Bytes = New-Object byte[] $ByteCount
    $Generator = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    try {
        $Generator.GetBytes($Bytes)
    }
    finally {
        $Generator.Dispose()
    }

    return ([Convert]::ToBase64String($Bytes) -replace "\+", "A" -replace "/", "B").TrimEnd("=")
}

if (Test-Path $EnvFile -PathType Leaf) {
    Write-Host ".env already exists: $EnvFile"
    exit 0
}

$Content = @"
DB_NAME=zh_aqy
MYSQL_ROOT_PASSWORD=$(New-RandomSecret 24)
DB_USERNAME=zh_aqy_app
DB_PASSWORD=$(New-RandomSecret 24)
DRUID_LOGIN_USERNAME=admin
DRUID_LOGIN_PASSWORD=$(New-RandomSecret 18)
TOKEN_SECRET=$(New-RandomSecret 32)
WEB_PORT=8080
BACKEND_PORT=7070
MQTT_HOST=tcp://127.0.0.1:1883
MQTT_USERNAME=
MQTT_PASSWORD=
ALIYUN_SMS_ACCESS_KEY_ID=
ALIYUN_SMS_ACCESS_KEY_SECRET=
ALIYUN_SMS_SIGN_NAME=
ALIYUN_SMS_TEMPLATE_CODE=
ALIYUN_SMS_COOLDOWN_SECONDS=600
"@

Set-Content -LiteralPath $EnvFile -Value $Content -Encoding ASCII
Write-Host "Created Docker Compose environment file: $EnvFile"
