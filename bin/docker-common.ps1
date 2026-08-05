$ErrorActionPreference = "Stop"

$Script:BinDir = $PSScriptRoot
$Script:RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
$Script:BackupDir = Join-Path $Script:RootDir "backups"

function Invoke-DockerCompose {
    param(
        [string[]]$Files,
        [string[]]$Arguments
    )

    $ComposeArgs = @()
    foreach ($File in $Files) {
        $ComposeArgs += @("-f", $File)
    }
    $ComposeArgs += $Arguments

    Push-Location $Script:RootDir
    try {
        & docker compose version *> $null
        if ($LASTEXITCODE -eq 0) {
            & docker compose @ComposeArgs
        }
        else {
            & docker-compose --version *> $null
            if ($LASTEXITCODE -ne 0) {
                throw "Docker Compose was not found. Install Docker Desktop first."
            }
            & docker-compose @ComposeArgs
        }

        if ($LASTEXITCODE -ne 0) {
            throw "Docker Compose command failed: $($Arguments -join ' ')"
        }
    }
    finally {
        Pop-Location
    }
}

function Invoke-DockerComposeWithInputFile {
    param(
        [string[]]$Files,
        [string[]]$Arguments,
        [string]$InputFile
    )

    $ComposeArgs = @()
    foreach ($File in $Files) {
        $ComposeArgs += @("-f", $File)
    }
    $ComposeArgs += $Arguments

    Push-Location $Script:RootDir
    try {
        & docker compose version *> $null
        if ($LASTEXITCODE -eq 0) {
            Get-Content -LiteralPath $InputFile -Raw | & docker compose @ComposeArgs
        }
        else {
            Get-Content -LiteralPath $InputFile -Raw | & docker-compose @ComposeArgs
        }

        if ($LASTEXITCODE -ne 0) {
            throw "Docker Compose command failed while reading $InputFile"
        }
    }
    finally {
        Pop-Location
    }
}

function Invoke-DockerComposeToOutputFile {
    param(
        [string[]]$Files,
        [string[]]$Arguments,
        [string]$OutputFile
    )

    $ComposeArgs = @()
    foreach ($File in $Files) {
        $ComposeArgs += @("-f", $File)
    }
    $ComposeArgs += $Arguments

    Push-Location $Script:RootDir
    try {
        & docker compose version *> $null
        if ($LASTEXITCODE -eq 0) {
            & docker compose @ComposeArgs > $OutputFile
        }
        else {
            & docker-compose @ComposeArgs > $OutputFile
        }

        if ($LASTEXITCODE -ne 0) {
            throw "Docker Compose command failed while writing $OutputFile"
        }
    }
    finally {
        Pop-Location
    }
}

function Invoke-ComposeBase {
    param([string[]]$Arguments)
    Invoke-DockerCompose -Files @("docker-compose.yml") -Arguments $Arguments
}

function Invoke-ComposeFresh {
    param([string[]]$Arguments)
    Invoke-DockerCompose -Files @("docker-compose.yml", "docker-compose.fresh.yml") -Arguments $Arguments
}

function Invoke-ComposeMigrate {
    param([string[]]$Arguments)
    Invoke-DockerCompose -Files @("docker-compose.yml", "docker-compose.migrate.yml") -Arguments $Arguments
}

function Require-Docker {
    & docker info *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker is not running. Start Docker Desktop first."
    }
}

function Read-DockerEnv {
    $EnvFile = Join-Path $Script:RootDir ".env"
    if (-not (Test-Path $EnvFile -PathType Leaf)) {
        throw ".env was not found. Run docker-generate-env.ps1 first."
    }

    $Values = @{}
    foreach ($RawLine in Get-Content -LiteralPath $EnvFile) {
        $Line = $RawLine.Trim()
        if ($Line -eq "" -or $Line.StartsWith("#")) {
            continue
        }
        $Index = $Line.IndexOf("=")
        if ($Index -lt 1) {
            continue
        }
        $Values[$Line.Substring(0, $Index)] = $Line.Substring($Index + 1)
    }
    return $Values
}

function Ensure-DockerEnv {
    & (Join-Path $Script:BinDir "docker-generate-env.ps1")
    return Read-DockerEnv
}

function Load-ExistingDockerEnv {
    $EnvFile = Join-Path $Script:RootDir ".env"
    if (-not (Test-Path $EnvFile -PathType Leaf)) {
        throw ".env was not found. Start or migrate the Docker deployment first."
    }
    return Read-DockerEnv
}

function ConvertTo-PlainText {
    param([Security.SecureString]$SecureString)
    $Pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureString)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($Pointer)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($Pointer)
    }
}

function Wait-Backend {
    param([hashtable]$EnvValues)

    $BackendPort = if ($EnvValues.ContainsKey("BACKEND_PORT")) { $EnvValues["BACKEND_PORT"] } else { "7070" }
    $Url = "http://127.0.0.1:$BackendPort/prod-api/captchaImage"
    for ($i = 1; $i -le 60; $i++) {
        try {
            Invoke-WebRequest -UseBasicParsing -TimeoutSec 5 -Uri $Url *> $null
            return
        }
        catch {
            Start-Sleep -Seconds 5
        }
    }
    throw "Backend did not become healthy in time: $Url"
}

function Reset-TargetDatabase {
    param([hashtable]$EnvValues)

    $DbName = $EnvValues["DB_NAME"]
    $DbUser = $EnvValues["DB_USERNAME"]
    $RootPassword = $EnvValues["MYSQL_ROOT_PASSWORD"]
    $Sql = "DROP DATABASE IF EXISTS ``$DbName``; CREATE DATABASE ``$DbName`` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; GRANT ALL PRIVILEGES ON ``$DbName``.* TO '$DbUser'@'%'; FLUSH PRIVILEGES;"
    Invoke-ComposeMigrate -Arguments @("exec", "-T", "mysql", "mysql", "-uroot", "-p$RootPassword", "-e", $Sql)
}

function Invoke-MySqlFile {
    param(
        [hashtable]$EnvValues,
        [string]$InputFile
    )

    Invoke-DockerComposeWithInputFile `
        -Files @("docker-compose.yml", "docker-compose.migrate.yml") `
        -Arguments @("exec", "-T", "mysql", "mysql", "-u$($EnvValues['DB_USERNAME'])", "-p$($EnvValues['DB_PASSWORD'])", $EnvValues["DB_NAME"]) `
        -InputFile $InputFile
}

function Invoke-RootMySqlFile {
    param(
        [hashtable]$EnvValues,
        [string]$InputFile
    )

    Invoke-DockerComposeWithInputFile `
        -Files @("docker-compose.yml", "docker-compose.migrate.yml") `
        -Arguments @("exec", "-T", "mysql", "mysql", "-uroot", "-p$($EnvValues['MYSQL_ROOT_PASSWORD'])", $EnvValues["DB_NAME"]) `
        -InputFile $InputFile
}

function Apply-DbMigrations {
    param([hashtable]$EnvValues)

    $MigrationsDir = Join-Path $Script:RootDir "sql\migrations"
    if (-not (Test-Path $MigrationsDir -PathType Container)) {
        Write-Host "No sql\migrations directory found; skipping database migrations."
        return
    }

    $Files = Get-ChildItem -LiteralPath $MigrationsDir -Filter "*.sql" -File | Sort-Object Name
    if ($Files.Count -eq 0) {
        Write-Host "No SQL migration files found; skipping database migrations."
        return
    }

    foreach ($File in $Files) {
        Write-Host "Applying migration: $($File.Name)"
        Invoke-MySqlFile -EnvValues $EnvValues -InputFile $File.FullName
    }
}

function Get-DefaultOldDbHost {
    return "host.docker.internal"
}

function Write-AccessInfo {
    param([hashtable]$EnvValues)

    $WebPort = if ($EnvValues.ContainsKey("WEB_PORT")) { $EnvValues["WEB_PORT"] } else { "8080" }
    $BackendPort = if ($EnvValues.ContainsKey("BACKEND_PORT")) { $EnvValues["BACKEND_PORT"] } else { "7070" }
    Write-Host ""
    Write-Host "Docker deployment is running."
    Write-Host "Frontend: http://127.0.0.1:$WebPort/"
    Write-Host "Backend : http://127.0.0.1:$BackendPort/prod-api/captchaImage"
}
