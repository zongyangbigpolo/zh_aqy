<#
.SYNOPSIS
Fresh Windows test deployment helper for Zh_AqY release packages.

.DESCRIPTION
Run this script from an extracted GitHub Actions release package. It creates a
fresh MySQL database/user, imports the bundled SQL files, deploys the packaged
backend/frontend, writes a reusable backend start script, and starts the backend
for testing.

This script is intended for NEW TEST MACHINES or EMPTY TEST DATABASES only.
It refuses to import destructive initialization SQL into a non-empty database
unless ForceReinitialize is explicitly specified.
#>

[CmdletBinding()]
param(
    [string]$DeployRoot = "D:\aqy",
    [string]$DatabaseName = "zh_aqy",
    [string]$MysqlHost = "127.0.0.1",
    [int]$MysqlPort = 3306,
    [string]$MysqlAdminUser = "root",
    [securestring]$MysqlAdminPassword,
    [string]$MysqlCli,
    [string]$DbUsername = "zh_aqy_app",
    [string]$DbPassword,
    [string]$RedisHost = "127.0.0.1",
    [int]$RedisPort = 6379,
    [string]$RedisPassword = "",
    [string]$MqttHost = "tcp://127.0.0.1:1883",
    [string]$MqttUsername = "",
    [string]$MqttPassword = "",
    [int]$ServerPort = 7070,
    [int]$WebPort = 80,
    [string]$TokenSecret,
    [string]$NginxExe,
    [switch]$SkipDatabaseInit,
    [switch]$SkipSqlImport,
    [switch]$SkipRedisCheck,
    [switch]$SkipNginx,
    [switch]$NoStartBackend,
    [switch]$PersistUserEnvironment,
    [switch]$ForceReinitialize
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

function ConvertFrom-SecureStringPlainText {
    param([securestring]$Value)
    if ($null -eq $Value) {
        return ""
    }

    $Ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($Value)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($Ptr)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($Ptr)
    }
}

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

function Test-SafeIdentifier {
    param(
        [string]$Value,
        [string]$Name
    )

    if ($Value -notmatch "^[A-Za-z0-9_]+$") {
        throw "$Name may only contain letters, numbers, and underscores: $Value"
    }
}

function Escape-MySqlString {
    param([string]$Value)
    return ($Value -replace "\\", "\\\\" -replace "'", "''")
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

function Invoke-MySql {
    param(
        [string]$Sql,
        [string]$Database,
        [switch]$Batch
    )

    $Args = @(
        "--protocol=tcp",
        "-h", $MysqlHost,
        "-P", [string]$MysqlPort,
        "-u", $MysqlAdminUser,
        "--default-character-set=utf8mb4"
    )

    if (-not [string]::IsNullOrWhiteSpace($Database)) {
        $Args += @("--database=$Database")
    }

    if ($Batch) {
        $Args += @("--batch", "--skip-column-names")
    }

    $PreviousMysqlPwd = $env:MYSQL_PWD
    try {
        $env:MYSQL_PWD = $MysqlAdminPasswordText
        $Output = $Sql | & $MysqlCli @Args 2>&1
        $ExitCode = $LASTEXITCODE
    }
    finally {
        $env:MYSQL_PWD = $PreviousMysqlPwd
    }

    if ($ExitCode -ne 0) {
        throw "mysql command failed with exit code $ExitCode.`n$($Output | Out-String)"
    }

    return $Output
}

function Import-MySqlFile {
    param(
        [string]$Path,
        [string]$Database
    )

    Write-Host "Importing SQL: $Path"
    $Sql = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
    Invoke-MySql -Sql $Sql -Database $Database | Out-Null
}

function Set-RuntimeEnvironment {
    param([hashtable]$Values)

    foreach ($Item in $Values.GetEnumerator()) {
        Set-Item -Path ("Env:" + $Item.Key) -Value $Item.Value
        if ($PersistUserEnvironment) {
            [Environment]::SetEnvironmentVariable($Item.Key, $Item.Value, "User")
        }
    }
}

function ConvertTo-NginxPath {
    param([string]$Path)
    return ($Path -replace "\\", "/")
}

function Find-Nginx {
    if (-not [string]::IsNullOrWhiteSpace($NginxExe)) {
        if (-not (Test-Path $NginxExe -PathType Leaf)) {
            throw "NginxExe was specified but does not exist: $NginxExe"
        }
        return (Resolve-FullPath $NginxExe)
    }

    return Find-Executable -CommandName "nginx" -Candidates @(
        "C:\nginx\nginx.exe",
        "C:\tools\nginx\nginx.exe",
        "C:\Program Files\nginx*\nginx.exe"
    )
}

function Start-TestNginx {
    param(
        [string]$NginxPath,
        [string]$FrontendDir
    )

    $NginxRoot = Join-Path $DeployRoot "nginx-runtime"
    $NginxConfDir = Join-Path $NginxRoot "conf"
    $NginxLogsDir = Join-Path $NginxRoot "logs"
    New-Item -ItemType Directory -Force -Path $NginxRoot, $NginxConfDir, $NginxLogsDir | Out-Null

    $FrontendRoot = ConvertTo-NginxPath $FrontendDir
    $NginxConf = @"
worker_processes  1;
error_log  logs/error.log;
pid        logs/nginx.pid;

events {
    worker_connections  1024;
}

http {
    default_type  application/octet-stream;
    sendfile      on;

    types {
        text/html html htm;
        text/css css;
        application/javascript js;
        application/json json;
        image/png png;
        image/jpeg jpeg jpg;
        image/gif gif;
        image/svg+xml svg;
        image/x-icon ico;
        application/font-woff woff;
        application/font-woff2 woff2;
    }

    server {
        listen $WebPort;
        server_name localhost;

        location / {
            root "$FrontendRoot";
            try_files `$uri `$uri/ /index.html;
        }

        location /prod-api/ {
            proxy_pass http://127.0.0.1:$ServerPort/prod-api/;
            proxy_set_header Host `$host;
            proxy_set_header X-Real-IP `$remote_addr;
            proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto `$scheme;
        }
    }
}
"@

    Set-Content -LiteralPath (Join-Path $NginxConfDir "nginx.conf") -Value $NginxConf -Encoding ASCII

    $NginxPrefix = $NginxRoot.TrimEnd("\") + "\"
    $PidFile = Join-Path $NginxLogsDir "nginx.pid"
    if (Test-Path $PidFile -PathType Leaf) {
        & $NginxPath -p $NginxPrefix -c "conf\nginx.conf" -s reload
        if ($LASTEXITCODE -ne 0) {
            throw "nginx reload failed with exit code $LASTEXITCODE"
        }
        Write-Host "Reloaded nginx for test frontend."
    }
    else {
        & $NginxPath -p $NginxPrefix -c "conf\nginx.conf"
        if ($LASTEXITCODE -ne 0) {
            throw "nginx start failed with exit code $LASTEXITCODE"
        }
        Write-Host "Started nginx for test frontend."
    }
}

Test-SafeIdentifier -Value $DatabaseName -Name "DatabaseName"
Test-SafeIdentifier -Value $DbUsername -Name "DbUsername"

$PackageRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$BackendJar = Join-Path $PackageRoot "server\aqy-admin.jar"
$FrontendDist = Join-Path $PackageRoot "web"
$UpgradeScript = Join-Path $PSScriptRoot "windows-upgrade.ps1"
$SqlDir = Join-Path $PackageRoot "sql"
$RequiredSqlFiles = @(
    Join-Path $SqlDir "ry_20240629.sql",
    Join-Path $SqlDir "quartz.sql",
    Join-Path $SqlDir "zh_aqy_schema.sql"
)

Write-Step "Checking release package"
if (-not (Test-Path $BackendJar -PathType Leaf)) {
    throw "Packaged backend jar not found: $BackendJar"
}
if (-not (Test-Path (Join-Path $FrontendDist "index.html") -PathType Leaf)) {
    throw "Packaged frontend files not found: $FrontendDist"
}
if (-not (Test-Path $UpgradeScript -PathType Leaf)) {
    throw "Upgrade script not found: $UpgradeScript"
}
foreach ($SqlFile in $RequiredSqlFiles) {
    if (-not (Test-Path $SqlFile -PathType Leaf)) {
        throw "SQL file not found: $SqlFile"
    }
}

$Java = Get-Command java -ErrorAction SilentlyContinue
if ($null -eq $Java) {
    throw "java was not found in PATH. Install JDK/JRE 8 first."
}

if ([string]::IsNullOrWhiteSpace($MysqlCli)) {
    $MysqlCli = Find-Executable -CommandName "mysql" -Candidates @(
        "C:\Program Files\MySQL\MySQL Server *\bin\mysql.exe",
        "C:\Program Files\MariaDB *\bin\mysql.exe",
        "C:\tools\mysql*\bin\mysql.exe"
    )
}
if ([string]::IsNullOrWhiteSpace($MysqlCli)) {
    throw "mysql.exe was not found. Install MySQL client or pass -MysqlCli C:\path\mysql.exe."
}
$MysqlCli = Resolve-FullPath $MysqlCli

if (-not $SkipRedisCheck) {
    Write-Step "Checking Redis"
    if (-not (Test-TcpPort -HostName $RedisHost -Port $RedisPort)) {
        throw "Redis is not reachable at ${RedisHost}:${RedisPort}. Start Redis first, or pass -SkipRedisCheck for a partial backend test."
    }
}

if (-not $SkipDatabaseInit) {
    Write-Step "Preparing MySQL database"
    if ($null -eq $MysqlAdminPassword) {
        $MysqlAdminPassword = Read-Host "Enter MySQL admin password for user '$MysqlAdminUser'" -AsSecureString
    }
    $script:MysqlAdminPasswordText = ConvertFrom-SecureStringPlainText -Value $MysqlAdminPassword

    if ([string]::IsNullOrWhiteSpace($DbPassword)) {
        $DbPassword = New-RandomSecret
    }

    $CreateDatabaseSql = "CREATE DATABASE IF NOT EXISTS ``$DatabaseName`` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
    Invoke-MySql -Sql $CreateDatabaseSql | Out-Null

    $TableCountOutput = Invoke-MySql -Sql "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$DatabaseName';" -Batch
    $TableCount = [int](($TableCountOutput | Select-Object -First 1).ToString().Trim())
    if ($TableCount -gt 0 -and -not $ForceReinitialize -and -not $SkipSqlImport) {
        throw "Database '$DatabaseName' already has $TableCount tables. This fresh-test script will not import initialization SQL into a non-empty database. Use a new database name, pass -SkipSqlImport, or pass -ForceReinitialize if this is disposable test data."
    }

    $EscapedDbPassword = Escape-MySqlString $DbPassword
    $CreateUserSql = @"
CREATE USER IF NOT EXISTS '$DbUsername'@'localhost' IDENTIFIED BY '$EscapedDbPassword';
CREATE USER IF NOT EXISTS '$DbUsername'@'%' IDENTIFIED BY '$EscapedDbPassword';
ALTER USER '$DbUsername'@'localhost' IDENTIFIED BY '$EscapedDbPassword';
ALTER USER '$DbUsername'@'%' IDENTIFIED BY '$EscapedDbPassword';
GRANT ALL PRIVILEGES ON ``$DatabaseName``.* TO '$DbUsername'@'localhost';
GRANT ALL PRIVILEGES ON ``$DatabaseName``.* TO '$DbUsername'@'%';
FLUSH PRIVILEGES;
"@
    Invoke-MySql -Sql $CreateUserSql | Out-Null

    if (-not $SkipSqlImport) {
        foreach ($SqlFile in $RequiredSqlFiles) {
            Import-MySqlFile -Path $SqlFile -Database $DatabaseName
        }
    }
}
elseif ([string]::IsNullOrWhiteSpace($DbPassword)) {
    throw "DbPassword is required when SkipDatabaseInit is specified."
}

if ([string]::IsNullOrWhiteSpace($TokenSecret)) {
    $TokenSecret = New-RandomSecret
}

$DeployRoot = Resolve-FullPath $DeployRoot
$BackendDir = Join-Path $DeployRoot "server"
$FrontendDir = Join-Path $DeployRoot "web"
$UploadDir = Join-Path $DeployRoot "uploadPath"
New-Item -ItemType Directory -Force -Path $DeployRoot, $BackendDir, $FrontendDir, $UploadDir | Out-Null

$DbUrl = "jdbc:mysql://${MysqlHost}:${MysqlPort}/${DatabaseName}?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8"
$RuntimeEnv = @{
    "DB_URL" = $DbUrl
    "DB_USERNAME" = $DbUsername
    "DB_PASSWORD" = $DbPassword
    "DRUID_LOGIN_USERNAME" = "admin"
    "DRUID_LOGIN_PASSWORD" = (New-RandomSecret 18)
    "TOKEN_SECRET" = $TokenSecret
    "SERVER_PORT" = [string]$ServerPort
    "SERVER_CONTEXT_PATH" = "/prod-api"
    "RUOYI_PROFILE" = $UploadDir
    "REDIS_HOST" = $RedisHost
    "REDIS_PORT" = [string]$RedisPort
    "REDIS_DATABASE" = "0"
    "REDIS_PASSWORD" = $RedisPassword
    "MQTT_HOST" = $MqttHost
    "MQTT_USERNAME" = $MqttUsername
    "MQTT_PASSWORD" = $MqttPassword
    "ALIYUN_SMS_ACCESS_KEY_ID" = ""
    "ALIYUN_SMS_ACCESS_KEY_SECRET" = ""
    "ALIYUN_SMS_SIGN_NAME" = ""
    "ALIYUN_SMS_TEMPLATE_CODE" = ""
    "ALIYUN_SMS_COOLDOWN_SECONDS" = "600"
    "FILE_DOMAIN" = "http://127.0.0.1:$WebPort"
    "FILE_UPLOAD_PATH" = $UploadDir
    "FILE_PREFIX" = "/zhbg"
}
Set-RuntimeEnvironment -Values $RuntimeEnv

Write-Step "Deploying packaged backend and frontend"
$UpgradeArgs = @{
    BackendJar = $BackendJar
    FrontendDist = $FrontendDist
    DeployRoot = $DeployRoot
    BackendPort = $ServerPort
    SkipStop = $true
}
if (-not $NoStartBackend) {
    $UpgradeArgs.StartBackend = $true
}
& $UpgradeScript @UpgradeArgs

Write-Step "Writing reusable backend start script"
$RunBackendScript = Join-Path $BackendDir "run-backend.ps1"
$BackendJarPath = Join-Path $BackendDir "aqy-admin.jar"
$EnvLines = foreach ($Item in $RuntimeEnv.GetEnumerator()) {
    '$env:' + $Item.Key + " = '" + ($Item.Value -replace "'", "''") + "'"
}
$RunScriptContent = @"
`$ErrorActionPreference = "Stop"
$($EnvLines -join "`r`n")
Set-Location `$PSScriptRoot
java -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -jar "$BackendJarPath"
"@
Set-Content -LiteralPath $RunBackendScript -Value $RunScriptContent -Encoding UTF8
Write-Host "Backend start script: $RunBackendScript"

if ($PersistUserEnvironment) {
    Write-Host "Runtime variables were persisted to the current Windows user environment."
}
else {
    Write-Host "Runtime variables were set for this script run only. Reuse run-backend.ps1 for later starts."
}

if (-not $SkipNginx) {
    Write-Step "Starting test nginx frontend"
    $ResolvedNginx = Find-Nginx
    if ([string]::IsNullOrWhiteSpace($ResolvedNginx)) {
        Write-Warning "nginx.exe was not found. Backend is deployed, but frontend is not served. Install nginx or rerun with -NginxExe C:\path\nginx.exe."
    }
    else {
        Start-TestNginx -NginxPath $ResolvedNginx -FrontendDir $FrontendDir
    }
}

Write-Step "Fresh Windows test deployment finished"
Write-Host "Backend health: http://127.0.0.1:$ServerPort/prod-api/captchaImage"
Write-Host "Frontend URL   : http://127.0.0.1:$WebPort/"
Write-Host "Deploy root    : $DeployRoot"
Write-Host "Database       : $DatabaseName"
Write-Host "Database user  : $DbUsername"
Write-Host "This script is for fresh test environments. Do not use it against an old production database."
