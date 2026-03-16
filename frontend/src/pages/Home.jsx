import { useState, useEffect } from 'react'
import { eventService } from '../services/api'
import { useAuth } from '../context/AuthContext'
import EventModal from '../components/EventModal'
import { SkeletonGrid } from '../components/SkeletonLoader'
import tunjaBg from '../image/tunja-fondo.webp'
import '../styles/home.css'

const CATEGORY_COLOR_MAP = {
  cultura: '#59a880',
  musica: '#60a5fa',
  gastronomia: '#fbbf24',
  arte: '#ef4444',
  danza: '#d946ef',
  comercio: '#06b6d4',
  deporte: '#8b5cf6',
  conciertos: '#22d3ee',
  teatro: '#f97316',
  fiestas: '#ec4899',
}

const FALLBACK_COLORS = ['#59a880', '#60a5fa', '#fbbf24', '#ef4444', '#d946ef', '#06b6d4', '#8b5cf6', '#f97316', '#ec4899']

const normalizeCategoryName = (name = '') =>
  name
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()

const getCategoryColor = (name = '') => {
  const normalized = normalizeCategoryName(name)
  if (CATEGORY_COLOR_MAP[normalized]) return CATEGORY_COLOR_MAP[normalized]

  const hash = [...normalized].reduce((acc, ch) => acc + ch.charCodeAt(0), 0)
  return FALLBACK_COLORS[hash % FALLBACK_COLORS.length]
}

const getCategoryStyle = (categoryName = '') => ({
  border: getCategoryColor(categoryName),
  label: (categoryName || 'EVENTO').toUpperCase(),
})

export default function Home() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [selectedEvent, setSelectedEvent] = useState(null)
  const [showModal, setShowModal] = useState(false)
  const { user: currentUser } = useAuth()

  const normalizeImage = (ev) => {
    const img = ev.image_url || ev.imageUrl || ev.image;
    if (!img) return 'linear-gradient(135deg, #1e3a8a, #60a5fa)';
    if (img.startsWith('/uploads/')) return `http://localhost:8080${img}`;
    return img;
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const evRes = await eventService.getAll()
        const apiEvents = Array.isArray(evRes.data) ? evRes.data : []
        const normalized = apiEvents.map(ev => ({
          ...ev,
          startDate: ev.event_date || ev.eventDate || ev.startDate,
          category: typeof ev.category === 'string' ? { name: ev.category } : ev.category,
          categoryId: ev.category_id || ev.categoryId || '',
          locationName: typeof ev.location === 'string' ? ev.location : ev.location?.name,
          image: normalizeImage(ev),
          is_free: ev.is_free !== false,
          ticket_purchase_url: ev.ticket_purchase_url || null,
          info_url: ev.info_url || null,
          attendee_count: ev.attendee_count || 0,
          average_rating: ev.average_rating || 0,
          organizer_name: ev.organizer_name || '',
        }))
        setEvents(normalized)
      } catch {
        setEvents([])
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])

  const handleEventClick = (event) => {
    setSelectedEvent(event)
    setShowModal(true)
  }

  const handleCloseModal = () => {
    setShowModal(false)
    setTimeout(() => setSelectedEvent(null), 300)
  }

  const handleRefresh = async () => {
    try {
      const evRes = await eventService.getAll()
      const apiEvents = Array.isArray(evRes.data) ? evRes.data : []
      const normalized = apiEvents.map(ev => ({
        ...ev,
        startDate: ev.event_date || ev.eventDate || ev.startDate,
        category: typeof ev.category === 'string' ? { name: ev.category } : ev.category,
        categoryId: ev.category_id || ev.categoryId || '',
        locationName: typeof ev.location === 'string' ? ev.location : ev.location?.name,
        image: normalizeImage(ev),
        is_free: ev.is_free !== false,
        ticket_purchase_url: ev.ticket_purchase_url || null,
        info_url: ev.info_url || null,
        attendee_count: ev.attendee_count || 0,
        average_rating: ev.average_rating || 0,
        organizer_name: ev.organizer_name || '',
      }))
      setEvents(normalized)
    } catch {
      // Mantener eventos actuales si hay error
    }
  }

  const visibleEvents = events

  return (
    <main className="home">
      <section className="hero" style={{ backgroundImage: `url(${tunjaBg})` }}>
        <div className="hero-overlay"></div>
        <div className="hero-content">
          <h1 className="hero-title">Tunja Evenfy</h1>
          <p className="hero-subtitle">Descubre los mejores eventos de la ciudad</p>
          <div className="hero-tags">
            <span className="tag tag-verde"> Cultura</span>
            <span className="tag tag-azul"> Música</span>
            <span className="tag tag-dorado"> Gastronomía</span>
            <span className="tag tag-rojo"> Arte</span>
          </div>
        </div>
      </section>

      <section className="events-section">
        <div className="section-header">
          <h2>Próximos Eventos</h2>
          <p>Explora la vibrante escena de Tunja</p>
        </div>

        {loading ? (
          <SkeletonGrid count={6} />
        ) : events.length === 0 ? (
          <div className="empty-state">
            <p>🎭</p>
            <h3>No hay eventos disponibles</h3>
            <p>Vuelve pronto para descubrir nuevos eventos en Tunja.</p>
          </div>
        ) : (
          <div className="events-scroll-container">
            <div className="events-scroll">
              {visibleEvents.map((event, index) => {
                const categoryColor = getCategoryStyle(event.category?.name)
                return (
                  <div
                    key={event.id}
                    className="event-card-scroll"
                    style={{
                      '--border-color': categoryColor.border,
                      animationDelay: `${index * 0.05}s`,
                    }}
                    onClick={() => handleEventClick(event)}
                  >
                    <div className="event-image" style={
                      event.image?.startsWith('linear-gradient')
                        ? { background: event.image }
                        : { backgroundImage: `url(${event.image})`, backgroundSize: 'cover', backgroundPosition: 'center' }
                    }>
                      <span className="category-badge-scroll" style={{ borderColor: categoryColor.border, color: categoryColor.border }}>
                        {categoryColor.label}
                      </span>
                    </div>
                    
                    <div className="event-card-content">
                      <h3 className="event-title-scroll">{event.title}</h3>
                      <p className="event-description-scroll">{event.description}</p>
                      
                      <div className="event-meta-scroll">
                        <div className="meta-item-scroll">
                          <span>📍</span>
                          <span>{event.locationName || 'N/A'}</span>
                        </div>
                        <div className="meta-item-scroll">
                          <span>📅</span>
                          <span>{new Date(event.startDate).toLocaleDateString('es-CO', { month: 'short', day: 'numeric' })}</span>
                        </div>
                      </div>

                      <div className="event-meta-scroll">
                        <div className="meta-item-scroll">
                          <span>👤</span>
                          <span>{event.organizer_name || 'Organizador'}</span>
                        </div>
                        <div className="meta-item-scroll">
                          <span>🎟️</span>
                          <span>{event.is_free ? 'Gratis' : 'Pago'}</span>
                        </div>
                      </div>

                      <div className="event-stats-scroll">
                        <span className="stat-attendees">👥 {event.attendee_count || 0}</span>
                        {event.average_rating > 0 && <span className="stat-rating">⭐ {event.average_rating.toFixed(1)}</span>}
                      </div>
                      
                      <button className="event-btn-scroll">Ver detalles →</button>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>
        )}
      </section>

      {showModal && selectedEvent && (
        <EventModal event={selectedEvent} onClose={handleCloseModal} categoryColors={new Proxy({}, { get: (_, key) => getCategoryStyle(key) })} currentUser={currentUser} onRefresh={handleRefresh} />
      )}
    </main>
  )
}
