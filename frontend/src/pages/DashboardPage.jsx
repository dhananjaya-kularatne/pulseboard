import { useAuth } from '../hooks/AuthContext'

/**
 * Placeholder dashboard, shown after successful login/registration.
 * Will be replaced with the real monitor list in Phase 2.
 */
export default function DashboardPage() {
  const { user, logout } = useAuth()

  return (
    <div className="min-h-screen bg-slate-900 text-white p-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold">PulseBoard</h1>
        <button
          onClick={logout}
          className="text-sm text-slate-300 hover:text-white transition"
        >
          Log out
        </button>
      </div>

      <p className="text-slate-300">
        Welcome, <span className="text-white font-medium">{user?.name}</span>.
      </p>
      <p className="text-slate-500 text-sm mt-2">
        Your monitors will show up here once Phase 2 is built.
      </p>
    </div>
  )
}