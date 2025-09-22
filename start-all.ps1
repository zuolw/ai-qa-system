param(
  [string]$GeminiApiKey = $env:GEMINI_API_KEY
)

$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

Write-Host '=============================================='
Write-Host ' AI-QA-System 一键启动脚本 (PowerShell .ps1)'
Write-Host ' - 前提：已启动 MySQL 与 Nacos: 54.219.180.170:8848'
Write-Host ' - 如需 Gemini，请传入 -GeminiApiKey 或预先设置环境变量'
Write-Host '=============================================='

if (-not $GeminiApiKey -or $GeminiApiKey.Length -lt 10) {
  Write-Warning '未检测到有效的 GEMINI_API_KEY，qa-service 可能无法访问模型接口'
} else {
  $env:GEMINI_API_KEY = $GeminiApiKey
  Write-Host '[OK] 已设置 GEMINI_API_KEY'
}

Write-Host "`n[1/2] 构建后端（跳过测试）..."
try {
  Push-Location (Join-Path $root 'backend-services')
  & mvn -q -DskipTests package
  if ($LASTEXITCODE -ne 0) { throw 'Maven 构建失败，请检查日志' }
} finally {
  Pop-Location
}

Write-Host "`n[2/2] 启动各个服务窗口..."

# API Gateway
Start-Process -WindowStyle Normal powershell -ArgumentList "-NoExit","-Command","cd \"\"$root/backend-services/api-gateway\"\"; mvn spring-boot:run"

# User Service
Start-Process -WindowStyle Normal powershell -ArgumentList "-NoExit","-Command","cd \"\"$root/backend-services/user-service\"\"; mvn spring-boot:run"

# QA Service（继承 GEMINI_API_KEY）
Start-Process -WindowStyle Normal powershell -ArgumentList "-NoExit","-Command","cd \"\"$root/backend-services/qa-service\"\"; $env:GEMINI_API_KEY='$env:GEMINI_API_KEY'; mvn spring-boot:run"

# Frontend
Start-Process -WindowStyle Normal powershell -ArgumentList "-NoExit","-Command","cd \"\"$root/frontend\"\"; npm install --no-fund --no-audit; npm run dev"

Write-Host "`n已在新窗口启动："
Write-Host ' - http://localhost:8080  (API Gateway)'
Write-Host ' - http://localhost:3000  (Frontend)'
Write-Host ' - Nacos: 54.219.180.170:8848  (作为注册中心)'
