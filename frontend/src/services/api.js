import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || '/api'

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
})

// Interceptor: adjunta el token JWT si existe
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Interceptor: manejo de errores global
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// ── AUTH ─────────────────────────────────────────────────
export const authService = {
  register: (data)     => api.post('/v1/auth/register', data),
  login:    (data)     => api.post('/v1/auth/login', data),
  verify2FA: (data)    => api.post('/v1/auth/verify-2fa', data),
}

// ── USUARIOS ─────────────────────────────────────────────
export const userService = {
  getAll:         ()       => api.get('/v1/users'),
  getById:        (id)     => api.get(`/v1/users/${id}`),
  getProfile:     ()       => api.get('/v1/users/profile/me'),
  updateProfile:  (data)   => api.patch('/v1/users/profile/me', data),
  update:         (id, data) => api.put(`/v1/users/${id}`, data),
  delete:         (id)     => api.delete(`/v1/users/${id}`),
  changeRole:     (id, role) => api.patch(`/v1/users/${id}/role`, { role }),
}

// ── EVENTOS ──────────────────────────────────────────────
export const eventService = {
  getAll:             ()            => api.get('/v1/events'),
  getById:            (id)          => api.get(`/v1/events/${id}`),
  search:             (params)      => api.get('/v1/events/search', { params }),
  create:             (data)        => api.post('/v1/events', data),
  update:             (id, data)    => api.put(`/v1/events/${id}`, data),
  delete:             (id)          => api.delete(`/v1/events/${id}`),
  getAttendees:       (id)          => api.get(`/v1/events/${id}/attendees`),
  getRatings:         (id)          => api.get(`/v1/events/${id}/ratings`),
  markAttendance:     (id, status)  => api.post(`/v1/events/${id}/attend`, { status }),
  rateEvent:          (id, data)    => api.post(`/v1/events/${id}/rate`, data),
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

// ── COMENTARIOS ──────────────────────────────────────────
export const commentService = {
  getByEvent: (eventId)    => api.get(`/v1/comments/event/${eventId}`),
  create:     (data)       => api.post('/v1/comments', data),
  update:     (id, data)   => api.put(`/v1/comments/${id}`, data),
  delete:     (id)         => api.delete(`/v1/comments/${id}`),
}

// ── FAVORITOS ────────────────────────────────────────────
export const favoriteService = {
  getMyFavorites: ()       => api.get('/v1/favorites/me'),
  toggle:         (data)   => api.post('/v1/favorites', data),
  delete:         (id)     => api.delete(`/v1/favorites/${id}`),
}

export default api

// ── UPLOAD ───────────────────────────────────────────────
export const uploadService = {
  uploadImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/v1/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
