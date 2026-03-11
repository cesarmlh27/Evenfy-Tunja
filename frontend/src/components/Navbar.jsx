import './navbar.css'

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <span className="brand-emoji"></span>
          <h2>Tunja Evenfy</h2>
        </div>
        <div className="navbar-links">
          <a href="/" className="nav-link">Inicio</a>
          <a href="/events" className="nav-link">Eventos</a>
          <a href="/login" className="nav-link nav-link-cta">Ingresar</a>
        </div>
      </div>
    </nav>
  )
}
