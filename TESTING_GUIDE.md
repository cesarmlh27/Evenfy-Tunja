# 🧪 Guía de Prueba - Sistema de Autenticación JWT

## 📋 Requisitos Previos

1. **PostgreSQL corriendo** en `localhost:5432`
2. **Backend levantado** en `http://localhost:8080`
3. **Postman instalado** (descarga gratis en https://www.postman.com/downloads/)
4. **Gmail configurado** (con contraseña de aplicación si usas 2FA)

---

## 🚀 Paso 1: Importar Colección en Postman

1. Abre **Postman**
2. Click en **Import** (arriba a la izquierda)
3. Selecciona `Tunja_Evenfy_Auth_Tests.postman_collection.json` de este proyecto
4. Click en **Import**

---

## 🔍 Paso 2: Configurar Variables de Entorno

En Postman:

1. Click en **Environments** (lado izquierdo)
2. Click en **Create Environment**
3. Nombre: `Tunja_Evenfy_Dev`
4. Agregar variables:

```
jwt_token          = (se rellena automáticamente al hacer login)
user_id            = (se rellena automáticamente al hacer login)
jwt_token_user     = (token de usuario con rol USER)
```

5. Click en **Save**

---

## ✅ Prueba 1: Registro

### Endpoint
```
POST http://localhost:8080/api/v1/auth/register
```

### Pasos en Postman:

1. Selecciona carpeta **1️⃣ REGISTRO (Register)**
2. Click en **Send**

### Respuesta Esperada (200 OK):
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "juan@example.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "message": "Registro exitoso. Revisa tu email para confirmar tu cuenta."
}
```

### Validar:
- ✅ Status 200
- ✅ Email recibido (opcional)
- ✅ Rol es "USER" por defecto

---

## ✅ Prueba 2: Login (SIN 2FA)

### Endpoint
```
POST http://localhost:8080/api/v1/auth/login
```

### Pasos en Postman:

1. Selecciona carpeta **2️⃣ LOGIN (Sin 2FA)**
2. Cambia email/password al usuario que registraste
3. Click en **Send**

### Respuesta Esperada (200 OK):
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "juan@example.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "twoFactorRequired": false,
  "message": "Inicio de sesión exitoso"
}
```

### ¿Qué sucede automáticamente?
- ✅ El script de **Tests** en Postman captura el token
- ✅ Guarda en variable `jwt_token`
- ✅ Guarda en variable `user_id`
- ✅ Ahora puedes usar ese token en otras peticiones

---

## ✅ Prueba 3: Ver Eventos (PÚBLICO - Sin Token)

### Endpoint
```
GET http://localhost:8080/api/v1/events
```

### Pasos en Postman:

1. Selecciona **✅ GET - Ver todos los eventos**
2. Click en **Send**

### Respuesta Esperada (200 OK):
```json
[
  {
    "id": "...",
    "title": "Festival de Luces de Tunja",
    "description": "...",
    "eventDate": "2025-06-15",
    ...
  }
]
```

### Validar:
- ✅ No requiere token
- ✅ Retorna lista de eventos (MOCK_EVENTS por ahora)

---

## 🔒 Prueba 4: Crear Evento (PROTEGIDO - Con Token)

### Endpoint
```
POST http://localhost:8080/api/v1/events
Authorization: Bearer {jwt_token}
```

### Pasos en Postman:

1. Selecciona **🔒 POST - Crear evento**
2. Verifica que el header `Authorization` tiene `Bearer {{jwt_token}}`
3. Modifica el body si lo deseas
4. Click en **Send**

### Respuesta Esperada (201 Created) Si eres ORGANIZER/ADMIN:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "title": "Concierto de Reggaeton",
  "description": "Música en vivo con mejores artistas",
  "eventDate": "2025-06-20",
  ...
}
```

### Respuesta Si eres USER (403 Forbidden):
```json
{
  "timestamp": "2024-03-10T15:30:00",
  "status": 403,
  "error": "BadRequestException",
  "message": "Solo administradores y organizadores pueden crear eventos"
}
```

---

## ❌ Prueba 5: Crear Evento SIN TOKEN (Debe Fallar)

### Pasos:

1. Selecciona **❌ POST - Crear evento SIN TOKEN**
2. Click en **Send**

### Respuesta Esperada (Error - depende del servidor):
Puede ser 401 o 400, pero el backend debe rechazarlo.

### Validar:
- ✅ Sin token no se puede crear

---

## 🛡️ Prueba 6: Crear Evento COMO USER (Debe Fallar)

### Pasos:

1. Primero, registra un usuario normal (USER)
2. Copia su token en la variable `jwt_token_user`
3. Selecciona **❌ POST - Crear evento COMO USER**
4. Click en **Send**

### Respuesta Esperada (403 Forbidden):
```json
{
  "message": "Solo administradores y organizadores pueden crear eventos"
}
```

### Validar:
- ✅ El endpoint valida roles correctamente

---

## 🔐 Prueba 7: 2FA (Verificación en Dos Pasos)

### Paso 1: Login con usuario que tiene 2FA habilitado

1. NOTA: Primero debe haber un usuario con 2FA habilitado
2. Selecciona **3️⃣ LOGIN (Con 2FA - Primer paso)**
3. Click en **Send**

### Respuesta Esperada (200 OK):
```json
{
  "userId": "...",
  "email": "usuario@example.com",
  "fullName": "Usuario",
  "role": "USER",
  "twoFactorRequired": true,
  "message": "Código de 2FA enviado a tu email"
}
```

### Validar:
- ✅ `twoFactorRequired` = true
- ✅ No retorna token todavía
- ✅ Código se envió al email

---

### Paso 2: Verificar código 2FA

1. **Revisa tu email** por el código de 6 dígitos (válido 10 minutos)
2. Selecciona **4️⃣ VERIFY 2FA**
3. Reemplaza `"code": "123456"` con el código real
4. Click en **Send**

### Respuesta Esperada (200 OK):
```json
{
  "userId": "...",
  "email": "usuario@example.com",
  "fullName": "Usuario",
  "role": "USER",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "twoFactorRequired": false,
  "message": "Verificación 2FA exitosa"
}
```

### Validar:
- ✅ Token retornado correctamente
- ✅ `twoFactorRequired` = false
- ✅ Código se marcó como usado

---

## 🏥 Prueba 8: Health Check

### Endpoint
```
GET http://localhost:8080/health
```

### Respuesta Esperada:
```json
{
  "status": "UP",
  "message": "El servidor está funcionando correctamente"
}
```

---

## 📊 Resumen de Lo Que Se Está Validando

| Prueba | Lo que valida | ✅ Esperado |
|--------|--------------|-----------|
| Registro | Usuario se crea con role USER | Guarda en BD |
| Login sin 2FA | Token se genera si no hay 2FA | Token válido 24h |
| Login con 2FA | Pide código antes de generar token | `twoFactorRequired: true` |
| Verify 2FA | Código verifica y genera token | Token después de validar |
| Ver eventos | Acceso público | Sin necesidad de token |
| Crear evento (ADMIN) | Solo roles autorizados | Success 201 |
| Crear evento (USER) | Rechaza usuarios normales | Error 403 |
| Sin token | Rechaza peticiones sin autorización | Error 401/400 |

---

## 🐛 Troubleshooting

### ❌ "Connection refused" (No puede conectar)
- Verifica que el backend está corriendo
- `mvn spring-boot:run` desde la carpeta raíz

### ❌ Error CORS
- El backend ya tiene CORS configurado
- Asegúrate que `http://localhost:8080` está en la lista

### ❌ "Email is required"
- Cambias el email en el body de la petición
- Usa un email único cada vez que registres

### ❌ "Usuario no encontrado"
- Verifica que el email está correcto
- La base de datos está corriendo

### ❌ "Código inválido o expirado"
- El código de 2FA expira en 10 minutos
- Copia el código completo del email
- Intenta de nuevo

---

## 🚀 Siguiente Paso

Una vez valides que todo funciona:

1. **Crear pantallas de Login/Registro en React**
2. **Guardar token en localStorage**
3. **Enviar token en headers de todas las peticiones**
4. **Implementar página de Crear Evento** (solo para ADMIN/ORGANIZER)

¿Necesitas ayuda con el frontend?
