import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import '../styles/auth.css'

export function Verify2FA() {
  const [code, setCode] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { verify2FA } = useAuth()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (code.length !== 6) {
      setError('El código debe tener 6 dígitos')
      return
    }

    setLoading(true)

    const result = await verify2FA(code)

    if (result.success) {
      navigate('/dashboard')
    } else {
      setError(result.error)
    }

    setLoading(false)
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>🔐 Verificación 2FA</h1>
        <p className="subtitle">Ingresa el código enviado a tu correo</p>

        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label>Código de Verificación</label>
            <input
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value.replace(/[^0-9]/g, '').slice(0, 6))}
              placeholder="000000"
              maxLength="6"
              required
              style={{ textAlign: 'center', fontSize: '2rem', letterSpacing: '8px', fontWeight: 'bold' }}
            />
            <small>Verifica tu correo para el código de 6 dígitos</small>
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Verificando...' : 'Verificar'}
          </button>
        </form>
      </div>
    </div>
  )
}
