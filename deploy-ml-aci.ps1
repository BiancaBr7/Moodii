<#
Deploy ML service (choose variant) to Azure Container Instances using existing or new ACR.
Usage examples:
  # Simple api.py service in ml/ (model pickle based)
  ./deploy-ml-aci.ps1 -ResourceGroup moodii-rg -Location eastus -Variant simple
  # CNN+LSTM gunicorn service
  ./deploy-ml-aci.ps1 -ResourceGroup moodii-rg -Location eastus -Variant cnnlstm
Optional:
  -AcrName existingAcrName  (reuse)  | omit to auto-detect same RG ACR or create new
  -ImageTag v1
  -Cpu 1 -Memory 2
  -DnsLabel customlabel (must be globally unique) otherwise auto
#>
[CmdletBinding()]
param(
  [ValidateSet('simple','cnnlstm')] [string]$Variant = 'simple',
  [string]$ResourceGroup = 'moodii-rg',
  [string]$Location = 'eastus',
  [string]$AcrName = '',
  [string]$ImageTag = 'latest',
  [int]$Cpu = 1,
  [int]$Memory = 2,
  [string]$DnsLabel = ''
)

$ErrorActionPreference = 'Stop'
function Require($c){ if(-not (Get-Command $c -ErrorAction SilentlyContinue)){ throw "Missing command: $c" } }
Require az; Require docker

# 1. RG ensure
if(-not (az group exists -n $ResourceGroup | ConvertFrom-Json)){
  az group create -n $ResourceGroup -l $Location 1>$null
}

# 2. ACR resolve/create
if(-not $AcrName -or -not (az acr show -n $AcrName -g $ResourceGroup 2>$null)){
  $existing = az acr list -g $ResourceGroup --query "[0].name" -o tsv
  if($existing){ $AcrName = $existing } else { $AcrName = "moodiimlacr$((Get-Random -Minimum 1000 -Maximum 9999))"; az acr create -g $ResourceGroup -n $AcrName --sku Basic --admin-enabled true 1>$null }
}
$acrServer = az acr show -n $AcrName --query loginServer -o tsv
az acr login -n $AcrName 1>$null

# 3. Build context select
if($Variant -eq 'simple'){
  $context = 'ml'
  $dockerfile = 'ml/Dockerfile'
  $containerName = 'moodii-ml-simple'
  $port = 5000
  $healthPath = '/health'
} else {
  $context = 'ml/CNN+LSTM_Model'
  $dockerfile = 'ml/CNN+LSTM_Model/Dockerfile'
  $containerName = 'moodii-ml-cnnlstm'
  $port = 5000
  $healthPath = '/health'
}

$imageRef = "${acrServer}/${containerName}:${ImageTag}"
Write-Host "Building ML image $imageRef (variant=$Variant)" -ForegroundColor Cyan

docker build -f $dockerfile -t $imageRef $context
if($LASTEXITCODE -ne 0){ throw 'Docker build failed' }

docker push $imageRef 1>$null

# 4. Delete old container group if exists (same name)
az container delete -g $ResourceGroup -n $containerName --yes 2>$null | Out-Null

# 5. ACR credentials
$acrUser = az acr credential show -n $AcrName --query username -o tsv
$acrPass = az acr credential show -n $AcrName --query "passwords[0].value" -o tsv

# 6. DNS label
if(-not $DnsLabel){ $DnsLabel = "$containerName-$((Get-Random -Minimum 1000 -Maximum 9999))" }

Write-Host "Deploying ML container group $containerName" -ForegroundColor Cyan
az container create `
  --resource-group $ResourceGroup `
  --name $containerName `
  --image $imageRef `
  --dns-name-label $DnsLabel `
  --ports $port `
  --os-type Linux `
  --cpu $Cpu `
  --memory $Memory `
  --restart-policy Always `
  --registry-login-server $acrServer `
  --registry-username $acrUser `
  --registry-password $acrPass 1>$null

Write-Host "Waiting for public endpoint..." -ForegroundColor DarkGray
$fqdn='';$ip=''
for($i=0;$i -lt 40;$i++){
  $fqdn = az container show -g $ResourceGroup -n $containerName --query "ipAddress.fqdn" -o tsv 2>$null
  $ip   = az container show -g $ResourceGroup -n $containerName --query "ipAddress.ip" -o tsv 2>$null
  if($fqdn -or $ip){ break }
  Start-Sleep 5
}
if(-not $fqdn -and -not $ip){ throw 'No public endpoint assigned' }
$mlHost = if($fqdn){$fqdn}else{$ip}

Write-Host "Probing health ($healthPath) ..." -ForegroundColor DarkGray
$baseUrl = "http://${mlHost}:${port}"
$probeUrl = "$baseUrl$healthPath"
$healthy=$false
for($i=0;$i -lt 18 -and -not $healthy;$i++){
  try {
    $resp = Invoke-WebRequest -Uri $probeUrl -TimeoutSec 8 -Method GET -ErrorAction Stop
    if($resp.StatusCode -ge 200 -and $resp.StatusCode -lt 500){
      $healthy=$true
      Write-Host "ML service responding (status $($resp.StatusCode))" -ForegroundColor Green
      break
    }
  } catch {
    Start-Sleep 5
  }
}
if(-not $healthy){
  Write-Host 'Service not healthy yet; recent logs:' -ForegroundColor Yellow
  $logs = az container logs -g $ResourceGroup -n $containerName 2>$null
  if($logs){ $logs -split "`n" | Select-Object -Last 80 | ForEach-Object { Write-Host $_ } } else { Write-Host 'No logs returned' }
}

[pscustomobject]@{ timestamp=(Get-Date).ToString('o'); variant=$Variant; rg=$ResourceGroup; acr=$AcrName; image=$imageRef; container=$containerName; fqdn=$fqdn; ip=$ip; port=$port; baseUrl=$baseUrl; probe=$probeUrl; healthy=$healthy } | ConvertTo-Json -Depth 3 | Out-File ml-deployment-$Variant.json -Encoding UTF8
Write-Host "ML deployment complete: $baseUrl" -ForegroundColor Cyan
Write-Host "Probe: $probeUrl" -ForegroundColor Cyan
if(-not $healthy){ Write-Host 'Investigate logs; may still be starting (TensorFlow cold start can be slow).' -ForegroundColor Yellow }
