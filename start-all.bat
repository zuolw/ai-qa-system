@echo off
chcp 65001 >nul
setlocal ENABLEDELAYEDEXPANSION

REM Project root (directory of this script)
set "ROOT=%~dp0"
cd /d "%ROOT%"

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
pushd "%ROOT%backend-services"
call mvn -q -DskipTests package
if errorlevel 1 (
  echo [错误] Maven 构建失败，请检查日志。
  popd
  pause
  goto :eof
)
popd

echo.
echo [2/2] 启动各个服务窗口...

REM API Gateway (:8080)
start "api-gateway_8080" cmd /k "pushd \"%ROOT%backend-services\api-gateway\" && mvn spring-boot:run"

REM User Service (:8081)
start "user-service_8081" cmd /k "pushd \"%ROOT%backend-services\user-service\" && mvn spring-boot:run"

REM QA Service (:8082)
start "qa-service_8082" cmd /k "pushd \"%ROOT%backend-services\qa-service\" && set GEMINI_API_KEY=%GEMINI_API_KEY% && mvn spring-boot:run"

REM Frontend (:3000)
start "frontend_3000" cmd /k "pushd \"%ROOT%frontend\" && npm install --no-fund --no-audit && npm run dev"

echo.
echo 已在新窗口启动：
echo  - http://localhost:8080  (API Gateway)
echo  - http://localhost:3000  (Frontend)
echo  - Nacos: 54.219.180.170:8848  (作为注册中心)

echo.
echo 若需停止，请关闭各自窗口或使用任务管理器结束相应进程。
endlocal
exit /b 0
