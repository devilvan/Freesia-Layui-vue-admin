$ErrorActionPreference = 'Stop'

$userProfile = [Environment]::GetFolderPath('UserProfile')
$codexHome = if ($env:CODEX_HOME) { $env:CODEX_HOME } else { Join-Path $userProfile '.codex' }
$skillDir = Join-Path $codexHome 'skills\image-to-pindou'
$installer = Join-Path $codexHome 'skills\.system\skill-installer\scripts\install-skill-from-github.py'

if (-not (Test-Path -LiteralPath $skillDir)) {
    if (-not (Test-Path -LiteralPath $installer)) {
        throw "skill-installer not found at $installer"
    }
    python $installer --repo kuizuo/pin-dou --path skills/image-to-pindou
}

if (-not (Test-Path -LiteralPath $skillDir)) {
    throw "image-to-pindou skill directory not found at $skillDir"
}

Push-Location $skillDir
try {
    $sharpModule = Join-Path $skillDir 'node_modules\sharp'
    if (-not (Test-Path -LiteralPath $sharpModule)) {
        & npm.cmd install
    }
    node scripts/self-test.mjs
}
finally {
    Pop-Location
}

Write-Host "image-to-pindou skill is installed and verified at $skillDir"
