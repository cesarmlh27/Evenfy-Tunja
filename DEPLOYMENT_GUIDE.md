# Deployment Guide

## 1. Required environment variables (production)

Backend:
- SPRING_PROFILES_ACTIVE=prod
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- JWT_EXPIRATION
- MAIL_USERNAME
- MAIL_PASSWORD
- RESEND_API_KEY
- CORS_ALLOWED_ORIGINS

Frontend:
- If you deploy frontend and backend behind same domain/reverse proxy, keep VITE_API_URL=/api
- If different domains, set VITE_API_URL to full backend URL (example: https://api.example.com/api)

## 2. Local full stack with Docker Compose

```bash
docker compose up --build
```

Services:
- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Postgres: localhost:5432

## 3. Production with GHCR images

1. Push to main to trigger image publishing workflow.
2. Create `.env` in your server with production values.
3. Pull and run:

```bash
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## 4. Health checks

- Public health endpoint: `GET /actuator/health`
- Detailed health requires authorized access.

## 5. Security baseline included

- Secrets removed from default config.
- Actuator restricted (`/actuator/health` and `/actuator/info` are public; other actuator endpoints require ADMIN role).
- `anyRequest` now requires authentication in backend security chain.
- CORS origins are environment-driven.
