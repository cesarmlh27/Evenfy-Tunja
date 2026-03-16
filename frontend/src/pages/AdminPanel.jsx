import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { userService } from '../services/api'
import { useToast } from '../components/Toast'
import './AdminPanel.css'

export default function AdminPanel() {
  const { user, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const toast = useToast()

  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('')

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'ADMIN') {
      toast.error('Acceso restringido a administradores')
      navigate('/')
      return
    }
    fetchUsers()
  }, [isAuthenticated, user])

  const fetchUsers = async () => {
    try {
      const res = await userService.getAll()
      setUsers(res.data || [])
    } catch {
      toast.error('Error cargando usuarios')
    } finally {
      setLoading(false)
    }
  }

  const handleRoleChange = async (userId, newRole) => {
    if (userId === user.id) {
      toast.warning('No puedes cambiar tu propio rol')
      return
    }
    try {
      await userService.changeRole(userId, newRole)
      toast.success(`Rol actualizado a ${newRole}`)
      setUsers(prev => prev.map(u =>
        u.id === userId ? { ...u, role: newRole } : u
      ))
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al cambiar rol')
    }
  }

  const filteredUsers = users.filter(u =>
    u.fullName?.toLowerCase().includes(filter.toLowerCase()) ||
    u.email?.toLowerCase().includes(filter.toLowerCase()) ||
    u.role?.toLowerCase().includes(filter.toLowerCase())
  )

  const roleBadge = (role) => {
    const colors = {
      ADMIN: 'badge-admin',
      ORGANIZER: 'badge-organizer',
      USER: 'badge-user',
    }
    return colors[role] || 'badge-user'
  }

  if (loading) {
    return (
      <main className="admin-page">
        <div className="admin-loading">Cargando usuarios...</div>
      </main>
    )
  }

  return (
    <main className="admin-page">
      <div className="admin-container">
        <div className="admin-header">
          <div>
            <h1>Panel de Administración</h1>
            <p className="admin-subtitle">Gestiona los usuarios y sus roles</p>
          </div>
          <div className="admin-stats">
            <div className="stat-item">
              <span className="stat-number">{users.length}</span>
              <span className="stat-label">Total</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">{users.filter(u => u.role === 'ORGANIZER').length}</span>
              <span className="stat-label">Organizadores</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">{users.filter(u => u.role === 'ADMIN').length}</span>
              <span className="stat-label">Admins</span>
            </div>
          </div>
        </div>

        <div className="admin-search">
          <input
            type="text"
            placeholder="Buscar por nombre, email o rol..."
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          />
        </div>

        <div className="users-table-wrapper">
          <table className="users-table">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Email</th>
                <th>Rol actual</th>
                <th>Cambiar rol</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map(u => (
                <tr key={u.id} className={u.id === user.id ? 'current-user' : ''}>
                  <td className="user-cell">
                    <div className="user-avatar">
                      {u.avatarUrl ? (
                        <img src={u.avatarUrl} alt={u.fullName} />
                      ) : (
                        <span>{u.fullName?.charAt(0)?.toUpperCase()}</span>
                      )}
                    </div>
                    <span>{u.fullName}</span>
                  </td>
                  <td className="email-cell">{u.email}</td>
                  <td>
                    <span className={`role-badge ${roleBadge(u.role)}`}>
                      {u.role}
                    </span>
                  </td>
                  <td>
                    {u.id === user.id ? (
                      <span className="text-muted">— Tú —</span>
                    ) : (
                      <select
                        value={u.role}
                        onChange={(e) => handleRoleChange(u.id, e.target.value)}
                        className="role-select"
                      >
                        <option value="USER">USER</option>
                        <option value="ORGANIZER">ORGANIZER</option>
                        <option value="ADMIN">ADMIN</option>
                      </select>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {filteredUsers.length === 0 && (
            <div className="no-results">No se encontraron usuarios</div>
          )}
        </div>
      </div>
    </main>
  )
}
