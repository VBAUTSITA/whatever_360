param(
    [ValidateSet('inspect','setup','verify','serve','all')][string]$Mode = 'all',
    [string]$OracleJdbcJar = 'C:\app\zuoti\product\26ai\dbhomeFree\jdbc\lib\ojdbc8.jar',
    [string]$JavaHome = 'C:\Users\zuoti\AppData\Local\Programs\WebTrackerTools\java\jdk8u504-b01',
    [switch]$KeepCredentialForSession
)
$ErrorActionPreference = 'Stop'
$auditRoot = Split-Path $PSScriptRoot -Parent
Set-Location -LiteralPath $auditRoot
$auditCpFile = Join-Path $auditRoot 'target\test-classpath.txt'
$auditClass = Join-Path $auditRoot 'target\test-classes\com\tracker\web\auditoriared\OracleIntegrationHarness.class'
if (!(Test-Path -LiteralPath $auditCpFile) -or !(Test-Path -LiteralPath $auditClass)) {
    throw 'Compile primero: mvnw.cmd test-compile dependency:build-classpath -Dmdep.outputFile=target/test-classpath.txt'
}
if (!(Test-Path -LiteralPath $OracleJdbcJar)) { throw 'No existe el driver Oracle JDBC indicado.' }

# El secreto existe solamente en memoria y en stdin privado del proceso Java.
# La ventana solicita entrada directa; nunca se pide pegar la contraseña en el chat.
Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing
$auditForm = New-Object Windows.Forms.Form
$auditForm.Text = 'Auditoría Red - Oracle local'
$auditForm.Size = New-Object Drawing.Size(470,225)
$auditForm.StartPosition = 'CenterScreen'
$auditForm.FormBorderStyle = 'FixedDialog'
$auditForm.MaximizeBox = $false
$auditForm.MinimizeBox = $false
$auditForm.TopMost = $true
$auditLabel = New-Object Windows.Forms.Label
$auditLabel.Location = New-Object Drawing.Point(20,18)
$auditLabel.Size = New-Object Drawing.Size(420,58)
$auditLabel.Text = "USERTEST @ localhost:1521/FREEPDB1`r`nContraseña para las pruebas locales de Auditoría Red.`r`nSe usará en memoria; no se guardará."
$auditPassword = New-Object Windows.Forms.TextBox
$auditPassword.Location = New-Object Drawing.Point(20,82)
$auditPassword.Size = New-Object Drawing.Size(415,25)
$auditPassword.UseSystemPasswordChar = $true
$auditOk = New-Object Windows.Forms.Button
$auditOk.Text = 'Conectar'
$auditOk.Location = New-Object Drawing.Point(230,128)
$auditOk.DialogResult = [Windows.Forms.DialogResult]::OK
$auditCancel = New-Object Windows.Forms.Button
$auditCancel.Text = 'Cancelar'
$auditCancel.Location = New-Object Drawing.Point(330,128)
$auditCancel.DialogResult = [Windows.Forms.DialogResult]::Cancel
$auditForm.Controls.AddRange(@($auditLabel,$auditPassword,$auditOk,$auditCancel))
$auditForm.AcceptButton = $auditOk
$auditForm.CancelButton = $auditCancel
$auditForm.Add_Shown({ $auditPassword.Focus() })
if ($auditForm.ShowDialog() -ne [Windows.Forms.DialogResult]::OK) { $auditForm.Dispose(); exit 2 }
if ([string]::IsNullOrEmpty($auditPassword.Text)) { $auditForm.Dispose(); throw 'No se ingresó una contraseña.' }
$auditSecret = ConvertTo-SecureString -String $auditPassword.Text -AsPlainText -Force
$auditPassword.Clear()
$auditForm.Dispose()

$auditLogDir = Join-Path $auditRoot 'target\oracle'
New-Item -ItemType Directory -Force -Path $auditLogDir | Out-Null
$auditCp = (Join-Path $auditRoot 'target\test-classes') + ';' + (Join-Path $auditRoot 'target\classes') + ';' + [IO.File]::ReadAllText($auditCpFile).Trim() + ';' + $OracleJdbcJar
try { do {
$auditStart = New-Object Diagnostics.ProcessStartInfo
$auditStart.FileName = Join-Path $JavaHome 'bin\java.exe'
$auditStart.Arguments = '-Dfile.encoding=UTF-8 -cp "' + $auditCp + '" com.tracker.web.auditoriared.OracleIntegrationHarness ' + $Mode + ' --password-stdin'
$auditStart.WorkingDirectory = $auditRoot
$auditStart.UseShellExecute = $false
$auditStart.CreateNoWindow = $true
$auditStart.RedirectStandardInput = $true
$auditStart.RedirectStandardOutput = $true
$auditStart.RedirectStandardError = $true
$auditProcess = New-Object Diagnostics.Process
$auditProcess.StartInfo = $auditStart
try {
    [void]$auditProcess.Start()
    $auditSecretPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($auditSecret)
    try { $auditProcess.StandardInput.WriteLine([Runtime.InteropServices.Marshal]::PtrToStringBSTR($auditSecretPtr)) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($auditSecretPtr) }
    $auditProcess.StandardInput.Close()
} catch { throw }
@{launcherPid=$PID; javaPid=$auditProcess.Id; mode=$Mode; address='127.0.0.1'} | ConvertTo-Json | Set-Content -Encoding UTF8 (Join-Path $auditLogDir 'process.json')
$auditErrors = $auditProcess.StandardError.ReadToEndAsync()
$auditWriter = New-Object IO.StreamWriter((Join-Path $auditLogDir ($Mode + '.log')), $false, (New-Object Text.UTF8Encoding($false)))
$auditWriter.AutoFlush = $true
try {
    while (($auditLine = $auditProcess.StandardOutput.ReadLine()) -ne $null) { $auditWriter.WriteLine($auditLine) }
    $auditProcess.WaitForExit()
    $auditWriter.Write($auditErrors.Result)
} finally { $auditWriter.Dispose() }
@{exitCode=$auditProcess.ExitCode; mode=$Mode} | ConvertTo-Json | Set-Content -Encoding UTF8 (Join-Path $auditLogDir 'exit.json')
$auditExitCode = $auditProcess.ExitCode
if (!$KeepCredentialForSession) { break }
# Puede reiniciarse el harness después de recompilar sin guardar ni volver a pedir la clave.
# El archivo de control sólo admite uno de los modos de prueba enumerados arriba.
$auditRestart = Join-Path $auditLogDir 'restart.request'
$auditStop = Join-Path $auditLogDir 'stop.request'
$auditDeadline = [DateTime]::UtcNow.AddHours(2)
while (!(Test-Path -LiteralPath $auditRestart) -and !(Test-Path -LiteralPath $auditStop) -and [DateTime]::UtcNow -lt $auditDeadline) { Start-Sleep -Seconds 1 }
if ((Test-Path -LiteralPath $auditStop) -or !(Test-Path -LiteralPath $auditRestart)) { break }
$auditNextMode = [IO.File]::ReadAllText($auditRestart).Trim()
Remove-Item -LiteralPath $auditRestart
if ($auditNextMode -notin @('inspect','setup','verify','serve','all')) { throw 'Modo de reinicio inválido.' }
$Mode = $auditNextMode
} while ($KeepCredentialForSession)
} finally { $auditSecret.Dispose() }
exit $auditExitCode
