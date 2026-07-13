import { Navigate } from 'react-router-dom'
import { useAuth } from '../hooks/AuthContext'

/**
 * Wraps routes that require authentication.
 * Redirects to /login if there's no valid token, once the initial
 * auth check (reading localStorage) has finished.
 */
export default function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth()

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-900">
        <p className="text-slate-400">Loading...</p>
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return children
}