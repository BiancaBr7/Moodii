# Deploy Spring Boot + (optional) ML API to Azure Container Instances (ACI)
# Usage:
#   PowerShell:  ./deploy-aci.ps1 -ResourceGroup moodii-rg -Location eastus -DeployMlApi $true
# Idempotent: safe to re-run; will replace existing containers of same name.
param(
  [string]$ResourceGroup = "moodii-rg",
  [string]$Location = "eastus",
  [switch]$DeployMlApi,
  [string]$MongoUser = "biancabr7",
  [SecureString]$MongoPasswordSecure,
  [string]$MongoPasswordPlain, # fallback if secure not provided
  [string]$MongoHost = "moodii-cluster.qbiqko9.mongodb.net",
  [string]$MongoDatabase = "moodii"
)

$ErrorActionPreference = 'Stop'

Write-Host "=== Moodii ACI Deployment (Spring Boot) ===" -ForegroundColor Cyan

# 1. Validate prerequisites
function Test-Command($name){ if(-not (Get-Command $name -ErrorAction SilentlyContinue)){ throw "Required command '$name' not found in PATH" } }
Test-Command az
Test-Command docker
Test-Command mvn

# 2. Ensure resource group exists
if(-not (az group show -n $ResourceGroup -o none 2>$null)){
  Write-Host "Creating resource group $ResourceGroup in $Location" -ForegroundColor Yellow
  az group create -n $ResourceGroup -l $Location | Out-Null
}

# 3. Build Spring Boot jar (local) to verify it runs before container build
Write-Host "Building Spring Boot jar locally (skip tests)" -ForegroundColor Yellow
Push-Location backend/moodii
mvn -q clean package -DskipTests
if($LASTEXITCODE -ne 0){ throw "Maven build failed" }
Pop-Location

# 4. Create (or reuse) Azure Container Registry
$AcrBaseName = "moodiiacr"
$ExistingAcr = az acr list --resource-group $ResourceGroup --query "[?contains(name,'$AcrBaseName')].name | [0]" -o tsv
if([string]::IsNullOrWhiteSpace($ExistingAcr)){
  $AcrName = "$AcrBaseName$((Get-Random -Minimum 1000 -Maximum 9999))"
  Write-Host "Creating ACR: $AcrName" -ForegroundColor Yellow
  az acr create -g $ResourceGroup -n $AcrName --sku Basic --admin-enabled true | Out-Null
}else{
  $AcrName = $ExistingAcr
  Write-Host "Reusing existing ACR: $AcrName" -ForegroundColor Green
}
$AcrLoginServer = az acr show -n $AcrName --query loginServer -o tsv

# 5. Build & push image using existing Dockerfile
$ImageTag = "$AcrLoginServer/moodii-backend:latest"
Write-Host "Logging into ACR and building image $ImageTag" -ForegroundColor Yellow
az acr login -n $AcrName | Out-Null
Push-Location backend/moodii
docker build -t $ImageTag .
if($LASTEXITCODE -ne 0){ throw "Docker build failed" }
docker push $ImageTag
if($LASTEXITCODE -ne 0){ throw "Docker push failed" }
Pop-Location

# 6. Prepare environment variables
if(-not $MongoPasswordPlain -and $MongoPasswordSecure){
  $MongoPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
      [Runtime.InteropServices.Marshal]::SecureStringToBSTR($MongoPasswordSecure))
}
if(-not $MongoPasswordPlain){ throw "Mongo password not provided. Use -MongoPasswordPlain or -MongoPasswordSecure." }

$escapedUser = [System.Uri]::EscapeDataString($MongoUser)
$escapedPass = [System.Uri]::EscapeDataString($MongoPasswordPlain)
$MongoUri = "mongodb+srv://${escapedUser}:${escapedPass}@$MongoHost/$MongoDatabase?retryWrites=true&w=majority&appName=moodii-cluster"

Write-Host "Mongo URI (masked user/pass): mongodb+srv://***:***@$MongoHost/$MongoDatabase" -ForegroundColor DarkGray

# 7. Remove any existing container group with same name
$BackendCg = "moodii-backend"
az container delete -g $ResourceGroup -n $BackendCg --yes 2>$null | Out-Null

# 8. Deploy backend container
Write-Host "Deploying backend container group $BackendCg" -ForegroundColor Yellow
$AcrUser = az acr credential show -n $AcrName --query username -o tsv
$AcrPass = az acr credential show -n $AcrName --query "passwords[0].value" -o tsv

az container create `
  -g $ResourceGroup `
  -n $BackendCg `
  --image $ImageTag `
  --registry-login-server $AcrLoginServer `
  --registry-username $AcrUser `
  --registry-password $AcrPass `
  --dns-name-label "$BackendCg-$((Get-Random -Minimum 1000 -Maximum 9999))" `
  --ports 8080 `
  --cpu 2 `
  --memory 3 `
  --restart-policy Always `
  --environment-variables `
  MONGODB_URI="$MongoUri" `
     SPRING_PROFILES_ACTIVE="prod" `
     ML_API_URL="http://localhost:5000" | Out-Null

Write-Host "Waiting ~90s for Spring Boot startup" -ForegroundColor Yellow
Start-Sleep 90

$BackendFqdn = az container show -g $ResourceGroup -n $BackendCg --query "ipAddress.fqdn" -o tsv
$BackendState = az container show -g $ResourceGroup -n $BackendCg --query "instanceView.state" -o tsv
Write-Host "Backend state: $BackendState  FQDN: $BackendFqdn" -ForegroundColor Cyan

# 9. Quick health probe
$HealthUrl = "http://$BackendFqdn:8080/actuator/health"
try {
  $resp = Invoke-RestMethod -Uri $HealthUrl -TimeoutSec 15
  Write-Host "Health endpoint status: $($resp.status)" -ForegroundColor Green
} catch {
  Write-Host "Health endpoint not ready yet. Fetching last 40 log lines:" -ForegroundColor Yellow
  az container logs -g $ResourceGroup -n $BackendCg --tail 40
}

# 10. Optional ML API deployment (simple Flask mock)
if($DeployMlApi){
  $MlCg = "moodii-ml"
  az container delete -g $ResourceGroup -n $MlCg --yes 2>$null | Out-Null
  Write-Host "Deploying ML mock API $MlCg" -ForegroundColor Yellow
  $mlCode = @'
from flask import Flask, jsonify, request
import random, datetime
app = Flask(__name__)
@app.get('/health')
def health():
    return jsonify(status='healthy', service='ml-api', ts=datetime.datetime.utcnow().isoformat())
@app.post('/predict')
def predict():
    emotions = ['happy','sad','angry','neutral','fear','surprise','disgust']
    emotion = random.choice(emotions)
    return jsonify(emotion=emotion, confidence=round(random.uniform(0.7,0.95),2))
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
'@
  $mlB64 = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($mlCode))
  az container create `
     -g $ResourceGroup `
     -n $MlCg `
     --image python:3.11-slim `
     --dns-name-label "$MlCg-$((Get-Random -Minimum 1000 -Maximum 9999))" `
     --ports 5000 `
     --cpu 1 `
     --memory 1.5 `
     --restart-policy Always `
     --environment-variables PORT=5000 `
     --command-line "bash -c 'pip install --no-cache-dir flask && echo $mlB64 | base64 -d > app.py && python app.py'" | Out-Null
  Start-Sleep 25
  $MlFqdn = az container show -g $ResourceGroup -n $MlCg --query "ipAddress.fqdn" -o tsv
  Write-Host "ML API FQDN: $MlFqdn" -ForegroundColor Cyan
  try { Invoke-RestMethod -Uri "http://$MlFqdn:5000/health" -TimeoutSec 10 | Out-Null; Write-Host "ML API healthy" -ForegroundColor Green } catch { Write-Host "ML API not ready" -ForegroundColor Yellow }
}

# 11. Output summary
Write-Host "=== Deployment Summary ===" -ForegroundColor Cyan
Write-Host "Backend: http://$BackendFqdn:8080" -ForegroundColor White
Write-Host "Health:  $HealthUrl" -ForegroundColor White
if($DeployMlApi -and $MlFqdn){ Write-Host "ML API:  http://$MlFqdn:5000" -ForegroundColor White }
Write-Host "ACR:     $AcrLoginServer" -ForegroundColor White
Write-Host "Image:   $ImageTag" -ForegroundColor White
Write-Host "MongoDB: mongodb+srv://$MongoHost/$MongoDatabase (credentials masked)" -ForegroundColor White

Write-Host "Next: Update Android config to use http://$BackendFqdn:8080" -ForegroundColor Yellow

# 12. Save deployment info
$info = [pscustomobject]@{
  timestamp = (Get-Date).ToString('o')
  backendFqdn = $BackendFqdn
  healthUrl = $HealthUrl
  mlFqdn = $MlFqdn
  acr = $AcrLoginServer
  image = $ImageTag
  mongoHost = $MongoHost
  mongoDb = $MongoDatabase
  deployMl = [bool]$DeployMlApi
}
$info | ConvertTo-Json -Depth 3 | Out-File deployment-aci.json -Encoding UTF8
Write-Host "Saved deployment details to deployment-aci.json" -ForegroundColor Green
