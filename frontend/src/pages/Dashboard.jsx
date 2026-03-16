import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import EventCard from '../components/EventCard'
import { eventService } from '../services/api'
import './Dashboard.css'

export default function Dashboard() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchEvents()
  }, [])

  const fetchEvents = async () => {
    try {
      const response = await eventService.getAll()
      setEvents(response.data || [])
    } catch {
      setEvents([])
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const handleProfileClick = () => {
    navigate('/profile')
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>🎪 Tunja Evenfy</h1>
        <div className="user-section">
          <span>Bienvenido, {user?.fullName || 'Usuario'}</span>
          <button className="btn-profile" onClick={handleProfileClick}>
            👤 Perfil
          </button>
          <button className="btn-logout" onClick={handleLogout}>
            🚪 Salir
          </button>
        </div>
      </div>

      <div className="events-container">
        <h2>Eventos Disponibles</h2>
        
        {loading ? (
          <p>Cargando eventos...</p>
        ) : events.length === 0 ? (
          <p>No hay eventos disponibles</p>
        ) : (
          <div className="events-grid">
            {events.map((event) => (
              <EventCard 
                key={event.id} 
                event={event}
                currentUser={user}
                onRefresh={fetchEvents}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
