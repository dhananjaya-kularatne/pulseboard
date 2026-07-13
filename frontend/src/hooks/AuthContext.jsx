import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

/**
 * Provides authentication state (current user + token) to the entire app.
 * Persists the token in localStorage so the user stays logged in across page refreshes.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')

    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
    }

    setLoading(false)
  }, [])

  function login(authResponse) {
    const { token, name, email } = authResponse
    const userData = { name, email }

    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(userData))

    setToken(token)
    setUser(userData)
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
  }

  const value = {
    user,
    token,
    isAuthenticated: !!token,
    loading,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

/**
 * Hook to access auth state and actions from any component.
 * Usage: const { user, login, logout, isAuthenticated } = useAuth()
 */
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}