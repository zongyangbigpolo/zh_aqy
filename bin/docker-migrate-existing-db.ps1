[CmdletBinding()]
param()

. (Join-Path $PSScriptRoot "docker-common.ps1")

function Read-WithDefault {
    param(
        [string]$Prompt,
        [string]$DefaultValue
    )

    $Value = Read-Host "$Prompt [$DefaultValue]"
    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $DefaultValue
    }
    return $Value
}

Require-Docker
$EnvValues = Ensure-DockerEnv
New-Item -ItemType Directory -Force -Path $Script:BackupDir | Out-Null

$OldHost = if ($env:OLD_DB_HOST) { $env:OLD_DB_HOST } else { Read-WithDefault "Existing MySQL host" (Get-DefaultOldDbHost) }
$OldPort = if ($env:OLD_DB_PORT) { $env:OLD_DB_PORT } else { Read-WithDefault "Existing MySQL port" "3306" }
$OldName = if ($env:OLD_DB_NAME) { $env:OLD_DB_NAME } else { Read-WithDefault "Existing MySQL database name" $EnvValues["DB_NAME"] }
$OldUser = if ($env:OLD_DB_USERNAME) { $env:OLD_DB_USERNAME } else { Read-WithDefault "Existing MySQL username" "root" }

if ($env:OLD_DB_PASSWORD) {
    $OldPassword = $env:OLD_DB_PASSWORD
}
else {
    $OldPassword = ConvertTo-PlainText (Read-Host "Existing MySQL password" -AsSecureString)
}

Write-Host ""
Write-Host "This will import '$OldName' from ${OldHost}:$OldPort into Docker database '$($EnvValues['DB_NAME'])'."
Write-Host "The Docker target database will be dropped and recreated before import."
$Confirm = Read-Host "Type MIGRATE to continue"
if ($Confirm -ne "MIGRATE") {
    throw "Migration cancelled."
}

Invoke-ComposeMigrate -Arguments @("up", "-d", "mysql", "redis")

$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$DumpFile = Join-Path $Script:BackupDir "existing-$OldName-$Timestamp.sql"

Write-Host "Dumping existing database to $DumpFile ..."
$DumpArgs = @(
    "run", "--rm",
    "-e", "MYSQL_PWD=$OldPassword",
    "mysql:8.0",
    "mysqldump",
    "--host=$OldHost",
    "--port=$OldPort",
    "--user=$OldUser",
    "--single-transaction",
    "--routines",
    "--triggers",
    "--events",
    "--default-character-set=utf8mb4",
    "--column-statistics=0",
    "--set-gtid-purged=OFF",
    $OldName
)
& docker @DumpArgs > $DumpFile
if ($LASTEXITCODE -ne 0) {
    throw "Failed to dump existing database."
}
if ((Get-Item -LiteralPath $DumpFile).Length -le 0) {
    throw "Database dump is empty: $DumpFile"
}

Write-Host "Resetting Docker target database ..."
Reset-TargetDatabase -EnvValues $EnvValues

Write-Host "Importing dump into Docker MySQL ..."
Invoke-RootMySqlFile -EnvValues $EnvValues -InputFile $DumpFile

Apply-DbMigrations -EnvValues $EnvValues

Invoke-ComposeMigrate -Arguments @("up", "-d", "--build")
Wait-Backend -EnvValues $EnvValues
Write-AccessInfo -EnvValues $EnvValues

Write-Host "Migration finished. Backup dump kept at: $DumpFile"
