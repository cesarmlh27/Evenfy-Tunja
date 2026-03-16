import { useState, useEffect, useCallback } from 'react'
import { useSearchParams } from 'react-router-dom'
import { eventService, categoryService } from '../services/api'
import { useAuth } from '../context/AuthContext'
import EventModal from '../components/EventModal'
import { SkeletonGrid } from '../components/SkeletonLoader'
import './SearchEvents.css'

const categoryColors = {
  'Cultura':     { border: '#59a880', label: 'CULTURA' },
  'Música':      { border: '#60a5fa', label: 'MÚSICA' },
  'Gastronomía': { border: '#fbbf24', label: 'GASTRONOMÍA' },
  'Arte':        { border: '#ef4444', label: 'ARTE' },
  'Danza':       { border: '#d946ef', label: 'DANZA' },
  'Comercio':    { border: '#06b6d4', label: 'COMERCIO' },
  'Deporte':     { border: '#8b5cf6', label: 'DEPORTE' },
}

const normalizeImage = (ev) => {
  const img = ev.image_url || ev.imageUrl || ev.image
  if (!img) return 'linear-gradient(135deg, #1e3a8a, #60a5fa)'
  if (img.startsWith('/uploads/')) return `http://localhost:8080${img}`
  return img
}

export default function SearchEvents() {
  const { user } = useAuth()
  const [searchParams] = useSearchParams()

  const [events, setEvents]               = useState([])
  const [categories, setCategories]       = useState([])
  const [loading, setLoading]             = useState(false)
  const [selectedEvent, setSelectedEvent] = useState(null)
  const [page, setPage]                   = useState(0)
  const [totalPages, setTotalPages]       = useState(0)
  const [dropdownOpen, setDropdownOpen]   = useState(false)
  const [titleInput, setTitleInput]       = useState('')
  const [activeCatId, setActiveCatId]     = useState(() => searchParams.get('categoryId') || '')

  // Load categories once on mount
  useEffect(() => {
    categoryService.getAll()
      .then(res => setCategories(res.data || []))
      .catch(() => {})
  }, [])

  const fetchEvents = useCallback(async (pageNum, catId, title) => {
    setLoading(true)
    try {
      const params = { page: pageNum, size: 12 }
      if (title?.trim()) params.title = title.trim()
      if (catId) params.categoryId = catId

      const res = await eventService.search(params)
      const data = res.data || {}
      const content = data.content || []

      setEvents(content.map(ev => ({
        ...ev,
        startDate:           ev.event_date || ev.eventDate || ev.startDate,
        category:            typeof ev.category === 'string' ? { name: ev.category } : ev.category,
        locationName:        typeof ev.location === 'string' ? ev.location : (ev.location?.name || ev.location_text || ''),
        image:               normalizeImage(ev),
        is_free:             ev.is_free !== false,
        ticket_purchase_url: ev.ticket_purchase_url || null,
        info_url:            ev.info_url || null,
        organizer_name:      ev.organizer_name || '',
        attendee_count:      ev.attendee_count || 0,
        average_rating:      ev.average_rating || 0,
      })))
      setPage(data.number ?? 0)
      setTotalPages(data.totalPages ?? 1)
    } catch (e) {
      console.error('Search error:', e)
      setEvents([])
    } finally {
      setLoading(false)
    }
  }, [])

  // Run on mount and when URL params change (navbar navigation)
  useEffect(() => {
    const urlCat = searchParams.get('categoryId') || ''
    setActiveCatId(urlCat)
    setTitleInput('')
    fetchEvents(0, urlCat, '')
  }, [searchParams, fetchEvents])

  const handleCategorySelect = (catId) => {
    setActiveCatId(catId)
    setDropdownOpen(false)
    fetchEvents(0, catId, titleInput)
  }

  const handleSearch = () => fetchEvents(0, activeCatId, titleInput)

  return (
    <main className="search-page">
      <div className="search-header">
        <h1>Buscar Eventos</h1>
        <p>Encuentra eventos en Tunja por nombre o categoría</p>
      </div>

      <div className="filter-bar">
        <div className="filter-search-wrap">
          <input
            type="text"
            value={titleInput}
            onChange={e => setTitleInput(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleSearch()}
            placeholder="🔍 Buscar eventos..."
            className="filter-title-input"
          />
          <button className="btn-search-icon" onClick={handleSearch}>Buscar</button>
        </div>

        <div className="category-dropdown-wrap">
          <div
            className={`category-trigger${dropdownOpen ? ' open' : ''}`}
            onClick={() => setDropdownOpen(o => !o)}
          >
            <span>🏷️ {categories.find(c => c.id?.toString() === activeCatId)?.name || 'Todas las categorías'}</span>
            <span className="dropdown-arrow">▾</span>
          </div>
          {dropdownOpen && (
            <ul className="category-dropdown-list">
              <li
                className={`category-option${!activeCatId ? ' active' : ''}`}
                onClick={() => handleCategorySelect('')}
              >
                <span className="cat-dot" style={{ background: '#888' }}></span>
                Todas las categorías
              </li>
              {categories.map(c => {
                const cc = categoryColors[c.name] || { border: '#59a880' }
                return (
                  <li
                    key={c.id}
                    className={`category-option${activeCatId === c.id?.toString() ? ' active' : ''}`}
                    onClick={() => handleCategorySelect(c.id.toString())}
                  >
                    <span className="cat-dot" style={{ background: cc.border }}></span>
                    {c.name}
                  </li>
                )
              })}
            </ul>
          )}
        </div>
      </div>

      <section className="search-results">
        {loading ? (
          <SkeletonGrid count={6} />
        ) : events.length === 0 ? (
          <div className="empty-state">
            <h2>No se encontraron eventos</h2>
            <p>Intenta con otros filtros</p>
          </div>
        ) : (
          <>
            <div className="events-grid-search">
              {events.map((event, idx) => {
                const cc = categoryColors[event.category?.name] || { border: '#59a880', label: event.category?.name?.toUpperCase() || 'EVENTO' }
                return (
                  <div
                    key={event.id}
                    className="event-card-search"
                    style={{ '--border-color': cc.border, animationDelay: `${idx * 0.05}s` }}
                    onClick={() => setSelectedEvent(event)}
                  >
                    <div
                      className="event-image-search"
                      style={
                        event.image?.startsWith('linear-gradient')
                          ? { background: event.image }
                          : { backgroundImage: `url(${event.image})`, backgroundSize: 'cover', backgroundPosition: 'center' }
                      }
                    >
                      <span className="category-badge-search" style={{ borderColor: cc.border, color: cc.border }}>
                        {cc.label}
                      </span>
                    </div>
                    <div className="event-info-search">
                      <h3>{event.title}</h3>
                      <p>{event.description?.substring(0, 80)}{event.description?.length > 80 ? '...' : ''}</p>
                      <div className="meta-row">
                        <span>📍 {event.locationName || 'N/A'}</span>
                        <span>📅 {event.startDate ? new Date(event.startDate).toLocaleDateString('es-CO') : 'N/A'}</span>
                      </div>
                      <div className="meta-row">
                        <span>👤 {event.organizer_name || 'Organizador'}</span>
                        <span>🎟️ {event.is_free ? 'Gratis' : 'Pago'}</span>
                      </div>
                      <div className="stats-row">
                        <span>👥 {event.attendee_count}</span>
                        {event.average_rating > 0 && <span>⭐ {event.average_rating.toFixed(1)}</span>}
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>

            {totalPages > 1 && (
              <div className="pagination">
                <button disabled={page <= 0} onClick={() => fetchEvents(page - 1, activeCatId, titleInput)}>← Anterior</button>
                <span>Página {page + 1} de {totalPages}</span>
                <button disabled={page >= totalPages - 1} onClick={() => fetchEvents(page + 1, activeCatId, titleInput)}>Siguiente →</button>
              </div>
            )}
          </>
        )}
      </section>

      {selectedEvent && (
        <EventModal
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
          categoryColors={categoryColors}
          currentUser={user}
          onRefresh={() => fetchEvents(page, activeCatId, titleInput)}
        />
      )}
    </main>
  )
}
