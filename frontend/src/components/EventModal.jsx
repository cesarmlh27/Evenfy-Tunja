import './event-modal.css'

export default function EventModal({ event, onClose, categoryColors }) {
  const categoryColor = categoryColors[event.category?.name] || categoryColors['Cultura']
  
  const isFreeEvent = event.price === 'Gratuito'

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
          <div className="modal-header" style={{ background: event.image }}>
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
                    <p className="detail-value">{event.location?.name}</p>
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
                    {event.price}
                  </span>
                  {isFreeEvent && <span className="free-badge">Gratis</span>}
                </div>

                <div className="ticket-info">
                  <p className="ticket-label">Cómo acceder:</p>
                  <p className="ticket-value">{event.tickets}</p>
                </div>
              </div>
            </section>

            {/* CTA */}
            <div className="modal-footer">
              <button className="btn-primary" style={{ background: `linear-gradient(135deg, ${categoryColor.border}, var(--neon-cyan))` }}>
                🎟️ Comprar Entradas
              </button>
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
