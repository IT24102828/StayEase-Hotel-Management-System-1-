$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
$env:CATALINA_HOME = "C:\Tools\apache-tomcat-10.1.61"
$env:CATALINA_BASE = $env:CATALINA_HOME

$projectUrl = "http://localhost:8080/stayease/"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    StayEase Development Server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Building StayEase..." -ForegroundColor Yellow

mvn clean package

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build Failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Build Successful!" -ForegroundColor Green

Copy-Item ".\target\stayease.war" "$env:CATALINA_HOME\webapps\stayease.war" -Force

Write-Host ""
Write-Host "Starting Apache Tomcat..." -ForegroundColor Yellow
Write-Host ""

# Start Tomcat in a separate process
Start-Process -FilePath "$env:CATALINA_HOME\bin\catalina.bat" `
    -ArgumentList "run" `
    -WorkingDirectory "$env:CATALINA_HOME"

# Wait until the website responds
$ready = $false

for ($i = 0; $i -lt 60; $i++) {
    try {
        $response = Invoke-WebRequest -Uri $projectUrl `
            -UseBasicParsing -TimeoutSec 2

        if ($response.StatusCode -eq 200) {
            $ready = $true
            break
        }
    }
    catch {
        Start-Sleep -Seconds 2
    }
}

if ($ready) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  StayEase is Running!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Local: $projectUrl" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Ctrl + Click the link to open website." -ForegroundColor Yellow
}
else {
    Write-Host "Server not ready. Check Tomcat logs." -ForegroundColor Red
    exit 1
}