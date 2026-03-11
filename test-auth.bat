@echo off
REM Script de prueba rápida para Tunja Evenfy - Authentication API
REM Ejecutar: test-auth.bat

setlocal enabledelayedexpansion

set "BASE_URL=http://localhost:8080/api/v1"
set "EMAIL=test-user-%random%@example.com"
set "PASSWORD=Password123!"
set "FULLNAME=Test User"

echo.
echo ============================================
echo 🧪 TESTS - Tunja Evenfy Authentication
echo ============================================
echo.

REM Test 1: Health Check
echo 1️⃣ Health Check...
curl -s http://localhost:8080/health | findstr /r "status"
echo.

REM Test 2: Registro
echo 2️⃣ Registrando usuario: %EMAIL%
set "REGISTER_RESPONSE="
for /f %%a in ('curl -s -X POST %BASE_URL%/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"fullName\":\"!FULLNAME!\",\"email\":\"!EMAIL!\",\"password\":\"!PASSWORD!\",\"passwordConfirm\":\"!PASSWORD!\"}"') do (
  set "REGISTER_RESPONSE=!REGISTER_RESPONSE!%%a"
)
echo Response: !REGISTER_RESPONSE!
echo.

REM Test 3: Login
echo 3️⃣ Haciendo login...
set "LOGIN_RESPONSE="
for /f %%a in ('curl -s -X POST %BASE_URL%/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"!EMAIL!\",\"password\":\"!PASSWORD!\"}"') do (
  set "LOGIN_RESPONSE=!LOGIN_RESPONSE!%%a"
)
echo Response: !LOGIN_RESPONSE!
echo.

REM Extraer token (básico - requiere jq para producción)
REM Aquí simplemente mostrar la respuesta y el user debe copiar el token

echo.
echo ============================================
echo ✅ Tests completados
echo.
echo 📝 Instrucciones:
echo 1. Copia el token de la respuesta anterior
echo 2. Usa ese token en el header: Authorization: Bearer {token}
echo 3. Intenta crear un evento:
echo.
echo    curl -X POST %BASE_URL%/events ^
echo      -H "Authorization: Bearer {TU_TOKEN}" ^
echo      -H "Content-Type: application/json" ^
echo      -d "{\"title\":\"Test Event\",\"description\":\"...\",\"eventDate\":\"2025-06-20\",\"categoryId\":\"...\",\"locationId\":\"...\",\"organizerId\":\"...\"}"
echo.
echo ============================================

endlocal
pause
