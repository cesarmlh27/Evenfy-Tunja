import { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'
import Login from './components/Login'
import Register from './components/Register'
import ProtectedRoute from './components/ProtectedRoute'
import { Verify2FA } from './pages/Verify2FA'
import './App.css'

const Dashboard = lazy(() => import('./pages/Dashboard'))
const UserProfile = lazy(() => import('./components/UserProfile'))
const CreateEvent = lazy(() => import('./pages/CreateEvent'))
const SearchEvents = lazy(() => import('./pages/SearchEvents'))
const Favorites = lazy(() => import('./pages/Favorites'))
const AdminPanel = lazy(() => import('./pages/AdminPanel'))

function LazyFallback() {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
      <div className="spinner" />
    </div>
  )
}

export default function App() {
  return (
    <>
      <Navbar />
      <Suspense fallback={<LazyFallback />}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/verify-2fa" element={<Verify2FA />} />
          <Route path="/search" element={<SearchEvents />} />
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          <Route path="/profile" element={
            <ProtectedRoute>
              <UserProfile />
            </ProtectedRoute>
          } />
          <Route path="/create-event" element={
            <ProtectedRoute>
              <CreateEvent />
            </ProtectedRoute>
          } />
          <Route path="/events/:id/edit" element={
            <ProtectedRoute>
              <CreateEvent />
            </ProtectedRoute>
          } />
          <Route path="/favorites" element={
            <ProtectedRoute>
              <Favorites />
            </ProtectedRoute>
          } />
          <Route path="/admin" element={
            <ProtectedRoute>
              <AdminPanel />
            </ProtectedRoute>
          } />
          <Route path="*" element={
            <div className="not-found">
              <h1>404</h1>
              <p>Oops, no encontramos esa página</p>
              <a href="/">← Volver al inicio</a>
            </div>
          }/>
        </Routes>
      </Suspense>
      <Footer />
    </>
  )
}
