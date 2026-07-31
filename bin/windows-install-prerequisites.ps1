<#
.SYNOPSIS
Installs or guides installation of Zh_AqY Windows prerequisites.

.DESCRIPTION
Attempts to install Java 8, MySQL, Redis, and optionally Nginx using winget or
Chocolatey. Some installers may still require interactive choices, especially
MySQL root password and Windows service setup.
#>

[CmdletBinding()]
param(
    [switch]$IncludeNginx,
    [switch]$UseChocolatey,
    [switch]$SkipJava,
    [switch]$SkipMySql,
    [switch]$SkipRedis,
    [switch]$DryRun,
    [switch]$RunPreflight
)

$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Test-IsAdministrator {
    $Identity = [Security.Principal.WindowsIdentity]::GetCurrent()
    $Principal = New-Object Security.Principal.WindowsPrincipal($Identity)
    return $Principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
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

function Test-Java8Installed {
    $Java = Find-Executable -CommandName "java" -Candidates @(
        "C:\Program Files\Eclipse Adoptium\jdk-8*\bin\java.exe",
        "C:\Program Files\Java\jdk1.8*\bin\java.exe",
        "C:\Program Files\Java\jre1.8*\bin\java.exe"
    )
    if ($null -eq $Java) {
        return $false
    }

    $Version = (& $Java -version 2>&1 | Out-String)
    return ($Version -match 'version "1\.8\.' -or $Version -match 'version "8\.')
}

function Test-MySqlInstalled {
    $Mysql = Find-Executable -CommandName "mysql" -Candidates @(
        "C:\Program Files\MySQL\MySQL Server *\bin\mysql.exe",
        "C:\Program Files\MariaDB *\bin\mysql.exe",
        "C:\tools\mysql*\bin\mysql.exe"
    )
    return ($null -ne $Mysql)
}

function Test-RedisInstalled {
    $RedisServer = Find-Executable -CommandName "redis-server" -Candidates @(
        "C:\Program Files\Redis\redis-server.exe",
        "C:\tools\redis*\redis-server.exe"
    )
    $RedisService = Get-Service -Name "*redis*" -ErrorAction SilentlyContinue
    return ($null -ne $RedisServer -or $null -ne $RedisService)
}

function Test-NginxInstalled {
    $Nginx = Find-Executable -CommandName "nginx" -Candidates @(
        "C:\nginx\nginx.exe",
        "C:\tools\nginx\nginx.exe",
        "C:\Program Files\nginx*\nginx.exe"
    )
    return ($null -ne $Nginx)
}

function Invoke-WingetInstall {
    param(
        [string]$PackageId,
        [string]$Name
    )

    $Args = @(
        "install",
        "--exact",
        "--id", $PackageId,
        "--accept-package-agreements",
        "--accept-source-agreements"
    )

    if ($DryRun) {
        Write-Host "DRY RUN: winget $($Args -join ' ')"
        return
    }

    Write-Host "Installing $Name with winget package: $PackageId"
    & winget @Args
    if ($LASTEXITCODE -ne 0) {
        throw "winget failed to install $Name with package $PackageId"
    }
}

function Invoke-ChocoInstall {
    param(
        [string]$PackageId,
        [string]$Name
    )

    $Args = @("install", $PackageId, "-y")
    if ($DryRun) {
        Write-Host "DRY RUN: choco $($Args -join ' ')"
        return
    }

    Write-Host "Installing $Name with Chocolatey package: $PackageId"
    & choco @Args
    if ($LASTEXITCODE -ne 0) {
        throw "Chocolatey failed to install $Name with package $PackageId"
    }
}

function Install-PackageIfMissing {
    param(
        [string]$Name,
        [scriptblock]$InstalledCheck,
        [string]$WingetPackage,
        [string]$ChocoPackage
    )

    if (& $InstalledCheck) {
        Write-Host "[OK] $Name already installed"
        return
    }

    if ($UseChocolatey) {
        if ($null -eq (Get-Command choco -ErrorAction SilentlyContinue)) {
            throw "Chocolatey was requested but choco was not found. Install Chocolatey first or rerun without -UseChocolatey."
        }
        Invoke-ChocoInstall -PackageId $ChocoPackage -Name $Name
        return
    }

    if ($null -ne (Get-Command winget -ErrorAction SilentlyContinue)) {
        Invoke-WingetInstall -PackageId $WingetPackage -Name $Name
        return
    }

    if ($null -ne (Get-Command choco -ErrorAction SilentlyContinue)) {
        Invoke-ChocoInstall -PackageId $ChocoPackage -Name $Name
        return
    }

    throw "Neither winget nor Chocolatey was found. Install prerequisites manually."
}

Write-Host "============================================================"
Write-Host "Zh_AqY Windows prerequisite installer"
Write-Host "============================================================"
Write-Host "Required: Java 8, MySQL, Redis"
Write-Host "Optional: Nginx"
Write-Host ""

if (-not (Test-IsAdministrator)) {
    Write-Warning "This PowerShell window is not running as Administrator. Installers may ask for elevation or fail."
}

try {
    if (-not $SkipJava) {
        Write-Step "Java 8"
        Install-PackageIfMissing `
            -Name "Java 8 JDK" `
            -InstalledCheck { Test-Java8Installed } `
            -WingetPackage "EclipseAdoptium.Temurin.8.JDK" `
            -ChocoPackage "temurin8"
    }

    if (-not $SkipMySql) {
        Write-Step "MySQL"
        Install-PackageIfMissing `
            -Name "MySQL Server/Client" `
            -InstalledCheck { Test-MySqlInstalled } `
            -WingetPackage "Oracle.MySQL" `
            -ChocoPackage "mysql"
    }

    if (-not $SkipRedis) {
        Write-Step "Redis"
        Install-PackageIfMissing `
            -Name "Redis for Windows" `
            -InstalledCheck { Test-RedisInstalled } `
            -WingetPackage "tporadowski.Redis" `
            -ChocoPackage "redis-64"
    }

    if ($IncludeNginx) {
        Write-Step "Nginx"
        Install-PackageIfMissing `
            -Name "Nginx" `
            -InstalledCheck { Test-NginxInstalled } `
            -WingetPackage "Nginx.Nginx" `
            -ChocoPackage "nginx"
    }
}
catch {
    Write-Host ""
    Write-Host "Automatic installation could not continue:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Manual download links:"
    Write-Host "- Java 8: https://adoptium.net/temurin/releases/?version=8"
    Write-Host "- MySQL:  https://dev.mysql.com/downloads/installer/"
    Write-Host "- Redis:  https://github.com/tporadowski/redis/releases"
    Write-Host "- Nginx:  https://nginx.org/en/download.html"
    exit 1
}

Write-Step "Next steps"
Write-Host "1. Close and reopen PowerShell/CMD so PATH changes take effect."
Write-Host "2. Make sure the MySQL service is running and you know the root password."
Write-Host "3. Make sure Redis is running on 127.0.0.1:6379, or configure REDIS_HOST/REDIS_PORT."
Write-Host "4. Run bin\RUN-CHECK-WINDOWS-ENV.bat to confirm the machine is ready."
Write-Host "5. For a fresh test machine, run bin\RUN-FRESH-WINDOWS-TEST.bat."
Write-Host "6. For an old server upgrade, run bin\RUN-UPGRADE-EXISTING-WINDOWS.bat."

if ($RunPreflight) {
    $Preflight = Join-Path $PSScriptRoot "windows-preflight-check.ps1"
    if (Test-Path $Preflight -PathType Leaf) {
        Write-Step "Running preflight"
        & $Preflight
    }
}

