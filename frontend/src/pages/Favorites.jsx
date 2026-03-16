import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { favoriteService, eventService } from '../services/api'
import EventModal from '../components/EventModal'
import { SkeletonGrid } from '../components/SkeletonLoader'
import { useToast } from '../components/Toast'
import './Favorites.css'

const categoryColors = {
  'Cultura': { border: 'var(--verde-accent)', label: 'CULTURA' },
  'Música': { border: 'var(--azul-accent)', label: 'MÚSICA' },
  'Gastronomía': { border: 'var(--dorado-accent)', label: 'GASTRONOMÍA' },
  'Arte': { border: 'var(--rojo-accent)', label: 'ARTE' },
  'Danza': { border: 'var(--neon-purple)', label: 'DANZA' },
  'Comercio': { border: 'var(--neon-cyan)', label: 'COMERCIO' },
}

export default function Favorites() {
  const { user, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const toast = useToast()
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)
  const [selectedEvent, setSelectedEvent] = useState(null)

  const normalizeImage = (ev) => {
    const img = ev.image_url || ev.imageUrl || ev.image
    if (!img) return 'linear-gradient(135deg, #1e3a8a, #60a5fa)'
    if (img.startsWith('/uploads/')) return `http://localhost:8080${img}`
    return img
  }

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    fetchFavorites()
  }, [isAuthenticated])

  const fetchFavorites = async () => {
    setLoading(true)
    try {
      const favRes = await favoriteService.getMyFavorites()
      const favs = favRes.data || []

      // Cargar detalles de cada evento
      const eventPromises = favs.map(f =>
        eventService.getById(f.eventId || f.event_id).then(r => ({
          ...r.data,
          favoriteId: f.id,
          startDate: r.data.event_date || r.data.eventDate || r.data.startDate,
          category: typeof r.data.category === 'string' ? { name: r.data.category } : r.data.category,
          location: typeof r.data.location === 'string' ? { name: r.data.location } : r.data.location,
          image: normalizeImage(r.data),
          attendee_count: r.data.attendee_count || 0,
          average_rating: r.data.average_rating || 0,
        })).catch(() => null)
      )

      const events = (await Promise.all(eventPromises)).filter(Boolean)
      setFavorites(events)
    } catch {
      toast.error('Error cargando favoritos')
    } finally {
      setLoading(false)
    }
  }

  const handleRemoveFavorite = async (event) => {
    try {
      await favoriteService.toggle({ userId: user.id, eventId: event.id })
      setFavorites(prev => prev.filter(f => f.id !== event.id))
      toast.success('Favorito removido')
    } catch {
      toast.error('Error al remover favorito')
    }
  }

  return (
    <main className="favorites-page">
      <div className="favorites-header">
        <h1>❤️ Mis Favoritos</h1>
        <p>Tus eventos guardados</p>
      </div>

      {loading ? (
        <SkeletonGrid count={4} />
      ) : favorites.length === 0 ? (
        <div className="empty-favorites">
          <h2>No tienes favoritos aún</h2>
          <p>Explora eventos y añádelos a tus favoritos</p>
          <button onClick={() => navigate('/search')} className="btn-explore">
            🔍 Explorar Eventos
          </button>
        </div>
      ) : (
        <div className="favorites-grid">
          {favorites.map((event, idx) => {
            const cc = categoryColors[event.category?.name] || { border: 'var(--verde-accent)', label: 'EVENTO' }
            return (
              <div key={event.id} className="favorite-card" style={{ animationDelay: `${idx * 0.05}s` }}>
                <div
                  className="fav-image"
                  style={
                    event.image?.startsWith('linear-gradient')
                      ? { background: event.image }
                      : { backgroundImage: `url(${event.image})`, backgroundSize: 'cover', backgroundPosition: 'center' }
                  }
                  onClick={() => setSelectedEvent(event)}
                >
                  <span className="fav-badge" style={{ borderColor: cc.border, color: cc.border }}>{cc.label}</span>
                </div>
                <div className="fav-info">
                  <h3 onClick={() => setSelectedEvent(event)}>{event.title}</h3>
                  <div className="fav-meta">
                    <span>📍 {event.location?.name || 'N/A'}</span>
                    <span>📅 {event.startDate ? new Date(event.startDate).toLocaleDateString('es-CO') : 'N/A'}</span>
                  </div>
                  <button className="btn-remove-fav" onClick={() => handleRemoveFavorite(event)}>
                    💔 Remover
                  </button>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {selectedEvent && (
        <EventModal
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
          categoryColors={categoryColors}
          currentUser={user}
          onRefresh={fetchFavorites}
        />
      )}
    </main>
  )
}
