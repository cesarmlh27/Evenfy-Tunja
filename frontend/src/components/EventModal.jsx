import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { eventService, commentService, favoriteService } from '../services/api'
import './event-modal.css'

export default function EventModal({ event, onClose, categoryColors, currentUser, onRefresh }) {
  const navigate = useNavigate()
  const [isAttending, setIsAttending] = useState(false)
  const [isFavorite, setIsFavorite] = useState(false)
  const [rating, setRating] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [comments, setComments] = useState([])
  const [newComment, setNewComment] = useState('')
  const [commentsLoading, setCommentsLoading] = useState(false)
  const categoryColor = categoryColors[event.category?.name] || categoryColors['Cultura']

  const normalizedImage = (() => {
    const img = event.image_url || event.imageUrl || event.image
    if (!img) return 'linear-gradient(135deg, #1e3a8a, #60a5fa)'
    if (img.startsWith('/uploads/')) return `http://localhost:8080${img}`
    return img
  })()

  const modalHeaderStyle = normalizedImage.startsWith('linear-gradient')
    ? { background: normalizedImage }
    : { backgroundImage: `url(${normalizedImage})`, backgroundSize: 'cover', backgroundPosition: 'center' }

  const isFreeEvent = event.is_free !== false
  const canEdit = event.created_by === currentUser?.id || currentUser?.role === 'ADMIN'

  useEffect(() => {
    loadComments()
  }, [event.id])

  const loadComments = async () => {
    setCommentsLoading(true)
    try {
      const res = await commentService.getByEvent(event.id)
      setComments(res.data || [])
    } catch {
      // silently fail
    } finally {
      setCommentsLoading(false)
    }
  }

  const handleAddComment = async () => {
    if (!currentUser?.id) {
      setError('Necesitas iniciar sesión para comentar')
      return
    }
    if (!newComment.trim()) return

    try {
      await commentService.create({
        content: newComment.trim(),
        userId: currentUser.id,
        eventId: event.id,
      })
      setNewComment('')
      loadComments()
    } catch (err) {
      setError(err.response?.data?.message || 'Error al publicar comentario')
    }
  }

  const handleDeleteComment = async (commentId) => {
    if (!currentUser?.id) {
      setError('Necesitas iniciar sesión para eliminar comentarios')
      return
    }
    try {
      await commentService.delete(commentId)
      await loadComments()
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.response?.data?.error || 'No se pudo eliminar el comentario'
      setError(errorMsg)
    }
  }

  const handleToggleFavorite = async () => {
    if (!currentUser?.id) {
      setError('Necesitas iniciar sesión para guardar favoritos')
      return
    }
    try {
      await favoriteService.toggle({ userId: currentUser.id, eventId: event.id })
      setIsFavorite(!isFavorite)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al actualizar favorito')
    }
  }

  const handleAttendance = async () => {
    if (!currentUser?.id) {
      setError('Necesitas iniciar sesión para asistir')
      return
    }

    setLoading(true)
    setError(null)
    try {
      await eventService.markAttendance(event.id, 'ATTENDING')
      setIsAttending(true)
      if (onRefresh) onRefresh()
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.response?.data?.error || err.message || 'Error al marcar asistencia'
      setError(errorMsg)
    } finally {
      setLoading(false)
    }
  }

  const handleRating = async (value) => {
    if (!currentUser?.id) {
      setError('Necesitas iniciar sesión para calificar')
      return
    }

    setLoading(true)
    setError(null)
    try {
      await eventService.rateEvent(event.id, { rating: value, comment: '' })
      setRating(value)
      if (onRefresh) onRefresh()
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.response?.data?.error || err.message || 'Error al calificar evento'
      setError(errorMsg)
    } finally {
      setLoading(false)
    }
  }

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose()
    }
  }

  return (
    <div className="modal-backdrop" onClick={handleBackdropClick}>
      <div className="modal-container">
        <button className="modal-close" onClick={onClose} aria-label="Cerrar modal">
          ✕
        </button>

        <div className="modal-content">
          {/* Header con imagen */}
          <div className="modal-header" style={modalHeaderStyle}>
            <div className="modal-header-overlay"></div>
            <div className="modal-header-content">
              <span className="category-badge-modal" style={{ borderColor: categoryColor.border, color: categoryColor.border }}>
                {categoryColor.label}
              </span>
              <h1 className="modal-title">{event.title}</h1>
            </div>
          </div>

          {/* Body */}
          <div className="modal-body">
            {/* Descripción */}
            <section className="modal-section">
              <h3 className="section-title">Sobre este evento</h3>
              <p className="section-description">{event.description}</p>
            </section>

            {/* Detalles */}
            <section className="modal-section">
              <h3 className="section-title">Detalles</h3>
              <div className="details-grid">
                <div className="detail-item">
                  <span className="detail-icon">📅</span>
                  <div>
                    <p className="detail-label">Fecha</p>
                    <p className="detail-value">
                      {new Date(event.startDate).toLocaleDateString('es-CO', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
                    </p>
                    {event.endDate && event.endDate !== event.startDate && (
                      <p className="detail-value-secondary">
                        hasta {new Date(event.endDate).toLocaleDateString('es-CO', { month: 'short', day: 'numeric' })}
                      </p>
                    )}
                  </div>
                </div>

                <div className="detail-item">
                  <span className="detail-icon">📍</span>
                  <div>
                    <p className="detail-label">Ubicación</p>
                    <p className="detail-value">{event.locationName || event.location?.name || event.location || 'N/A'}</p>
                  </div>
                </div>

                <div className="detail-item">
                  <span className="detail-icon">🏷️</span>
                  <div>
                    <p className="detail-label">Categoría</p>
                    <p className="detail-value">{event.category?.name}</p>
                  </div>
                </div>
              </div>
            </section>

            {/* Precio y Entradas */}
            <section className="modal-section">
              <h3 className="section-title">Entradas y Acceso</h3>
              <div className="pricing-box" style={{ borderColor: categoryColor.border }}>
                <div className="price-display">
                  <span className="price-label">Precio</span>
                  <span className={`price-value ${isFreeEvent ? 'free' : 'paid'}`}>
                    {isFreeEvent ? 'Gratuito' : 'De pago'}
                  </span>
                  {isFreeEvent && <span className="free-badge">Gratis</span>}
                </div>

                <div className="ticket-info">
                  <p className="ticket-label">Cómo acceder:</p>
                  {event.ticket_purchase_url && (
                    <p className="ticket-value"><a href={event.ticket_purchase_url} target="_blank" rel="noopener noreferrer">Comprar boletas</a></p>
                  )}
                  {event.info_url && (
                    <p className="ticket-value"><a href={event.info_url} target="_blank" rel="noopener noreferrer">Más información</a></p>
                  )}
                </div>
              </div>
            </section>

            {/* Asistentes y Calificación */}
            <section className="modal-section">
              <h3 className="section-title">Participación</h3>
              <div className="participation-grid">
                <div className="participation-item">
                  <span className="participation-label">👥 Asistentes</span>
                  <span className="participation-value">{event.attendee_count || 0}{event.attendee_count === 1 ? ' persona asistirá' : ' personas asistirán'}</span>
                </div>
                {event.average_rating > 0 && (
                  <div className="participation-item">
                    <span className="participation-label">⭐ Calificación</span>
                    <span className="participation-value">{event.average_rating.toFixed(1)}/5</span>
                  </div>
                )}
              </div>
            </section>

            {/* Acciones del Usuario */}
            <section className="modal-section">
              <h3 className="section-title">Tu Participación</h3>
              {error && <div className="error-message" style={{ marginBottom: 'var(--spacing-lg)', padding: 'var(--spacing-md)', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid #ef4444', borderRadius: '8px', color: '#ef4444' }}>⚠️ {error}</div>}
              <div className="user-actions">
                <button 
                  className="btn-attend" 
                  onClick={handleAttendance}
                  disabled={loading || isAttending}
                  style={{ background: categoryColor.border }}
                >
                  {isAttending ? '✓ Asistiré' : '🎫 Asistiré'}
                </button>

                <button 
                  className="btn-attend"
                  onClick={handleToggleFavorite}
                  style={{ background: isFavorite ? 'var(--rojo-accent)' : 'var(--bg-tertiary)' }}
                >
                  {isFavorite ? '💔 Quitar favorito' : '❤️ Favorito'}
                </button>

                {canEdit && (
                  <button
                    className="btn-attend"
                    onClick={() => {
                      onClose()
                      navigate(`/events/${event.id}/edit`)
                    }}
                    style={{ background: '#0f766e' }}
                  >
                    ✏️ Editar evento
                  </button>
                )}

                <div className="rating-section">
                  <span className="rating-label">Califica este evento:</span>
                  <div className="stars-container">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        className={`star-btn ${star <= rating ? 'active' : ''}`}
                        onClick={() => handleRating(star)}
                        type="button"
                        disabled={loading}
                        title={`Calificar ${star} estrella${star > 1 ? 's' : ''}`}
                      >
                        ⭐
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </section>

            {/* Comentarios */}
            <section className="modal-section">
              <h3 className="section-title">Comentarios ({comments.length})</h3>
              
              {currentUser && (
                <div className="comment-form" style={{ display: 'flex', gap: 'var(--spacing-sm)', marginBottom: 'var(--spacing-lg)' }}>
                  <input
                    type="text"
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Escribe un comentario..."
                    onKeyDown={(e) => e.key === 'Enter' && handleAddComment()}
                    style={{
                      flex: 1, padding: 'var(--spacing-sm) var(--spacing-md)',
                      background: 'var(--bg-secondary)', border: '1px solid var(--bg-tertiary)',
                      borderRadius: '8px', color: 'var(--text-primary)', fontSize: '0.9rem',
                    }}
                  />
                  <button
                    onClick={handleAddComment}
                    style={{
                      padding: 'var(--spacing-sm) var(--spacing-md)',
                      background: categoryColor.border, color: 'var(--bg-primary)',
                      border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 700,
                    }}
                  >
                    Enviar
                  </button>
                </div>
              )}

              <div className="comments-list" style={{ display: 'flex', flexDirection: 'column', gap: 'var(--spacing-md)', maxHeight: '300px', overflowY: 'auto' }}>
                {commentsLoading ? (
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Cargando comentarios...</p>
                ) : comments.length === 0 ? (
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No hay comentarios aún. ¡Sé el primero!</p>
                ) : comments.map((c) => (
                  <div key={c.id} style={{
                    padding: 'var(--spacing-md)', background: 'var(--bg-secondary)',
                    borderRadius: '8px', border: '1px solid var(--bg-tertiary)',
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-sm)', marginBottom: 'var(--spacing-xs)' }}>
                      {c.userAvatar && <img src={c.userAvatar} alt="" style={{ width: 24, height: 24, borderRadius: '50%' }} />}
                      <strong style={{ fontSize: '0.85rem', color: 'var(--verde-accent)' }}>{c.userName || 'Anónimo'}</strong>
                      <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginLeft: 'auto' }}>
                        {c.createdAt ? new Date(c.createdAt).toLocaleDateString('es-CO') : ''}
                      </span>
                      {(currentUser?.role === 'ADMIN' || event.created_by === currentUser?.id || c.userId === currentUser?.id) && (
                        <button
                          type="button"
                          onClick={() => handleDeleteComment(c.id)}
                          style={{
                            marginLeft: '8px',
                            padding: '4px 8px',
                            background: 'rgba(239, 68, 68, 0.2)',
                            border: '1px solid #ef4444',
                            color: '#ef4444',
                            borderRadius: '6px',
                            cursor: 'pointer',
                            fontSize: '0.75rem',
                            fontWeight: 700,
                          }}
                          title="Eliminar comentario"
                        >
                          Eliminar
                        </button>
                      )}
                    </div>
                    <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>{c.content}</p>
                  </div>
                ))}
              </div>
            </section>

            {/* CTA */}
            <div className="modal-footer">
              <button className="btn-secondary" onClick={onClose}>
                Cerrar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
