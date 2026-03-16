import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { userService, eventService } from '../services/api'
import EventModal from './EventModal'
import '../styles/profile.css'

const categoryColors = {
  'Cultura':     { border: '#59a880', label: 'CULTURA' },
  'Música':      { border: '#60a5fa', label: 'MÚSICA' },
  'Gastronomía': { border: '#fbbf24', label: 'GASTRONOMÍA' },
  'Arte':        { border: '#ef4444', label: 'ARTE' },
  'Danza':       { border: '#d946ef', label: 'DANZA' },
  'Comercio':    { border: '#06b6d4', label: 'COMERCIO' },
  'Deporte':     { border: '#8b5cf6', label: 'DEPORTE' },
}

export default function UserProfile() {
  const navigate = useNavigate()
  const { user, logout, isAuthenticated } = useAuth()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [activeTab, setActiveTab] = useState('attending')
  const [editing, setEditing] = useState(false)
  const [editForm, setEditForm] = useState({ fullName: '', avatarUrl: '', bio: '' })
  const [saving, setSaving] = useState(false)
  const [selectedEvent, setSelectedEvent] = useState(null)
  const [eventLoading, setEventLoading] = useState(false)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    fetchProfile()
  }, [isAuthenticated])

  const fetchProfile = async () => {
    try {
      const response = await userService.getProfile()
      setProfile(response.data)
      setEditForm({
        fullName: response.data.fullName || '',
        avatarUrl: response.data.avatarUrl || '',
        bio: response.data.bio || '',
      })
    } catch (err) {
      // Fallback al usuario del contexto
      if (user) {
        setProfile({
          fullName: user.fullName,
          email: user.email,
          role: user.role || 'USER',
          createdEvents: [],
          attendingEvents: [],
          totalEventsCreated: 0,
          totalEventsAttending: 0,
        })
      }
    } finally {
      setLoading(false)
    }
  }

  const handleSaveProfile = async () => {
    setSaving(true)
    try {
      const response = await userService.updateProfile(editForm)
      setProfile(response.data)
      setEditing(false)
    } catch (err) {
      setError('Error al guardar perfil')
    } finally {
      setSaving(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const normalizeImage = (ev) => {
    const img = ev.image_url || ev.imageUrl || ev.image
    if (!img) return 'linear-gradient(135deg, #1e3a8a, #60a5fa)'
    if (img.startsWith('/uploads/')) return `http://localhost:8080${img}`
    return img
  }

  const openEventDetails = async (eventId) => {
    setEventLoading(true)
    try {
      const res = await eventService.getById(eventId)
      const ev = res.data || {}
      setSelectedEvent({
        ...ev,
        startDate: ev.event_date || ev.eventDate,
        category: typeof ev.category === 'string' ? { name: ev.category } : ev.category,
        locationName: typeof ev.location === 'string' ? ev.location : ev.location?.name,
        image: normalizeImage(ev),
      })
    } catch {
      setError('No se pudo cargar el detalle del evento')
    } finally {
      setEventLoading(false)
    }
  }

  const handleEditEvent = (eventId) => {
    navigate(`/events/${eventId}/edit`)
  }

  const handleDeleteEvent = async (eventId) => {
    const ok = window.confirm('¿Seguro que deseas eliminar este evento?')
    if (!ok) return
    try {
      await eventService.delete(eventId)
      await fetchProfile()
    } catch {
      setError('No se pudo eliminar el evento')
    }
  }

  if (loading) return <div className="profile-container"><p>Cargando perfil...</p></div>
  if (!profile) return <div className="profile-container"><p>Perfil no encontrado</p></div>

  const canCreateEvents = ['ADMIN', 'ORGANIZER'].includes(profile.role)
  const roleLabel = profile.role === 'ADMIN' ? '👑 Administrador' : profile.role === 'ORGANIZER' ? '🎭 Organizador' : '👤 Asistente'

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-info">
          {profile.avatarUrl && (
            <img src={profile.avatarUrl} alt="Avatar" className="profile-avatar" />
          )}
          {editing ? (
            <div className="edit-form">
              <div className="form-group">
                <label>Nombre</label>
                <input
                  type="text"
                  value={editForm.fullName}
                  onChange={(e) => setEditForm({ ...editForm, fullName: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>Avatar URL</label>
                <input
                  type="text"
                  value={editForm.avatarUrl}
                  onChange={(e) => setEditForm({ ...editForm, avatarUrl: e.target.value })}
                  placeholder="https://..."
                />
              </div>
              <div className="form-group">
                <label>Bio</label>
                <textarea
                  value={editForm.bio}
                  onChange={(e) => setEditForm({ ...editForm, bio: e.target.value })}
                  placeholder="Cuéntanos sobre ti..."
                  rows={3}
                />
              </div>
              <div className="edit-actions">
                <button className="btn-save" onClick={handleSaveProfile} disabled={saving}>
                  {saving ? 'Guardando...' : '✓ Guardar'}
                </button>
                <button className="btn-cancel" onClick={() => setEditing(false)}>Cancelar</button>
              </div>
            </div>
          ) : (
            <>
              <h1>{profile.fullName}</h1>
              <p className="profile-email">{profile.email}</p>
              {profile.bio && <p className="profile-bio">{profile.bio}</p>}
              <span className={`profile-role role-${profile.role?.toLowerCase()}`}>
                {roleLabel}
              </span>
              <button className="btn-edit" onClick={() => setEditing(true)}>✏️ Editar perfil</button>
            </>
          )}
        </div>

        <button onClick={handleLogout} className="logout-btn">
          Cerrar Sesión
        </button>
      </div>

      {error && <div className="profile-error">{error}</div>}

      {/* RESUMEN */}
      <div className="profile-stats">
        {canCreateEvents && (
          <div className="stat-card">
            <h3>{profile.totalEventsCreated}</h3>
            <p>Eventos Creados</p>
          </div>
        )}
        <div className="stat-card">
          <h3>{profile.totalEventsAttending}</h3>
          <p>Eventos Asistiendo</p>
        </div>
      </div>

      {/* TABS */}
      <div className="profile-content">
        <div className="profile-tabs">
          {canCreateEvents && (
            <button
              className={`tab ${activeTab === 'created' ? 'active' : ''}`}
              onClick={() => setActiveTab('created')}
            >
              🎪 Mis Eventos ({profile.totalEventsCreated})
            </button>
          )}
          <button
            className={`tab ${activeTab === 'attending' ? 'active' : ''}`}
            onClick={() => setActiveTab('attending')}
          >
            📅 Asistiendo ({profile.totalEventsAttending})
          </button>
        </div>

        {/* CONTENIDO DEL TAB */}
        <div className="tab-content">
          {activeTab === 'created' && canCreateEvents && (
            <div className="events-list">
              {profile.createdEvents?.length === 0 ? (
                <p className="no-events">No has creado eventos aún</p>
              ) : (
                profile.createdEvents?.map(event => (
                  <div key={event.id} className="profile-event-card">
                    <h4>{event.title}</h4>
                    <p>{event.description}</p>
                    <div className="event-meta-mini">
                      <span>📅 {event.event_date ? new Date(event.event_date).toLocaleDateString('es-CO') : 'N/A'}</span>
                      <span>👥 {event.attendee_count || 0} asistentes</span>
                      {event.average_rating && <span>⭐ {event.average_rating.toFixed(1)}</span>}
                    </div>
                    <div className="event-actions-mini">
                      <button type="button" className="btn-mini btn-view" onClick={() => openEventDetails(event.id)} disabled={eventLoading}>
                        Ver
                      </button>
                      <button type="button" className="btn-mini btn-edit-event" onClick={() => handleEditEvent(event.id)}>
                        Editar
                      </button>
                      <button type="button" className="btn-mini btn-delete-event" onClick={() => handleDeleteEvent(event.id)}>
                        Eliminar
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}

          {activeTab === 'attending' && (
            <div className="events-list">
              {profile.attendingEvents?.length === 0 ? (
                <p className="no-events">No estás asistiendo a eventos aún</p>
              ) : (
                profile.attendingEvents?.map(event => (
                  <div key={event.id} className="profile-event-card">
                    <h4>{event.title}</h4>
                    <p>{event.description}</p>
                    <div className="event-meta-mini">
                      <span>📅 {event.event_date ? new Date(event.event_date).toLocaleDateString('es-CO') : 'N/A'}</span>
                      <span>📍 {event.location || 'N/A'}</span>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>

      {selectedEvent && (
        <EventModal
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
          categoryColors={categoryColors}
          currentUser={user}
          onRefresh={() => {
            fetchProfile()
            openEventDetails(selectedEvent.id)
          }}
        />
      )}
    </div>
  )
}
