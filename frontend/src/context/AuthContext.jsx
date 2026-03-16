import React, { createContext, useState, useEffect } from 'react'
import { authService } from '../services/api'

export const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)
  const [loading, setLoading] = useState(true)
  const [twoFactorRequired, setTwoFactorRequired] = useState(false)
  const [tempEmail, setTempEmail] = useState(null)

  useEffect(() => {
    const storedToken = localStorage.getItem('authToken')
    const storedUser = localStorage.getItem('user')
    
    if (storedToken && storedUser) {
      try {
        setToken(storedToken)
        setUser(JSON.parse(storedUser))
      } catch (e) {
        console.error('Error loading stored user:', e)
        localStorage.removeItem('authToken')
        localStorage.removeItem('user')
      }
    }
    setLoading(false)
  }, [])

  const login = async (email, password) => {
    try {
      const response = await authService.login({ email, password })
      const data = response.data

      if (data.twoFactorRequired) {
        setTwoFactorRequired(true)
        setTempEmail(email)
        return { success: false, twoFactorRequired: true }
      }

      // Construir objeto usuario de la respuesta
      const user = {
        id: data.userId,
        email: data.email,
        fullName: data.fullName,
        role: data.role,
      }

      localStorage.setItem('authToken', data.token)
      localStorage.setItem('user', JSON.stringify(user))
      setToken(data.token)
      setUser(user)
      return { success: true }
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Login failed' }
    }
  }

  const verify2FA = async (code) => {
    try {
      const response = await authService.verify2FA({ email: tempEmail, code })
      const data = response.data

      // Construir objeto usuario de la respuesta
      const user = {
        id: data.userId,
        email: data.email,
        fullName: data.fullName,
        role: data.role,
      }

      localStorage.setItem('authToken', data.token)
      localStorage.setItem('user', JSON.stringify(user))
      setToken(data.token)
      setUser(user)
      setTwoFactorRequired(false)
      setTempEmail(null)
      return { success: true }
    } catch (error) {
      return { success: false, error: error.response?.data?.message || '2FA verification failed' }
    }
  }

  const register = async (fullName, email, password, passwordConfirm) => {
    try {
      const response = await authService.register({ fullName, email, password, passwordConfirm })
      return { success: true, message: 'Registration successful. Please login.' }
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Registration failed' }
    }
  }

  const logout = () => {
    localStorage.removeItem('authToken')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
    setTwoFactorRequired(false)
    setTempEmail(null)
  }

  const isAuthenticated = !!token && !!user

  return (
    <AuthContext.Provider value={{
      user,
      token,
      loading,
      isAuthenticated,
      twoFactorRequired,
      login,
      register,
      logout,
      verify2FA
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = React.useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
