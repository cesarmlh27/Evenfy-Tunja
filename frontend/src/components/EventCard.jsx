import './event-card.css'

export default function EventCard({ event, onFavorite, isFavorite }) {
  const categoryColors = {
    'Cultura': { border: 'var(--verde-accent)', label: 'CULTURA' },
    'Música': { border: 'var(--azul-accent)', label: 'MÚSICA' },
    'Gastronomía': { border: 'var(--dorado-accent)', label: 'GASTRONOMÍA' },
    'Arte': { border: 'var(--rojo-accent)', label: 'ARTE' },
    'Danza': { border: 'var(--neon-purple)', label: 'DANZA' },
    'Comercio': { border: 'var(--neon-cyan)', label: 'COMERCIO' },
  }
  
  const categoryColor = categoryColors[event.category?.name] || categoryColors['Cultura']

  return (
    <div className="event-card" style={{ '--border-color': categoryColor.border }}>
      <div className="event-card-header">
        <span className="category-badge" style={{ borderColor: categoryColor.border, color: categoryColor.border }}>
          {categoryColor.label}
        </span>
        <button 
          className={`favorite-btn ${isFavorite ? 'active' : ''}`}
          onClick={() => onFavorite?.(event.id)}
          aria-label="Añadir a favoritos"
        >
          {isFavorite ? '❤️' : '🤍'}
        </button>
      </div>
      
      <h3 className="event-title">{event.title}</h3>
      <p className="event-description">{event.description}</p>
      
      <div className="event-meta">
        <div className="meta-item">
          <span>📍</span>
          <span>{event.location?.name}</span>
        </div>
        <div className="meta-item">
          <span>📅</span>
          <span>{new Date(event.startDate).toLocaleDateString('es-CO', { month: 'short', day: 'numeric' })}</span>
        </div>
      </div>
    </div>
  )
}
