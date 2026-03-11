# 🔐 Sistema de Autenticación - Tunja Evenfy

## Implementación Completada

### ✅ Backend - Autenticación & Autorización

**Componentes Creados:**

1. **Entidades Extendidas**
   - `UserEntity` - Campos adicionales para 2FA, verificación de email, etc.
   - `TwoFactorTokenEntity` - Almacena códigos 2FA temporales (vencimiento en 10 min)

2. **DTOs de Autenticación**
   - `LoginRequest` - Email + Contraseña
   - `RegisterRequest` - Registro de nuevo usuario
   - `AuthResponse` - Respuesta con token JWT y datos del usuario
   - `TwoFactorRequest` - Validación de código 2FA

3. **Servicios**
   - `AuthService` - Lógica de registro, login, 2FA
   - `EmailService` - Envío de códigos 2FA y emails de bienvenida vía Gmail

4. **Utilidades**
   - `JwtUtil` - Generación y validación de tokens JWT
   - `JwtFilter` - Validación de autorización en peticiones
   - `SecurityConfig` - Configuración CORS y BCrypt

5. **Controlador**
   - `AuthRest` - Endpoints: `/register`, `/login`, `/verify-2fa`

6. **Autorizacion**
   - Solo **ADMIN** y **ORGANIZER** pueden crear/editar/eliminar eventos
   - Los endpoints están protegidos y validan el JWT

---

## 🔧 Configuración Necesaria

### 1. Variables de Entorno (Gmail)

Necesitas crear una **contraseña de aplicación** en Gmail (no es tu contraseña normal):

1. Ve a: https://myaccount.google.com/security
2. Activa **Autenticación en 2 pasos**
3. Busca **"Contraseñas de aplicación"** 
4. Selecciona Mail → Windows Computer
5. Copia la contraseña generada

En `application.properties`, reemplaza:
```properties
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-contraseña-de-16-caracteres-generada
```

O usa variables de entorno:
```bash
set GMAIL_USER=tu-email@gmail.com
set GMAIL_PASSWORD=tu-contrasena-generada
```

### 2. Base de Datos PostgreSQL

Las entidades se crearán automáticamente gracias a `ddl-auto: update`

```sql
-- Opcional: crear índices para mejor rendimiento
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_two_factor_tokens_user_id ON two_factor_tokens(user_id);
```

### 3. JWT Secret (Opcional)

En producción, cambia `jwt.secret` en `application.properties`:
```properties
jwt.secret=tu-secret-key-super-segureza-min-32-caracteres
jwt.expiration=86400000  # 24 horas (en milisegundos)
```

---

## 📡 Endpoints de Autenticación

### 1. Registro

```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "fullName": "Juan Pérez",
  "email": "juan@example.com",
  "password": "Password123!",
  "passwordConfirm": "Password123!"
}
```

**Respuesta (201):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "juan@example.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "message": "Registro exitoso. Revisa tu email para confirmar tu cuenta."
}
```

---

### 2. Login (sin 2FA)

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "Password123!"
}
```

**Respuesta (200) - Sin 2FA:**
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

---

### 3. Login (con 2FA) - Primer paso

**Respuesta (200) - Con 2FA Habilitado:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "juan@example.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "twoFactorRequired": true,
  "message": "Código de 2FA enviado a tu email"
}
```

Un código de 6 dígitos se envía al email (válido por 10 minutos).

---

### 4. Verificar Código 2FA

```bash
POST /api/v1/auth/verify-2fa
Content-Type: application/json

{
  "email": "juan@example.com",
  "code": "123456"
}
```

**Respuesta (200):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "juan@example.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "twoFactorRequired": false,
  "message": "Verificación 2FA exitosa"
}
```

---

## 🛡️ Usando Tokens JWT

El token se envía en el header `Authorization`:

```bash
GET /api/v1/events
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**El token contiene:**
- `userId` - ID del usuario
- `email` - Email del usuario
- `role` - Rol (USER, ORGANIZER, ADMIN)
- `exp` - Fecha de expiración

---

## 🔒 Roles & Permisos

| Acción | USER | ORGANIZER | ADMIN |
|--------|------|-----------|-------|
| Ver eventos | ✅ | ✅ | ✅ |
| Crear evento | ❌ | ✅ | ✅ |
| Editar evento | ❌ | ✅* | ✅ |
| Eliminar evento | ❌ | ✅* | ✅ |
| Registrarse en evento | ✅ | ✅ | ✅ |
| Agregar a favoritos | ✅ | ✅ | ✅ |

*ORGANIZER solo puede editar/eliminar sus propios eventos

---

## 📝 Crear Evento (Solo ADMIN/ORGANIZER)

```bash
POST /api/v1/events
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Festival de Luces de Tunja",
  "description": "Una noche mágica...",
  "eventDate": "2025-06-15",
  "categoryId": "550e8400-e29b-41d4-a716-446655440001",
  "locationId": "550e8400-e29b-41d4-a716-446655440002",
  "organizerId": "{userId_del_token}"
}
```

**Error si no es ADMIN/ORGANIZER (403):**
```json
{
  "timestamp": "2024-03-10T15:30:00",
  "status": 403,
  "error": "BadRequestException",
  "message": "Solo administradores y organizadores pueden crear eventos"
}
```

---

## 🚀 Próximos Pasos

1. **Configurar email en Gmail** (variable de entorno o properties)
2. **Iniciar PostgreSQL** con base de datos
3. **Ejecutar el backend**
4. **Actualizar el frontend** para usar los nuevos endpoints (ver sección Frontend)
5. **Habilitar 2FA** para usuarios (opcional clic desde perfil)

---

## 📲 Frontend - Próximas Acciones

Se necesitará:
- Página de **Registro**
- Página de **Login** 
- Página de **Verificación 2FA**
- Almacenar token en `localStorage`
- Enviar token en headers de todas las peticiones autenticadas
- Página de **Perfil** con opción de habilitar/deshabilitar 2FA
- Página de **Crear Evento** (solo para ADMIN/ORGANIZER)

---

## 🐛 Troubleshooting

**Error: "Error al enviar email de 2FA"**
- Verifica que Gmail está configurado correctamente
- Revisa que la contraseña de aplicación es válida
- Comprueba que 2FA está habilitado en la cuenta Gmail

**Error: "Token inválido"**
- El token puede haber expirado (vence en 24 horas)
- El usuario debe hacer login nuevamente

**Error: "Solo administradores..."**
- El rol del usuario no es ADMIN ni ORGANIZER
- El token no contiene el rol correcto

---

Implementación completada ✅
