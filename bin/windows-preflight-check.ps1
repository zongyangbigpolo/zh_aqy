<#
.SYNOPSIS
Preflight checker for Zh_AqY Windows deployments.

.DESCRIPTION
Checks release package files, local runtime tools, service reachability, and
backend environment variables before a fresh test deployment or an existing
server upgrade. The script does not modify the machine.
#>

[CmdletBinding()]
param(
    [string]$DeployRoot = "D:\aqy",
    [switch]$RequireProductionConfig,
    [switch]$SkipPackageCheck,
    [switch]$SkipNginx,
    [string]$MysqlHost,
    [int]$MysqlPort = 0,
    [string]$RedisHost,
    [int]$RedisPort = 0
)

$ErrorActionPreference = "Stop"
$Issues = New-Object System.Collections.Generic.List[object]

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Add-Issue {
    param(
        [ValidateSet("ERROR", "WARN", "INFO")]
        [string]$Severity,
        [string]$Area,
        [string]$Message,
        [string]$Fix
    )

    $Issues.Add([PSCustomObject]@{
        Severity = $Severity
        Area = $Area
        Message = $Message
        Fix = $Fix
    }) | Out-Null
}

function Get-EnvValue {
    param([string]$Name)
    return [Environment]::GetEnvironmentVariable($Name, "Process")
}

function Test-EnvPresent {
    param(
        [string]$Name,
        [string]$Description,
        [switch]$Required
    )

    $Value = Get-EnvValue $Name
    if ([string]::IsNullOrWhiteSpace($Value)) {
        $Severity = "WARN"
        if ($Required) {
            $Severity = "ERROR"
        }
        Add-Issue $Severity "Config" "$Name is not configured. $Description" ('Set it with: setx {0} "your-value"' -f $Name)
        return $false
    }

    Write-Host "[OK] $Name configured"
    return $true
}

function Find-Executable {
    param(
        [string]$CommandName,
        [string[]]$Candidates
    )

    $Command = Get-Command $CommandName -ErrorAction SilentlyContinue
    if ($null -ne $Command) {
        return $Command.Source
    }

    foreach ($Candidate in $Candidates) {
        $Matches = @(Get-ChildItem -Path $Candidate -ErrorAction SilentlyContinue)
        if ($Matches.Count -gt 0) {
            return $Matches[0].FullName
        }
    }

    return $null
}

function Test-TcpPort {
    param(
        [string]$HostName,
        [int]$Port,
        [int]$TimeoutMilliseconds = 3000
    )

    $Client = New-Object System.Net.Sockets.TcpClient
    try {
        $Async = $Client.BeginConnect($HostName, $Port, $null, $null)
        if (-not $Async.AsyncWaitHandle.WaitOne($TimeoutMilliseconds, $false)) {
            return $false
        }

        $Client.EndConnect($Async)
        return $true
    }
    catch {
        return $false
    }
    finally {
        $Client.Close()
    }
}

function Get-JavaVersionText {
    param([string]$JavaPath)
    $Output = & $JavaPath -version 2>&1
    return ($Output | Out-String).Trim()
}

function Resolve-DatabaseEndpointFromUrl {
    param([string]$DbUrl)
    if ($DbUrl -match "^jdbc:mysql://([^:/?]+)(?::([0-9]+))?/") {
        $Port = 3306
        if (-not [string]::IsNullOrWhiteSpace($Matches[2])) {
            $Port = [int]$Matches[2]
        }
        return [PSCustomObject]@{
            Host = $Matches[1]
            Port = $Port
        }
    }

    return $null
}

Write-Host "============================================================"
Write-Host "Zh_AqY Windows preflight check"
Write-Host "============================================================"

Write-Step "Checking release package files"
$PackageRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
if (-not $SkipPackageCheck) {
    $RequiredFiles = @(
        "server\aqy-admin.jar",
        "web\index.html",
        "bin\RUN-CHECK-WINDOWS-ENV.bat",
        "bin\RUN-INSTALL-WINDOWS-PREREQS.bat",
        "bin\RUN-FRESH-WINDOWS-TEST.bat",
        "bin\RUN-UPGRADE-EXISTING-WINDOWS.bat",
        "bin\deploy-release.ps1",
        "bin\windows-upgrade.ps1",
        "bin\windows-preflight-check.ps1",
        "bin\windows-install-prerequisites.ps1",
        "bin\windows-fresh-test-deploy.ps1",
        "sql\ry_20240629.sql",
        "sql\quartz.sql",
        "sql\zh_aqy_schema.sql"
    )

    foreach ($RelativePath in $RequiredFiles) {
        $Path = Join-Path $PackageRoot $RelativePath
        if (Test-Path $Path -PathType Leaf) {
            Write-Host "[OK] $RelativePath"
        }
        else {
            Add-Issue "ERROR" "Package" "Missing release package file: $RelativePath" "Download a complete release zip and extract it again."
        }
    }
}
else {
    Write-Host "Package file checks skipped."
}

Write-Step "Checking runtime programs"
$Java = Find-Executable -CommandName "java" -Candidates @(
    "C:\Program Files\Eclipse Adoptium\jdk-8*\bin\java.exe",
    "C:\Program Files\Java\jdk1.8*\bin\java.exe",
    "C:\Program Files\Java\jre1.8*\bin\java.exe"
)
if ($null -eq $Java) {
    Add-Issue "ERROR" "Java" "Java was not found." "Install Java 8, then reopen this command window."
}
else {
    $JavaVersion = Get-JavaVersionText $Java
    Write-Host "[OK] java found: $Java"
    Write-Host $JavaVersion
    if ($JavaVersion -notmatch 'version "1\.8\.' -and $JavaVersion -notmatch 'version "8\.') {
        Add-Issue "WARN" "Java" "Java exists but does not look like Java 8." "Install Java 8 or verify this JRE can run aqy-admin.jar."
    }
}

$MysqlCli = Find-Executable -CommandName "mysql" -Candidates @(
    "C:\Program Files\MySQL\MySQL Server *\bin\mysql.exe",
    "C:\Program Files\MariaDB *\bin\mysql.exe",
    "C:\tools\mysql*\bin\mysql.exe"
)
if ($null -eq $MysqlCli) {
    Add-Issue "WARN" "MySQL" "mysql.exe was not found in PATH." "Install MySQL client or add mysql.exe to PATH. Fresh database initialization needs it."
}
else {
    Write-Host "[OK] mysql.exe found: $MysqlCli"
}

$RedisCli = Find-Executable -CommandName "redis-cli" -Candidates @(
    "C:\Program Files\Redis\redis-cli.exe",
    "C:\tools\redis*\redis-cli.exe"
)
if ($null -eq $RedisCli) {
    Add-Issue "INFO" "Redis" "redis-cli was not found." "This is optional, but redis-cli helps diagnose Redis connectivity."
}
else {
    Write-Host "[OK] redis-cli found: $RedisCli"
}

if (-not $SkipNginx) {
    $Nginx = Find-Executable -CommandName "nginx" -Candidates @(
        "C:\nginx\nginx.exe",
        "C:\tools\nginx\nginx.exe",
        "C:\Program Files\nginx*\nginx.exe"
    )
    if ($null -eq $Nginx) {
        Add-Issue "INFO" "Nginx" "nginx was not found." "Nginx is optional. Install it if this server will serve the frontend directly."
    }
    else {
        Write-Host "[OK] nginx found: $Nginx"
    }
}

Write-Step "Checking backend environment variables"
$Strict = [bool]$RequireProductionConfig
$DbUrlPresent = Test-EnvPresent -Name "DB_URL" -Description "MySQL JDBC URL used by the backend." -Required:$Strict
Test-EnvPresent -Name "DB_USERNAME" -Description "MySQL application username." -Required:$Strict | Out-Null
Test-EnvPresent -Name "DB_PASSWORD" -Description "MySQL application password." -Required:$Strict | Out-Null
$TokenPresent = Test-EnvPresent -Name "TOKEN_SECRET" -Description "JWT signing secret. Use a long random value." -Required:$Strict
Test-EnvPresent -Name "DRUID_LOGIN_USERNAME" -Description "Druid console username." -Required:$false | Out-Null
Test-EnvPresent -Name "DRUID_LOGIN_PASSWORD" -Description "Druid console password." -Required:$false | Out-Null

if ($TokenPresent) {
    $TokenSecret = Get-EnvValue "TOKEN_SECRET"
    if ($TokenSecret.Length -lt 32) {
        Add-Issue "WARN" "Config" "TOKEN_SECRET is shorter than 32 characters." "Use a long random secret, then restart the backend."
    }
}

$UploadPath = Get-EnvValue "FILE_UPLOAD_PATH"
if ([string]::IsNullOrWhiteSpace($UploadPath)) {
    $UploadPath = Get-EnvValue "RUOYI_PROFILE"
}
if ([string]::IsNullOrWhiteSpace($UploadPath)) {
    Add-Issue "WARN" "Config" "FILE_UPLOAD_PATH is not configured." 'Set it with: setx FILE_UPLOAD_PATH "D:\aqy\uploadPath"'
}
else {
    Write-Host "[OK] upload path configured: $UploadPath"
    if (-not (Test-Path $UploadPath -PathType Container)) {
        Add-Issue "WARN" "Filesystem" "Upload directory does not exist: $UploadPath" "Create it before starting the backend."
    }
}

Write-Step "Checking service ports"
$DbUrl = Get-EnvValue "DB_URL"
$DbEndpoint = Resolve-DatabaseEndpointFromUrl $DbUrl
if (-not [string]::IsNullOrWhiteSpace($MysqlHost)) {
    if ($MysqlPort -le 0) {
        $MysqlPort = 3306
    }
    $DbEndpoint = [PSCustomObject]@{ Host = $MysqlHost; Port = $MysqlPort }
}

if ($null -eq $DbEndpoint) {
    if ($DbUrlPresent) {
        Add-Issue "WARN" "MySQL" "DB_URL is configured but the host and port could not be parsed." "Check the JDBC URL format."
    }
    else {
        Write-Host "MySQL TCP check skipped because DB_URL is not configured."
    }
}
else {
    if (Test-TcpPort -HostName $DbEndpoint.Host -Port $DbEndpoint.Port) {
        Write-Host "[OK] MySQL reachable: $($DbEndpoint.Host):$($DbEndpoint.Port)"
    }
    else {
        Add-Issue "WARN" "MySQL" "MySQL is not reachable at $($DbEndpoint.Host):$($DbEndpoint.Port)." "Start MySQL, check firewall, or fix DB_URL."
    }
}

if ([string]::IsNullOrWhiteSpace($RedisHost)) {
    $RedisHost = Get-EnvValue "REDIS_HOST"
}
if ([string]::IsNullOrWhiteSpace($RedisHost)) {
    $RedisHost = "127.0.0.1"
}
if ($RedisPort -le 0) {
    $RedisPortText = Get-EnvValue "REDIS_PORT"
    if ([string]::IsNullOrWhiteSpace($RedisPortText)) {
        $RedisPort = 6379
    }
    else {
        $RedisPort = [int]$RedisPortText
    }
}

if (Test-TcpPort -HostName $RedisHost -Port $RedisPort) {
    Write-Host "[OK] Redis reachable: ${RedisHost}:${RedisPort}"
}
else {
    Add-Issue "WARN" "Redis" "Redis is not reachable at ${RedisHost}:${RedisPort}." "Start Redis, check firewall, or set REDIS_HOST/REDIS_PORT."
}

Write-Step "Checking optional business integrations"
$MqttHost = Get-EnvValue "MQTT_HOST"
if ([string]::IsNullOrWhiteSpace($MqttHost)) {
    Add-Issue "WARN" "MQTT" "MQTT_HOST is not configured." "Set MQTT_HOST/MQTT_USERNAME/MQTT_PASSWORD if this server receives device data through MQTT."
}
else {
    Write-Host "[OK] MQTT_HOST configured: $MqttHost"
}

$SmsVars = @(
    "ALIYUN_SMS_ACCESS_KEY_ID",
    "ALIYUN_SMS_ACCESS_KEY_SECRET",
    "ALIYUN_SMS_SIGN_NAME",
    "ALIYUN_SMS_TEMPLATE_CODE"
)
$MissingSms = @()
foreach ($Name in $SmsVars) {
    if ([string]::IsNullOrWhiteSpace((Get-EnvValue $Name))) {
        $MissingSms += $Name
    }
}
if ($MissingSms.Count -gt 0) {
    Add-Issue "WARN" "Aliyun SMS" ("SMS environment variables are incomplete: " + ($MissingSms -join ", ")) "Set all SMS variables before enabling alarm SMS notifications."
}
else {
    Write-Host "[OK] Aliyun SMS variables configured"
}

Write-Step "Preflight summary"
if ($Issues.Count -eq 0) {
    Write-Host "All checks passed." -ForegroundColor Green
    exit 0
}

foreach ($Issue in $Issues) {
    $Color = "Gray"
    if ($Issue.Severity -eq "ERROR") {
        $Color = "Red"
    }
    elseif ($Issue.Severity -eq "WARN") {
        $Color = "Yellow"
    }

    Write-Host "[$($Issue.Severity)] $($Issue.Area): $($Issue.Message)" -ForegroundColor $Color
    Write-Host "       Fix: $($Issue.Fix)"
}

Write-Host ""
Write-Host "Common production variables:" -ForegroundColor Cyan
Write-Host 'setx DB_URL "jdbc:mysql://127.0.0.1:3306/zh_aqy?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8"'
Write-Host 'setx DB_USERNAME "zh_aqy_app"'
Write-Host 'setx DB_PASSWORD "your-db-password"'
Write-Host 'setx TOKEN_SECRET "replace-with-a-long-random-secret"'
Write-Host 'setx REDIS_HOST "127.0.0.1"'
Write-Host 'setx REDIS_PORT "6379"'
Write-Host 'setx FILE_UPLOAD_PATH "D:\aqy\uploadPath"'
Write-Host ""
Write-Host "After setx, close and reopen the command window before starting the backend." -ForegroundColor Yellow

$Errors = @($Issues | Where-Object { $_.Severity -eq "ERROR" })
if ($Errors.Count -gt 0) {
    exit 1
}

exit 0
