@echo off
chcp 65001 >nul
setlocal ENABLEDELAYEDEXPANSION

REM Project root (directory of this script)
set "ROOT=%~dp0"
cd /d "%ROOT%"

set GEMINI_API_KEY=AIzaSyCd3KlzM6KvMBO5HaiUvpWeuDPcb9vCjx0

echo ==============================================
echo  AI-QA-System ^| 一键启动 (Windows .bat)
echo  前提：已启动 MySQL 与 Nacos: 54.219.180.170:8848
echo  如需 Gemini，请先设置 GEMINI_API_KEY
echo ==============================================

echo.
if "%GEMINI_API_KEY%"=="" (
  echo [提示] 未检测到 GEMINI_API_KEY，qa-service 可能无法访问模型接口。
) else (
  echo [OK] 已检测到 GEMINI_API_KEY。
)

echo.
echo [1/2] 构建后端（跳过测试）...
pushd %~dp0backend-services
call mvn -q -DskipTests clean package
if errorlevel 1 (
  echo [错误] Maven 构建失败，请检查日志。
  popd
  pause
  goto :eof
)
popd

echo.
echo [2/2] 启动各个服务...

echo 启动 API Gateway (:8080)...
start "API Gateway" cmd /k "cd /d %~dp0backend-services\api-gateway && mvn spring-boot:run"

timeout /t 3 /nobreak >nul

echo 启动 User Service (:8081)...
start "User Service" cmd /k "cd /d %~dp0backend-services\user-service && mvn spring-boot:run"

timeout /t 3 /nobreak >nul

echo 启动 QA Service (:8082)...
start "QA Service" cmd /k "cd /d %~dp0backend-services\qa-service && set GEMINI_API_KEY=%GEMINI_API_KEY% && mvn spring-boot:run"

timeout /t 3 /nobreak >nul

echo 启动 Frontend (:3000)...
start "Frontend" cmd /k "cd /d %~dp0frontend && npm install --no-fund --no-audit && npm run dev"

echo.
echo ==============================================
echo  服务启动中...
echo.
echo  访问地址：
echo   - Frontend: http://localhost:3000
echo   - API Gateway: http://localhost:8080
echo   - Nacos: 54.219.180.170:8848
echo.
echo  每个服务在独立窗口中运行
echo ==============================================

endlocal
exit /b 0
