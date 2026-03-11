import { useState, useEffect } from 'react'
import { eventService, categoryService } from '../services/api'
import EventModal from '../components/EventModal'
import tunjaBg from '../image/tunja-fondo.jpg'
import '../styles/home.css'

const MOCK_EVENTS = [
  { id: 1, title: 'Festival de Luces de Tunja', description: 'Una noche mágica con iluminación artística', startDate: '2025-06-15', endDate: '2025-06-16', category: { name: 'Cultura' }, location: { name: 'Plaza de Bolívar' }, image: 'linear-gradient(135deg, #2d5016, #4ade80)', price: 'Gratuito', tickets: 'Entrada libre' },
  { id: 2, title: 'Concierto en el Parque', description: 'Música en vivo con artistas locales', startDate: '2025-06-22', endDate: '2025-06-22', category: { name: 'Música' }, location: { name: 'Parque Pinzón' }, image: 'linear-gradient(135deg, #1e3a8a, #60a5fa)', price: '25.000 COP', tickets: 'Comprar en Eventbrite' },
  { id: 3, title: 'Feria Gastronómica', description: 'Sabores tradicionales de Boyacá', startDate: '2025-07-04', endDate: '2025-07-04', category: { name: 'Gastronomía' }, location: { name: 'Centro Histórico' }, image: 'linear-gradient(135deg, #b45309, #fbbf24)', price: 'Gratuito', tickets: 'Acceso libre' },
  { id: 4, title: 'Exposición de Arte Local', description: 'Obras de artistas tunjanos', startDate: '2025-07-10', endDate: '2025-07-20', category: { name: 'Arte' }, location: { name: 'Museo de Tunja' }, image: 'linear-gradient(135deg, #7f1d1d, #ef4444)', price: '15.000 COP', tickets: 'Boletería en museo' },
  { id: 5, title: 'Noche de Danza', description: 'Danza folclórica y contemporánea', startDate: '2025-07-18', endDate: '2025-07-18', category: { name: 'Danza' }, location: { name: 'Teatro Municipal' }, image: 'linear-gradient(135deg, #7c3aed, #d946ef)', price: '30.000 COP', tickets: 'Teleticket' },
  { id: 6, title: 'Mercado de Artesanías', description: 'Productos locales y artesanas de la región', startDate: '2025-07-25', endDate: '2025-07-25', category: { name: 'Comercio' }, location: { name: 'Centro Histórico' }, image: 'linear-gradient(135deg, #0891b2, #06b6d4)', price: 'Gratuito', tickets: 'Compra directa' },
  { id: 7, title: 'Taller de Cerámica Prehispánica', description: 'Aprende técnicas ancestrales de alfarería', startDate: '2025-08-01', endDate: '2025-08-01', category: { name: 'Cultura' }, location: { name: 'Casa Terracotta' }, image: 'linear-gradient(135deg, #854d0e, #fbbf24)', price: '50.000 COP', tickets: 'Inscripciones cerradas' },
  { id: 8, title: 'Festival de Comidas Rápidas', description: 'Sabores del mundo en un mismo lugar', startDate: '2025-08-10', endDate: '2025-08-10', category: { name: 'Gastronomía' }, location: { name: 'Parque La Laguna' }, image: 'linear-gradient(135deg, #ea580c, #fbbf24)', price: 'Gratuito', tickets: 'Entrada libre' },
]

const categoryColors = {
  'Cultura': { border: 'var(--verde-accent)', label: 'CULTURA' },
  'Música': { border: 'var(--azul-accent)', label: 'MÚSICA' },
  'Gastronomía': { border: 'var(--dorado-accent)', label: 'GASTRONOMÍA' },
  'Arte': { border: 'var(--rojo-accent)', label: 'ARTE' },
  'Danza': { border: 'var(--neon-purple)', label: 'DANZA' },
  'Comercio': { border: 'var(--neon-cyan)', label: 'COMERCIO' },
}

export default function Home() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [selectedEvent, setSelectedEvent] = useState(null)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const evRes = await eventService.getAll()
        setEvents(evRes.data || MOCK_EVENTS)
      } catch {
        setEvents(MOCK_EVENTS)
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
          <div className="loading">
            <div className="spinner"></div>
            <p>Cargando eventos increíbles...</p>
          </div>
        ) : (
          <div className="events-scroll-container">
            <div className="events-scroll">
              {events.map((event, index) => {
                const categoryColor = categoryColors[event.category?.name] || categoryColors['Cultura']
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
                    <div className="event-image" style={{ background: event.image }}>
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
                          <span>{event.location?.name}</span>
                        </div>
                        <div className="meta-item-scroll">
                          <span>📅</span>
                          <span>{new Date(event.startDate).toLocaleDateString('es-CO', { month: 'short', day: 'numeric' })}</span>
                        </div>
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
        <EventModal event={selectedEvent} onClose={handleCloseModal} categoryColors={categoryColors} />
      )}
    </main>
  )
}
