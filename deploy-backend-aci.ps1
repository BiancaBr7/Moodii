# Simple deployment of Moodii Spring Boot backend to Azure Container Instances
# Usage (PowerShell):
#   ./deploy-backend-aci.ps1 -ResourceGroup moodii-rg -Location eastus -MongoUser biancabr7 -MongoPassword "YOUR_PASS" -Deploy
# Add -SkipBuild to reuse existing Docker image.
param(
  [string]$ResourceGroup = "moodii-rg",
  [string]$Location = "eastus",
  [string]$AcrBaseName = "moodiiacr",
  [string]$MongoUser = "biancabr7",
  [string]$MongoPassword,   # Plain for simplicity; move to Key Vault later
  [string]$MongoHost = "moodii-cluster.qbiqko9.mongodb.net",
  [string]$MongoDatabase = "moodii",
  [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
if(-not $MongoPassword){ throw "Provide -MongoPassword" }

function Require($cmd){ if(-not (Get-Command $cmd -ErrorAction SilentlyContinue)){ throw "Required command not found: $cmd" } }
Require az; Require docker; Require mvn

# Ensure resource group
# Robust resource group existence check
az group show -n $ResourceGroup -o none *> $null
if($LASTEXITCODE -ne 0){
  Write-Host "Creating resource group $ResourceGroup in $Location" -ForegroundColor Yellow
  az group create -n $ResourceGroup -l $Location | Out-Null
} else {
  Write-Host "Resource group $ResourceGroup exists" -ForegroundColor DarkGray
}

# Find or create ACR
$acrName = az acr list --resource-group $ResourceGroup --query "[?starts_with(name,'$AcrBaseName')].name | [0]" -o tsv
if(-not $acrName){
  $acrName = "$AcrBaseName$((Get-Random -Minimum 1000 -Maximum 9999))"
  az acr create -g $ResourceGroup -n $acrName --sku Basic --admin-enabled true | Out-Null
}
$acrServer = az acr show -n $acrName --query loginServer -o tsv

# Build jar & image
if(-not $SkipBuild){
  Push-Location backend/moodii
  mvn -q clean package -DskipTests
  if($LASTEXITCODE -ne 0){ throw "Maven build failed" }
  az acr login -n $acrName | Out-Null
  $image = "$acrServer/moodii-backend:latest"
  docker build -t $image .
  if($LASTEXITCODE -ne 0){ throw "Docker build failed" }
  docker push $image | Out-Null
  Pop-Location
} else {
  $image = "$acrServer/moodii-backend:latest"
}

# Compose Mongo URI (omit query params to avoid PowerShell ampersand parsing issues)
$mongoUri = 'mongodb+srv://'+$MongoUser+':' + $MongoPassword + '@' + $MongoHost + '/' + $MongoDatabase
Write-Host "Using Mongo URI (masked): mongodb+srv://***:***@$MongoHost/$MongoDatabase" -ForegroundColor DarkGray

# Delete any previous container group name
$cgName = 'moodii-backend'
az container delete -g $ResourceGroup -n $cgName --yes 2>$null | Out-Null

# Get ACR creds
$acrUser = az acr credential show -n $acrName --query username -o tsv
$acrPass = az acr credential show -n $acrName --query "passwords[0].value" -o tsv

# Deploy (single line to avoid line-continuation quoting issues)
az container create `
  --resource-group $ResourceGroup `
  --name $cgName `
  --image $image `
  --dns-name-label "$cgName-$((Get-Random -Minimum 1000 -Maximum 9999))" `
  --ports 8080 `
  --os-type Linux `
  --cpu 2 `
  --memory 3 `
  --restart-policy Always `
  --registry-login-server $acrServer `
  --registry-username $acrUser `
  --registry-password $acrPass `
  --environment-variables MONGODB_URI=$mongoUri SPRING_PROFILES_ACTIVE=prod | Out-Null

Write-Host "Waiting for startup (polling FQDN / IP)..."
$fqdn = ""
$ip = ""
for($i=0;$i -lt 36;$i++) { # up to ~180s
  $fqdn = az container show -g $ResourceGroup -n $cgName --query "ipAddress.fqdn" -o tsv 2>$null
  $ip    = az container show -g $ResourceGroup -n $cgName --query "ipAddress.ip" -o tsv 2>$null
  if($fqdn -and $fqdn.Trim() -ne '' -and $fqdn -ne 'null'){ break }
  if($ip -and $ip.Trim() -ne '' -and $ip -ne 'null'){ break }
  Start-Sleep 5
}
if(-not ($fqdn -and $fqdn.Trim() -ne '' -and $fqdn -ne 'null')){
  if($ip -and $ip.Trim() -ne '' -and $ip -ne 'null'){
    Write-Host "FQDN not ready; falling back to IP $ip" -ForegroundColor Yellow
  } else {
    throw "Container did not expose FQDN or public IP after wait"
  }
}

if($fqdn -and $fqdn.Trim() -ne '' -and $fqdn -ne 'null'){
  Write-Host "FQDN: $fqdn" -ForegroundColor DarkGray
}
if($ip -and $ip.Trim() -ne '' -and $ip -ne 'null'){
  Write-Host "Public IP: $ip" -ForegroundColor DarkGray
}

# Health probe with retries
$baseHost = if($fqdn -and $fqdn.Trim() -ne '' -and $fqdn -ne 'null'){ $fqdn } else { $ip }
$health = "http://$baseHost:8080/actuator/health"
$healthy = $false
for($i=0;$i -lt 12 -and -not $healthy;$i++) { # up to ~120s
  try {
    $resp = Invoke-RestMethod -Uri $health -TimeoutSec 8
    if($resp.status){
      Write-Host "Backend healthy: $($resp.status)" -ForegroundColor Green
      $healthy = $true
    }
  } catch { Start-Sleep 10 }
}
if(-not $healthy){
  Write-Host "Health endpoint not ready; showing last log lines:" -ForegroundColor Yellow
  $logs = az container logs -g $ResourceGroup -n $cgName 2>$null
  if($logs){ $logs -split "`n" | Select-Object -Last 60 | ForEach-Object { Write-Host $_ } } else { Write-Host "No logs returned" }
}

if($baseHost){
  Write-Host "Backend URL: http://$baseHost:8080" -ForegroundColor Cyan
} else {
  Write-Host "Backend URL host unresolved" -ForegroundColor Yellow
}
Write-Host "Health URL:  $health" -ForegroundColor Cyan

# Save info
[pscustomobject]@{ timestamp=(Get-Date).ToString('o'); fqdn=$fqdn; ip=$ip; baseHost=$baseHost; health=$health; image=$image; acr=$acrServer; mongoHost=$MongoHost; db=$MongoDatabase } | ConvertTo-Json -Depth 3 | Out-File backend-deployment.json -Encoding UTF8
Write-Host "Deployment details saved to backend-deployment.json"
