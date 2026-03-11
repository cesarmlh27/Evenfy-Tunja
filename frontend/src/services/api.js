import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

// Interceptor: adjunta el token JWT si existe
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Interceptor: manejo de errores global
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// ── EVENTOS ──────────────────────────────────────────────
export const eventService = {
  getAll:    ()           => api.get('/v1/events'),
  getById:   (id)         => api.get(`/v1/events/${id}`),
  create:    (data)       => api.post('/v1/events', data),
  update:    (id, data)   => api.put(`/v1/events/${id}`, data),
  delete:    (id)         => api.delete(`/v1/events/${id}`),
  getByCategory: (catId)  => api.get(`/v1/events?categoryId=${catId}`),
}

// ── CATEGORÍAS ───────────────────────────────────────────
export const categoryService = {
  getAll:  () => api.get('/v1/categories'),
  getById: (id) => api.get(`/v1/categories/${id}`),
  create:  (data) => api.post('/v1/categories', data),
}

// ── UBICACIONES ──────────────────────────────────────────
export const locationService = {
  getAll:  () => api.get('/v1/locations'),
  getById: (id) => api.get(`/v1/locations/${id}`),
  create:  (data) => api.post('/v1/locations', data),
}

// ── USUARIOS ─────────────────────────────────────────────
export const userService = {
  getAll:  () => api.get('/v1/users'),
  getById: (id) => api.get(`/v1/users/${id}`),
  create:  (data) => api.post('/v1/users', data),
  update:  (id, data) => api.put(`/v1/users/${id}`, data),
}

// ── COMENTARIOS ──────────────────────────────────────────
export const commentService = {
  getByEvent: (eventId)    => api.get(`/v1/comments?eventId=${eventId}`),
  create:     (data)       => api.post('/v1/comments', data),
  delete:     (id)         => api.delete(`/v1/comments/${id}`),
}

// ── FAVORITOS ────────────────────────────────────────────
export const favoriteService = {
  getByUser:  (userId)     => api.get(`/v1/favorites?userId=${userId}`),
  create:     (data)       => api.post('/v1/favorites', data),
  delete:     (id)         => api.delete(`/v1/favorites/${id}`),
}

// ── ASISTENCIA ───────────────────────────────────────────
export const attendanceService = {
  getByEvent: (eventId)    => api.get(`/v1/event-attendance?eventId=${eventId}`),
  create:     (data)       => api.post('/v1/event-attendance', data),
  delete:     (id)         => api.delete(`/v1/event-attendance/${id}`),
}

export default api
