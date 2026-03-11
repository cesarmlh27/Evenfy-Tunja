import './footer.css'

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-content">
          <div className="footer-section">
            <h3>Tunja Evenfy</h3>
            <p>Tu plataforma para descubrir los mejores eventos de la ciudad</p>
          </div>
          
          <div className="footer-section">
            <h4>Enlaces</h4>
            <ul>
              <li><a href="/">Inicio</a></li>
              <li><a href="/events">Eventos</a></li>
              <li><a href="#">Sobre nosotros</a></li>
              <li><a href="#">Contacto</a></li>
            </ul>
          </div>
          
          <div className="footer-section">
            <h4>Síguenos</h4>
            <div className="social-links">
              <a href="#" className="social-link">Twitter</a>
              <a href="#" className="social-link">Instagram</a>
              <a href="#" className="social-link">Facebook</a>
            </div>
          </div>
        </div>
        
        <div className="footer-bottom">
          <p>&copy; 2026 Tunja Evenfy. Todos los derechos reservados.</p>
          <p>Hecho con ❤️ para Tunja</p>
        </div>
      </div>
    </footer>
  )
}
