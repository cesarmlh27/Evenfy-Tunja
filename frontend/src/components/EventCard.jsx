import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { eventService } from '../services/api'
import './event-card.css'

export function EventCard({ event, currentUser, onRefresh }) {
  const navigate = useNavigate()
  const [isAttending, setIsAttending] = useState(false)
  const [rating, setRating] = useState(0)
  const [loading, setLoading] = useState(false)

  const getEventImage = () => {
    const img = event.image_url || event.imageUrl || event.image
    if (!img) return null
    if (img.startsWith('/uploads/')) return `http://localhost:8080${img}`
    return img
  }

  const eventImage = getEventImage()
  const canEdit = event.created_by === currentUser?.id

  const handleAttendance = async () => {
    if (!currentUser?.id) {
      alert('Debes iniciar sesión para asistir')
      return
    }

    setLoading(true)
    try {
      await eventService.markAttendance(event.id, 'ATTENDING')
      setIsAttending(true)
      if (onRefresh) onRefresh()
    } catch {
      alert('Error al marcar asistencia')
    } finally {
      setLoading(false)
    }
  }

  const handleRating = async (value) => {
    if (!currentUser?.id) {
      alert('Debes iniciar sesión para calificar')
      return
    }

    try {
      await eventService.rateEvent(event.id, { rating: value })
      setRating(value)
      if (onRefresh) onRefresh()
    } catch {
      alert('Error al calificar evento')
    }
  }

  return (
    <div className="event-card">
      {eventImage && (
        <div className="event-card-image-wrap">
          <img src={eventImage} alt={event.title} className="event-card-image" />
        </div>
      )}

      <div className="event-header">
        <h3>{event.title}</h3>
        {event.created_by === currentUser?.id && <span className="badge-owner">👤 Tu evento</span>}
      </div>

      <p className="event-organizer">Organiza: {event.organizer_name || 'Organizador no disponible'}</p>

      <p className="event-description">{event.description}</p>

      <div className="event-meta">
        <div className="meta-item">
          <span className="label">Fecha:</span>
          <span>{event.event_date ? new Date(event.event_date).toLocaleDateString('es-CO') : 'N/A'}</span>
        </div>
        <div className="meta-item">
          <span className="label">Hora:</span>
          <span>{event.event_date ? new Date(event.event_date).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' }) : 'N/A'}</span>
        </div>
        <div className="meta-item">
          <span className="label">📍 Ubicación:</span>
          <span>{event.location || 'N/A'}</span>
        </div>
        <div className="meta-item">
          <span className="label">🎟️ Tipo:</span>
          <span>{event.is_free === false ? 'Pago' : 'Gratis'}</span>
        </div>
      </div>

      {(event.ticket_purchase_url || event.info_url) && (
        <div className="event-links">
          {event.ticket_purchase_url && (
            <a href={event.ticket_purchase_url} target="_blank" rel="noopener noreferrer" className="event-link">
              Comprar boletas
            </a>
          )}
          {event.info_url && (
            <a href={event.info_url} target="_blank" rel="noopener noreferrer" className="event-link">
              Ver información
            </a>
          )}
        </div>
      )}

      <div className="event-attendees">
        <div className="attendee-count">
          👥 <strong>{event.attendee_count || 0} {(event.attendee_count || 0) === 1 ? 'persona asistirá' : 'personas asistirán'} a este evento</strong>
        </div>
      </div>

      {event.average_rating > 0 && (
        <div className="event-rating">
          <span className="stars">⭐ {event.average_rating.toFixed(1)}</span>
        </div>
      )}

      <div className="event-actions">
        {canEdit && (
          <button
            className="btn btn-secondary"
            type="button"
            onClick={() => navigate(`/events/${event.id}/edit`)}
          >
            Editar
          </button>
        )}
        <button 
          className="btn btn-primary" 
          onClick={handleAttendance}
          disabled={loading || isAttending}
        >
          {isAttending ? '✓ Asistiré' : 'Asistiré'}
        </button>

        <div className="rating-section">
          <span className="label">Califica:</span>
          <div className="stars-input">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                className={`star ${star <= rating ? 'active' : ''}`}
                onClick={() => handleRating(star)}
                type="button"
              >
                ⭐
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default EventCard

