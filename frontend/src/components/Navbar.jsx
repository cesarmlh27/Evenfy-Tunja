import { useState, useEffect, useRef } from 'react'
import { useAuth } from '../context/AuthContext'
import { Link, useLocation } from 'react-router-dom'
import { categoryService } from '../services/api'
import './navbar.css'

const NAV_CATEGORY_COLORS = {
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
  if (NAV_CATEGORY_COLORS[normalized]) return NAV_CATEGORY_COLORS[normalized]

  // Deterministic fallback color for unknown categories.
  const hash = [...normalized].reduce((acc, ch) => acc + ch.charCodeAt(0), 0)
  return FALLBACK_COLORS[hash % FALLBACK_COLORS.length]
}

export default function Navbar() {
  const { isAuthenticated, user } = useAuth()
  const location = useLocation()
  const [menuOpen, setMenuOpen] = useState(false)
  const [categories, setCategories] = useState([])
  const [eventsOpen, setEventsOpen] = useState(false)
  const dropdownRef = useRef(null)
  const activeCategoryId = new URLSearchParams(location.search).get('categoryId') || ''

  const canCreate = ['ADMIN', 'ORGANIZER'].includes(user?.role)
  const isAdmin = user?.role === 'ADMIN'

  useEffect(() => {
    categoryService.getAll()
      .then(res => setCategories(res.data || []))
      .catch(() => {})
  }, [])

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setEventsOpen(false)
      }
    }
    document.addEventListener('click', handleClickOutside)
    return () => document.removeEventListener('click', handleClickOutside)
  }, [])

  const handleDropdownSelect = (e) => {
    e?.stopPropagation()
    setEventsOpen(false)
    setMenuOpen(false)
  }

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand" onClick={() => setMenuOpen(false)}>
          <span className="brand-emoji"></span>
          <h2>Tunja Evenfy</h2>
        </Link>

        <button className="navbar-toggle" onClick={() => setMenuOpen(!menuOpen)} aria-label="Menú">
          <span className={`hamburger ${menuOpen ? 'open' : ''}`}></span>
        </button>

        <div className={`navbar-links ${menuOpen ? 'active' : ''}`}>
          <Link to="/" className="nav-link" onClick={() => setMenuOpen(false)}>Inicio</Link>

          <div className="nav-dropdown-wrap" ref={dropdownRef}>
            <span
              className={`nav-link nav-events-trigger${eventsOpen ? ' hovered' : ''}`}
              onClick={(e) => {
                e.stopPropagation()
                setEventsOpen(o => !o)
              }}
            >
              Eventos <span className="nav-arrow">&#9660;</span>
            </span>
            {eventsOpen && (
              <div className="nav-dropdown" onClick={(e) => e.stopPropagation()}>
                <a
                  className={`nav-dropdown-item${!activeCategoryId ? ' active' : ''}`}
                  href="/search"
                  onClick={handleDropdownSelect}
                >
                  <span className="nav-dot" style={{ background: '#888' }}></span>
                  Todos los eventos
                </a>
                <div className="nav-dropdown-divider"></div>
                {categories.map(c => (
                  <a
                    key={c.id}
                    className={`nav-dropdown-item${activeCategoryId === c.id?.toString() ? ' active' : ''}`}
                    href={`/search?categoryId=${c.id}`}
                    onClick={handleDropdownSelect}
                  >
                    <span className="nav-dot" style={{ background: getCategoryColor(c.name) }}></span>
                    {c.name}
                  </a>
                ))}
              </div>
            )}
          </div>

          {isAuthenticated ? (
            <>
              {canCreate && (
                <Link to="/create-event" className="nav-link nav-link-cta" onClick={() => setMenuOpen(false)}>
                  + Crear Evento
                </Link>
              )}
              <Link to="/favorites" className="nav-link" onClick={() => setMenuOpen(false)}>Favoritos</Link>
              {isAdmin && (
                <Link to="/admin" className="nav-link nav-link-admin" onClick={() => setMenuOpen(false)}>
                  ⚙️ Admin
                </Link>
              )}
              <Link to="/profile" className="nav-link nav-user" onClick={() => setMenuOpen(false)}>
                👤 {user?.fullName?.split(' ')[0] || 'Mi Perfil'}
              </Link>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link" onClick={() => setMenuOpen(false)}>Ingresar</Link>
              <Link to="/register" className="nav-link nav-link-cta" onClick={() => setMenuOpen(false)}>Registrarse</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
