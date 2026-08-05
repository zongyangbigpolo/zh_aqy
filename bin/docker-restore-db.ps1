[CmdletBinding()]
param(
    [string]$DumpFile
)

. (Join-Path $PSScriptRoot "docker-common.ps1")

Require-Docker
$EnvValues = Load-ExistingDockerEnv

if ([string]::IsNullOrWhiteSpace($DumpFile)) {
    $DumpFile = Read-Host "SQL dump file to restore"
}

if (-not (Test-Path $DumpFile -PathType Leaf)) {
    throw "SQL dump file does not exist: $DumpFile"
}

Write-Host "This will replace Docker database '$($EnvValues['DB_NAME'])' with $DumpFile."
$Confirm = Read-Host "Type RESTORE to continue"
if ($Confirm -ne "RESTORE") {
    throw "Restore cancelled."
}

Invoke-ComposeMigrate -Arguments @("up", "-d", "mysql", "redis")
try {
    Invoke-ComposeMigrate -Arguments @("stop", "backend", "web")
}
catch {
    Write-Host "Backend/web were not running; continuing restore."
}

Reset-TargetDatabase -EnvValues $EnvValues
Write-Host "Importing $DumpFile into Docker MySQL ..."
Invoke-RootMySqlFile -EnvValues $EnvValues -InputFile $DumpFile

Apply-DbMigrations -EnvValues $EnvValues
Invoke-ComposeMigrate -Arguments @("up", "-d", "--build")
Wait-Backend -EnvValues $EnvValues
Write-AccessInfo -EnvValues $EnvValues
Write-Host "Restore finished."
