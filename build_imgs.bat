@echo off
REM ============================
REM Script para build e push das imagens Docker para o ECR
REM ============================

REM Configura variáveis
set ACCOUNT_ID=425515537844
set REGION=us-east-1
set BACKEND_IMAGE=%ACCOUNT_ID%.dkr.ecr.%REGION%.amazonaws.com/batponto-dev-backend:latest
set FLYWAY_IMAGE=%ACCOUNT_ID%.dkr.ecr.%REGION%.amazonaws.com/batponto-dev-flyway:latest

REM ============================
REM Login no ECR
REM ============================
echo Logging in to ECR...
aws ecr get-login-password --region %REGION% | docker login --username AWS --password-stdin %ACCOUNT_ID%.dkr.ecr.%REGION%.amazonaws.com

IF %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha ao logar no ECR
    exit /b 1
)

REM ============================
REM Build backend
REM ============================
echo Building backend...
call mvn -DskipTests package

IF %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha no build do backend
    exit /b 1
)

docker build -f Dockerfile.backend -t batponto-backend:latest .
docker tag batponto-backend:latest %BACKEND_IMAGE%
docker push %BACKEND_IMAGE%

IF %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha ao push do backend
    exit /b 1
)

REM ============================
REM Build Flyway
REM ============================
echo Building Flyway...
docker build -f Dockerfile.flyway -t batponto-flyway:latest .
docker tag batponto-flyway:latest %FLYWAY_IMAGE%
docker push %FLYWAY_IMAGE%

IF %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha ao push do Flyway
    exit /b 1
)

echo ============================
echo Todas as imagens foram enviadas com sucesso!
echo ============================
pause
