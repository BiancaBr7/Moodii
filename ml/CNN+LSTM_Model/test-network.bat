@echo off
echo Testing Docker Network Connectivity
echo ====================================

echo.
echo 1. Testing basic internet connectivity...
ping -n 2 8.8.8.8

echo.
echo 2. Testing Docker registry connectivity...
ping -n 2 registry-1.docker.io

echo.
echo 3. Testing DNS resolution...
nslookup registry-1.docker.io

echo.
echo 4. Testing Docker daemon...
docker version

echo.
echo 5. Testing Docker pull (small image)...
docker pull hello-world

if errorlevel 1 (
    echo.
    echo ❌ Network issues detected!
    echo.
    echo Possible solutions:
    echo 1. Check Docker Desktop network settings
    echo 2. Restart Docker Desktop
    echo 3. Check firewall/antivirus settings
    echo 4. Try VPN disconnect if using one
    echo 5. Check corporate proxy settings
) else (
    echo.
    echo ✅ Network connectivity OK!
    echo You can now try building your container again.
)

echo.
pause
