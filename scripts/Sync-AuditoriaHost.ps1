param(
    [string]$HostRoot,
    [Parameter(Mandatory=$true)][string]$BackupRoot
)
$ErrorActionPreference = 'Stop'
$auditSourceRoot = (Resolve-Path (Split-Path $PSScriptRoot -Parent)).Path
if ([string]::IsNullOrWhiteSpace($HostRoot)) { $HostRoot = Join-Path (Split-Path $auditSourceRoot -Parent) 'web-tracker' }
$auditHostRoot = (Resolve-Path -LiteralPath $HostRoot).Path
$auditBackupRoot = [IO.Path]::GetFullPath($BackupRoot)
if ($auditHostRoot -eq $auditSourceRoot -or $auditBackupRoot.StartsWith($auditHostRoot + '\',[StringComparison]::OrdinalIgnoreCase)) {
    throw 'El respaldo debe estar fuera de la aplicación; fuente y destino deben ser distintos.'
}
if (Test-Path -LiteralPath (Join-Path $auditBackupRoot 'applied-files.json')) { throw 'Use un directorio nuevo de respaldo.' }
$auditRelativeRoots = @(
    'src\main\java\com\tracker\web\auditoriared',
    'src\main\resources\scripts\auditoriared',
    'src\main\resources\templates\auditoriared',
    'src\main\resources\static\images\auditoriared',
    'src\test\java\com\tracker\web\auditoriared',
    'src\test\resources\oracle'
)
$auditFiles = @()
foreach ($auditRelativeRoot in $auditRelativeRoots) {
    $auditDirectory = Join-Path $auditSourceRoot $auditRelativeRoot
    if (Test-Path -LiteralPath $auditDirectory) { $auditFiles += Get-ChildItem -LiteralPath $auditDirectory -File -Recurse }
}
$auditFiles += Get-Item -LiteralPath (Join-Path $auditSourceRoot 'src\main\resources\templates\layouts\base_auditoriared.html')
$auditFiles += Get-ChildItem -LiteralPath (Join-Path $auditSourceRoot 'src\main\resources\static\css') -File -Filter 'auditoriared*.css'
$auditFiles += Get-ChildItem -LiteralPath (Join-Path $auditSourceRoot 'src\main\resources\static\js') -File -Filter 'auditoriared*.js'
# Browser tests stay with the module; avoid overwriting unrelated host browser tooling.
New-Item -ItemType Directory -Force -Path $auditBackupRoot | Out-Null
$auditManifest = @()
foreach ($auditFile in ($auditFiles | Sort-Object FullName -Unique)) {
    $auditRelative = $auditFile.FullName.Substring($auditSourceRoot.Length + 1)
    $auditDestination = [IO.Path]::GetFullPath((Join-Path $auditHostRoot $auditRelative))
    if (!$auditDestination.StartsWith($auditHostRoot + '\',[StringComparison]::OrdinalIgnoreCase)) { throw 'Destino fuera del host.' }
    $auditAfter = (Get-FileHash -LiteralPath $auditFile.FullName -Algorithm SHA256).Hash
    $auditBefore = $null
    if (Test-Path -LiteralPath $auditDestination) {
        $auditBefore = (Get-FileHash -LiteralPath $auditDestination -Algorithm SHA256).Hash
        if ($auditBefore -ne $auditAfter) {
            $auditBackupFile = Join-Path (Join-Path $auditBackupRoot 'before') $auditRelative
            New-Item -ItemType Directory -Force -Path (Split-Path $auditBackupFile -Parent) | Out-Null
            Copy-Item -LiteralPath $auditDestination -Destination $auditBackupFile
            if ((Get-FileHash -LiteralPath $auditBackupFile -Algorithm SHA256).Hash -ne $auditBefore) { throw 'El respaldo no coincide.' }
        }
    }
    if ($auditBefore -ne $auditAfter) {
        New-Item -ItemType Directory -Force -Path (Split-Path $auditDestination -Parent) | Out-Null
        Copy-Item -LiteralPath $auditFile.FullName -Destination $auditDestination
    }
    if ((Get-FileHash -LiteralPath $auditDestination -Algorithm SHA256).Hash -ne $auditAfter) { throw 'La copia no coincide.' }
    $auditManifest += [pscustomobject]@{path=$auditRelative; before=$auditBefore; after=$auditAfter; changed=($auditBefore -ne $auditAfter)}
    # Persist after every file: a partial failure remains reviewable and recoverable.
    $auditManifest | ConvertTo-Json -Depth 4 | Set-Content -Encoding UTF8 (Join-Path $auditBackupRoot 'applied-files.json')
}
Write-Output "Verified $($auditManifest.Count) audit files; $(@($auditManifest | Where-Object changed).Count) changed. Backup: $auditBackupRoot"
