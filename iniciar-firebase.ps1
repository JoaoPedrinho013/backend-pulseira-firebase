param([string]$Credencial = $env:GOOGLE_APPLICATION_CREDENTIALS)

$ErrorActionPreference = 'Stop'
if (-not $Credencial) {
    $arquivo = Get-ChildItem -Path (Join-Path $env:USERPROFILE 'Downloads\zelo-pulseira-kotlin-firebase-adminsdk-*.json') -File |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
    if (-not $arquivo) { throw 'JSON do Firebase ausente em Downloads. Informe o caminho com -Credencial.' }
    $Credencial = $arquivo.FullName
}

$chave = Get-Item -LiteralPath $Credencial
$config = Get-Content -LiteralPath $chave.FullName -Raw -Encoding UTF8 | ConvertFrom-Json
if ($config.type -ne 'service_account' -or $config.project_id -ne 'zelo-pulseira-kotlin') {
    throw 'Selecione o JSON da conta de servico do projeto zelo-pulseira-kotlin.'
}
$env:GOOGLE_APPLICATION_CREDENTIALS = $chave.FullName
$jar = Join-Path $PSScriptRoot 'target\backend-pulseira-firebase-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path -LiteralPath $jar)) {
    throw 'Executavel ausente. Compile com mvn -DskipTests package na pasta do clone.'
}
& java -jar $jar
exit $LASTEXITCODE
