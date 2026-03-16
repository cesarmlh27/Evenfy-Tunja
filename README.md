# Tunja Evenfy

Plataforma de eventos para la ciudad de Tunja, Boyacá. Permite descubrir, crear y gestionar eventos culturales, gastronómicos, artísticos y más.

## Arquitectura

| Capa | Tecnología |
|------|-----------|
| Backend | Spring Boot 3.5.7, Java 17 |
| Frontend | React 18 + Vite 5 |
| Base de datos | PostgreSQL 16 |
| Autenticación | JWT (jjwt 0.12.3) |
| Documentación API | Springdoc OpenAPI (Swagger UI) |

## Requisitos

- Java 17+
- Node.js 18+
- PostgreSQL 16+

## Configuración

1. Crear la base de datos:
```sql
CREATE DATABASE evenfy_db;
```

2. Copiar y configurar variables de entorno:
```bash
cp .env.example .env
```

Variables requeridas: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`

Variables adicionales recomendadas: `SPRING_PROFILES_ACTIVE`, `CORS_ALLOWED_ORIGINS`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `RESEND_API_KEY`

## Ejecución

### Backend
```bash
./mvnw spring-boot:run
```
El servidor inicia en `http://localhost:8080`

Perfiles disponibles:
- `dev` (default): configuración local con valores de respaldo
- `prod`: requiere variables de entorno obligatorias y validación de esquema

Ejemplo producción:
```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
La aplicación inicia en `http://localhost:5173`

## API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Actuator health: `http://localhost:8080/actuator/health`

## Docker

### Backend
```bash
docker build -t tunja-evenfy-backend .
docker run -p 8080:8080 --env-file .env tunja-evenfy-backend
```

### Frontend
```bash
docker build -t tunja-evenfy-frontend ./frontend
docker run -p 5173:80 tunja-evenfy-frontend
```

### Full stack (frontend + backend + postgres)
```bash
docker compose up --build
```

### Production from published images
```bash
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## CI

El repositorio incluye workflow en [`.github/workflows/ci.yml`](.github/workflows/ci.yml) con:
- Build + tests de backend (`./mvnw clean verify`)
- Build de frontend (`npm ci && npm run build`)

Además incluye workflow de publicación de imágenes a GHCR:
- [`.github/workflows/cd-images.yml`](.github/workflows/cd-images.yml)

Guía completa de despliegue:
- [`DEPLOYMENT_GUIDE.md`](DEPLOYMENT_GUIDE.md)

### Endpoints principales

| Recurso | Ruta base |
|---------|----------|
| Auth | `/api/v1/auth` |
| Usuarios | `/api/v1/users` |
| Eventos | `/api/v1/events` |
| Categorías | `/api/v1/categories` |
| Ubicaciones | `/api/v1/locations` |
| Comentarios | `/api/v1/comments` |
| Favoritos | `/api/v1/favorites` |
| Asistencia | `/api/v1/events/{id}/attend` |
| Calificaciones | `/api/v1/events/{id}/rate` |

## Estructura del proyecto

```
├── src/main/java/org/jdc/tunja_evenfy/
│   ├── config/          # Configuración (CORS, seguridad, rutas)
│   ├── dto/             # Data Transfer Objects con validación
│   ├── entity/          # Entidades JPA
│   ├── exception/       # Manejo global de errores
│   ├── repository/      # Repositorios Spring Data JPA
│   ├── rest/            # Controladores REST
│   └── service/         # Lógica de negocio
├── frontend/src/
│   ├── components/      # Componentes reutilizables
│   ├── context/         # AuthContext (JWT)
│   ├── pages/           # Páginas de la aplicación
│   ├── services/        # Cliente API (axios)
│   └── styles/          # Estilos globales
```
