[CmdletBinding()]
param()

. (Join-Path $PSScriptRoot "docker-common.ps1")

Require-Docker
$EnvValues = Load-ExistingDockerEnv
New-Item -ItemType Directory -Force -Path $Script:BackupDir | Out-Null

Invoke-ComposeMigrate -Arguments @("up", "-d", "mysql")

$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$DumpFile = Join-Path $Script:BackupDir "docker-$($EnvValues['DB_NAME'])-$Timestamp.sql"

Write-Host "Backing up Docker MySQL database to $DumpFile ..."
Invoke-DockerComposeToOutputFile `
    -Files @("docker-compose.yml", "docker-compose.migrate.yml") `
    -Arguments @("exec", "-T", "mysql", "mysqldump", "-u$($EnvValues['DB_USERNAME'])", "-p$($EnvValues['DB_PASSWORD'])", "--single-transaction", "--routines", "--triggers", "--events", "--default-character-set=utf8mb4", $EnvValues["DB_NAME"]) `
    -OutputFile $DumpFile

Write-Host "Backup finished: $DumpFile"
