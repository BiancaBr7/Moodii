Deprecated / Removed Scripts
===========================

Pruned to reduce duplication. Canonical scripts retained:

Retained:
- deploy.ps1 (GHCR pull + local prod compose)
- deploy.sh (cross-platform equivalent)
- deploy-backend-aci.ps1 (backend Azure Container Instances)
- deploy-ml-aci.ps1 (ML Azure Container Instances)
- frontend/build-apk.ps1 (Docker-based APK build)
- ml/CNN+LSTM_Model/docker-deploy.sh (ML variant deploy)
- ml/CNN+LSTM_Model/test-docker.sh (ML container test)

Removed duplicates / obsolete / empty:
- deploy-test-azure.ps1
- deploy-springboot-simple.ps1
- deploy-springboot-fixed.ps1
- deploy-simple-prod.ps1
- deploy-springboot-azure.ps1
- deploy-simple-azure.ps1
- deploy-quick-production.ps1
- deploy-production-azure.ps1 (empty)
- deploy-moodii-azure.ps1 (empty)
- deploy-fast-azure.ps1
- deploy-clean-azure.ps1
- deploy-aci.ps1
- frontend/build-local.ps1
- ml/CNN+LSTM_Model/deploy.sh
- backend/moodii/deploy-jave17.bat
- backend/moodii/deploy-java21.bat
- ml/CNN+LSTM_Model/deploy.bat
- ml/CNN+LSTM_Model/deploy-d-drive.bat
- ml/CNN+LSTM_Model/build-on-d-drive.bat
- ml/CNN+LSTM_Model/build-offline.bat

Generated deployment JSON manifests now ignored (kept out of VCS):
- backend-deployment.json
- ml-deployment-*.json
- deployment-info.json
- deployment-aci.json
- clean-deployment.json

Recover with: `git checkout <commit> -- path/to/script` if ever required.
