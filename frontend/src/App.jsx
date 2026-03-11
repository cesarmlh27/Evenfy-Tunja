import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'
import './App.css'

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="*" element={
          <div className="not-found">
            <h1>404</h1>
            <p>Oops, no encontramos esa página</p>
            <a href="/">← Volver al inicio</a>
          </div>
        }/>
      </Routes>
      <Footer />
    </>
  )
}
