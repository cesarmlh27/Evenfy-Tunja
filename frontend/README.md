# 🏛️ Tunja Evenfy — Frontend

Frontend en React + Vite para el sistema de visualización de eventos de Tunja, Boyacá.

## 🚀 Instalación y ejecución

```bash
# 1. Instalar dependencias
npm install

# 2. Ejecutar en modo desarrollo (requiere Spring Boot en :8080)
npm run dev

# 3. Abrir en el navegador
# http://localhost:5173
```

## 📁 Estructura del proyecto

```
src/
├── components/       # Componentes reutilizables
│   ├── Navbar.jsx    # Barra de navegación
│   ├── EventCard.jsx # Tarjeta de evento
│   ├── Footer.jsx    # Pie de página
│   └── Toast.jsx     # Notificaciones
├── context/
│   └── AppContext.jsx # Estado global (usuario, toasts)
├── pages/
│   ├── Home.jsx       # Página principal con hero
│   ├── Events.jsx     # Lista de eventos con filtros
│   ├── EventDetail.jsx# Detalle + comentarios + asistencia
│   └── Auth.jsx       # Login y Registro
├── services/
│   └── api.js         # Capa de comunicación con Spring Boot
└── index.css          # Variables CSS y estilos globales
```

## 🔌 Conexión con Spring Boot

El archivo `vite.config.js` tiene configurado un proxy:
- Todas las peticiones a `/api/*` → `http://localhost:8080`
- Asegúrate de que tu backend esté corriendo en el puerto **8080**

## 📡 Endpoints esperados del backend

| Entidad       | Endpoint base          |
|---------------|------------------------|
| Eventos       | `GET /api/events`      |
| Categorías    | `GET /api/categories`  |
| Ubicaciones   | `GET /api/locations`   |
| Usuarios      | `POST /api/users`      |
| Comentarios   | `GET /api/comments`    |
| Favoritos     | `GET /api/favorites`   |
| Asistencia    | `GET /api/event-attendance` |

## 🎨 Paleta de colores (inspirada en Tunja/Boyacá)

| Variable         | Color     | Uso                  |
|-----------------|-----------|----------------------|
| `--terracotta`  | #b05a3c   | Primario / CTAs      |
| `--gold`        | #d4a843   | Acentos / Brand      |
| `--stone`       | #1a1208   | Texto principal      |
| `--earth`       | #3d2b1f   | Textos secundarios   |
| `--cream`       | #f5edd8   | Fondos / Chips       |
| `--sky`         | #4a7fa5   | Fechas / Info        |
| `--green`       | #3a6b4a   | Éxito / Asistencia   |

## 📦 Build para producción

```bash
npm run build
# Archivos generados en /dist
```

## 🌐 Despliegue recomendado

- **Frontend**: Vercel (gratis) — `vercel deploy`
- **Backend**: Railway o Render (gratis)
- **DB PostgreSQL**: Supabase o Railway

---

Desarrollado por **JDC** | Portfolio Project 🇨🇴
