import { Component } from 'react'

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null })
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
          minHeight: '60vh', padding: '2rem', textAlign: 'center',
          color: 'var(--text-primary)', background: 'var(--bg-primary)',
        }}>
          <h1 style={{ fontSize: '3rem', marginBottom: '1rem', color: 'var(--rojo-accent)' }}>Oops!</h1>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem', maxWidth: '500px' }}>
            Algo salió mal. Intenta recargar la página.
          </p>
          <div style={{ display: 'flex', gap: '1rem' }}>
            <button
              onClick={this.handleReset}
              style={{
                padding: '0.75rem 1.5rem', background: 'var(--verde-accent)', color: 'var(--bg-primary)',
                border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 700,
              }}
            >
              Reintentar
            </button>
            <button
              onClick={() => window.location.href = '/'}
              style={{
                padding: '0.75rem 1.5rem', background: 'var(--bg-tertiary)', color: 'var(--text-primary)',
                border: '1px solid var(--text-muted)', borderRadius: '8px', cursor: 'pointer', fontWeight: 700,
              }}
            >
              Ir al inicio
            </button>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
