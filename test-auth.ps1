# Script de prueba rápida para Tunja Evenfy - Authentication API
# Ejecutar en PowerShell: .\test-auth.ps1

param(
    [string]$BaseUrl = "http://localhost:8080/api/v1",
    [string]$Email = "test-user-$(Get-Random)@example.com",
    [string]$Password = "Password123!",
    [string]$FullName = "Test User"
)

Write-Host "" 
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "🧪 TESTS - Tunja Evenfy Authentication" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Health Check
Write-Host "1️⃣ Health Check..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/health" -Method Get
    Write-Host "✅ Status: $($health.status)" -ForegroundColor Green
    Write-Host "   Message: $($health.message)" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    Write-Host "   Asegúrate que el backend está corriendo: mvn spring-boot:run" -ForegroundColor Yellow
    exit
}
Write-Host ""

# Test 2: Registro
Write-Host "2️⃣ Registrando usuario..." -ForegroundColor Yellow
Write-Host "   Email: $Email" -ForegroundColor Gray
Write-Host "   Password: $Password" -ForegroundColor Gray

$registerBody = @{
    fullName = $FullName
    email = $Email
    password = $Password
    passwordConfirm = $Password
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/register" `
        -Method Post `
        -Headers @{"Content-Type" = "application/json"} `
        -Body $registerBody
    
    Write-Host "✅ Usuario registrado exitosamente" -ForegroundColor Green
    Write-Host "   UserId: $($registerResponse.userId)" -ForegroundColor Green
    Write-Host "   Role: $($registerResponse.role)" -ForegroundColor Green
} catch {
    Write-Host "❌ Error en registro: $_" -ForegroundColor Red
    exit
}
Write-Host ""

# Test 3: Login
Write-Host "3️⃣ Haciendo login..." -ForegroundColor Yellow

$loginBody = @{
    email = $Email
    password = $Password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login" `
        -Method Post `
        -Headers @{"Content-Type" = "application/json"} `
        -Body $loginBody
    
    Write-Host "✅ Login exitoso" -ForegroundColor Green
    Write-Host "   Full Name: $($loginResponse.fullName)" -ForegroundColor Green
    Write-Host "   Role: $($loginResponse.role)" -ForegroundColor Green
    
    $token = $loginResponse.token
    $userId = $loginResponse.userId
    
    if ($loginResponse.twoFactorRequired) {
        Write-Host "🔐 2FA requerido - Revisa tu email por el código" -ForegroundColor Yellow
    } else {
        Write-Host "   Token: $($token.Substring(0, 50))..." -ForegroundColor Green
    }
    
} catch {
    Write-Host "❌ Error en login: $_" -ForegroundColor Red
    exit
}
Write-Host ""

# Test 4: Obtener eventos (sin token)
Write-Host "4️⃣ Obteniendo eventos (sin token)..." -ForegroundColor Yellow
try {
    $eventsResponse = Invoke-RestMethod -Uri "$BaseUrl/events" -Method Get
    Write-Host "✅ Eventos obtenidos exitosamente" -ForegroundColor Green
    Write-Host "   Total: $($eventsResponse.Count) eventos" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Error: $_" -ForegroundColor Yellow
}
Write-Host ""

# Test 5: Intentar crear evento (con token)
if ($token) {
    Write-Host "5️⃣ Intentando crear evento..." -ForegroundColor Yellow
    
    $eventBody = @{
        title = "Test Event - $(Get-Date -Format 'HH:mm:ss')"
        description = "Evento de prueba del script"
        eventDate = "2025-06-20"
        categoryId = "550e8400-e29b-41d4-a716-446655440001"
        locationId = "550e8400-e29b-41d4-a716-446655440002"
        organizerId = $userId
    } | ConvertTo-Json
    
    try {
        $eventResponse = Invoke-RestMethod -Uri "$BaseUrl/events" `
            -Method Post `
            -Headers @{
                "Content-Type" = "application/json"
                "Authorization" = "Bearer $token"
            } `
            -Body $eventBody
        
        Write-Host "✅ Evento creado (si eres ADMIN/ORGANIZER)" -ForegroundColor Green
        Write-Host "   Event ID: $($eventResponse.id)" -ForegroundColor Green
    } catch {
        $errorDetail = $_.Exception.Response.StatusCode
        Write-Host "❌ Error al crear evento: $errorDetail" -ForegroundColor Red
        if ($errorDetail -eq 403) {
            Write-Host "   → Tu rol (USER) no tiene permiso. Necesitas ser ADMIN o ORGANIZER" -ForegroundColor Yellow
        }
    }
    Write-Host ""
}

# Resumen
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "✅ Tests completados" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Cambiar usuario en variables:" -ForegroundColor Yellow
Write-Host "  .\test-auth.ps1 -Email 'tuEmail@example.com' -Password 'TuPassword'" -ForegroundColor Gray
Write-Host ""
Write-Host "Próximos pasos:" -ForegroundColor Yellow
Write-Host "  1. Ver guía detallada en: TESTING_GUIDE.md" -ForegroundColor Gray
Write-Host "  2. Importar colección Postman: Tunja_Evenfy_Auth_Tests.postman_collection.json" -ForegroundColor Gray
Write-Host "  3. Crear pantallas de login en React" -ForegroundColor Gray
Write-Host ""
