import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { eventService, categoryService, uploadService } from '../services/api'
import { useToast } from '../components/Toast'
import './CreateEvent.css'

export default function CreateEvent() {
  const { id } = useParams()
  const isEditMode = Boolean(id)
  const { user, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const toast = useToast()

  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(false)
  const [imageFile, setImageFile] = useState(null)
  const [imagePreview, setImagePreview] = useState(null)

  const [form, setForm] = useState({
    title: '',
    description: '',
    eventDate: '',
    categoryId: '',
    location: '',
    isFree: 'true',
    ticketPurchaseUrl: '',
    infoUrl: '',
    imageUrl: '',
    maxCapacity: '',
  })

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    if (!['ADMIN', 'ORGANIZER'].includes(user?.role)) {
      toast.error('Solo organizadores y administradores pueden crear eventos')
      navigate('/')
      return
    }
    categoryService.getAll()
      .then((catRes) => {
        setCategories(catRes.data || [])
      })
      .catch(() => toast.error('Error cargando categorías'))
  }, [isAuthenticated, user])

  useEffect(() => {
    if (!isEditMode || !id || !isAuthenticated) return

    eventService.getById(id)
      .then((res) => {
        const ev = res.data || {}
        const ownerId = ev.created_by || ev.createdBy
        const role = user?.role
        const canEdit = role === 'ADMIN' || (ownerId && ownerId === user?.id)

        if (!canEdit) {
          toast.error('Solo el creador del evento puede editarlo')
          navigate('/')
          return
        }

        const eventDateRaw = ev.event_date || ev.eventDate
        const eventDate = eventDateRaw ? String(eventDateRaw).slice(0, 16) : ''

        setForm((prev) => ({
          ...prev,
          title: ev.title || '',
          description: ev.description || '',
          eventDate,
          categoryId: ev.category_id || ev.categoryId || '',
          location: ev.location || '',
          isFree: (ev.is_free === false || ev.isFree === false) ? 'false' : 'true',
          ticketPurchaseUrl: ev.ticket_purchase_url || ev.ticketPurchaseUrl || '',
          infoUrl: ev.info_url || ev.infoUrl || '',
          imageUrl: ev.image_url || ev.imageUrl || '',
          maxCapacity: ev.max_capacity || ev.maxCapacity || '',
        }))

        const img = ev.image_url || ev.imageUrl
        if (img) {
          setImagePreview(img.startsWith('/uploads/') ? `http://localhost:8080${img}` : img)
        }
      })
      .catch(() => {
        toast.error('No se pudo cargar el evento para editar')
        navigate('/')
      })
  }, [id, isEditMode, isAuthenticated, user, navigate])

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleImageChange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    const allowed = ['image/jpeg', 'image/png', 'image/webp', 'image/gif']
    if (!allowed.includes(file.type)) {
      toast.warning('Solo se permiten imágenes JPEG, PNG, WebP o GIF')
      return
    }
    if (file.size > 5 * 1024 * 1024) {
      toast.warning('La imagen no puede superar 5MB')
      return
    }
    setImageFile(file)
    setImagePreview(URL.createObjectURL(file))
  }

  const removeImage = () => {
    setImageFile(null)
    setImagePreview(null)
    setForm(prev => ({ ...prev, imageUrl: '' }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.title.trim() || !form.eventDate) {
      toast.warning('Título y fecha son obligatorios')
      return
    }

    setLoading(true)
    try {
      let imageUrl = form.imageUrl.trim() || null

      // Si hay archivo de imagen, subir primero
      if (imageFile) {
        const uploadRes = await uploadService.uploadImage(imageFile)
        imageUrl = uploadRes.data.url
      }

      const payload = {
        title: form.title.trim(),
        description: form.description.trim() || null,
        event_date: form.eventDate,
        category_id: form.categoryId || null,
        location: form.location.trim() || null,
        is_free: form.isFree === 'true',
        ticket_purchase_url: form.ticketPurchaseUrl.trim() || null,
        info_url: form.infoUrl.trim() || null,
        image_url: imageUrl,
        max_capacity: form.maxCapacity ? parseInt(form.maxCapacity) : null,
        created_by: user.id,
      }
      if (isEditMode) {
        await eventService.update(id, payload)
        toast.success('¡Evento actualizado exitosamente!')
      } else {
        await eventService.create(payload)
        toast.success('¡Evento creado exitosamente!')
      }
      navigate('/')
    } catch (err) {
      toast.error(err.response?.data?.message || (isEditMode ? 'Error al actualizar el evento' : 'Error al crear el evento'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="create-event-page">
      <div className="create-event-container">
        <h1>{isEditMode ? 'Editar Evento' : 'Crear Nuevo Evento'}</h1>
        <p className="subtitle">{isEditMode ? 'Actualiza la información de tu evento' : 'Comparte un evento con la comunidad de Tunja'}</p>

        <form onSubmit={handleSubmit} className="create-event-form">
          <div className="form-group">
            <label htmlFor="title">Título del evento *</label>
            <input
              id="title"
              name="title"
              type="text"
              value={form.title}
              onChange={handleChange}
              placeholder="Ej: Festival de Luces de Tunja"
              maxLength={150}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Descripción</label>
            <textarea
              id="description"
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Describe tu evento..."
              rows={4}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="eventDate">Fecha y hora *</label>
              <input
                id="eventDate"
                name="eventDate"
                type="datetime-local"
                value={form.eventDate}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="maxCapacity">Capacidad máxima</label>
              <input
                id="maxCapacity"
                name="maxCapacity"
                type="number"
                min="1"
                value={form.maxCapacity}
                onChange={handleChange}
                placeholder="Sin límite"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="categoryId">Categoría</label>
              <select id="categoryId" name="categoryId" value={form.categoryId} onChange={handleChange}>
                <option value="">Seleccionar categoría</option>
                {categories.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="location">Ubicación</label>
              <input
                id="location"
                name="location"
                type="text"
                value={form.location}
                onChange={handleChange}
                placeholder="Ej: Estadio La Independencia, Tunja"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="isFree">Tipo de evento</label>
              <select id="isFree" name="isFree" value={form.isFree} onChange={handleChange}>
                <option value="true">Gratis</option>
                <option value="false">Pago</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="ticketPurchaseUrl">Link de compra de boletas</label>
              <input
                id="ticketPurchaseUrl"
                name="ticketPurchaseUrl"
                type="url"
                value={form.ticketPurchaseUrl}
                onChange={handleChange}
                placeholder="https://..."
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="infoUrl">Link de información del evento</label>
            <input
              id="infoUrl"
              name="infoUrl"
              type="url"
              value={form.infoUrl}
              onChange={handleChange}
              placeholder="https://..."
            />
          </div>

          <div className="form-group">
            <label>Imagen del evento</label>
            <div className="image-upload-area">
              {imagePreview ? (
                <div className="image-preview">
                  <img src={imagePreview} alt="Preview" />
                  <button type="button" className="btn-remove-image" onClick={removeImage}>✕</button>
                </div>
              ) : (
                <label className="upload-dropzone" htmlFor="imageFile">
                  <span className="upload-icon">📷</span>
                  <span>Haz clic para subir una imagen</span>
                  <span className="upload-hint">JPEG, PNG, WebP o GIF — Máx. 5MB</span>
                </label>
              )}
              <input
                id="imageFile"
                type="file"
                accept="image/jpeg,image/png,image/webp,image/gif"
                onChange={handleImageChange}
                style={{ display: 'none' }}
              />
            </div>
            {!imageFile && (
              <input
                name="imageUrl"
                type="url"
                value={form.imageUrl}
                onChange={handleChange}
                placeholder="O pega la URL de una imagen..."
                className="url-fallback"
              />
            )}
          </div>

          <div className="form-actions">
            <button type="submit" className="btn-create" disabled={loading}>
              {loading ? (isEditMode ? 'Guardando...' : 'Creando...') : (isEditMode ? 'Guardar cambios' : '🎪 Crear Evento')}
            </button>
            <button type="button" className="btn-cancel" onClick={() => navigate('/')}>
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </main>
  )
}
