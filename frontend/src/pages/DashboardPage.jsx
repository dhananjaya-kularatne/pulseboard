import { useState, useEffect } from 'react'
import { useAuth } from '../hooks/AuthContext'
import { getMonitors, createMonitor, deleteMonitor } from '../api/monitorApi'

// Maps backend MonitorStatus enum values to Tailwind color classes
const statusColors = {
  UP: 'bg-green-500',
  DOWN: 'bg-red-500',
}

/**
 * Main dashboard, shown after login. Fetches the logged-in user's monitors on mount, and provides a form to create new ones plus
 * a delete action per monitor. Status dot color reflects currentStatus as last updated by the backend's scheduled PingService. A legend clarifies what each 
 * color means; UNKNOWN monitors show a "checking" indicator instead of an ambiguous dot color.
 */
export default function DashboardPage() {
  const { user, token, logout } = useAuth()
  const [monitors, setMonitors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [name, setName] = useState('')
  const [url, setUrl] = useState('')
  const [method, setMethod] = useState('GET')
  const [expectedStatusCode, setExpectedStatusCode] = useState(200)
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function loadMonitors() {
    try {
      const data = await getMonitors(token)
      setMonitors(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMonitors()
  }, [])

  async function handleCreate(e) {
    e.preventDefault()
    setIsSubmitting(true)
    setError('')

    try {
      await createMonitor(token, {
        name,
        url,
        method,
        expectedStatusCode: Number(expectedStatusCode), // input value is always a string; backend expects an int
      })
      setName('')
      setUrl('')
      setMethod('GET')
      setExpectedStatusCode(200)
      setShowForm(false)
      await loadMonitors()
    } catch (err) {
      setError(err.message)
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleDelete(id) {
    try {
      await deleteMonitor(token, id)
      await loadMonitors()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="min-h-screen bg-slate-900 text-white p-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold">PulseBoard</h1>
        <div className="flex items-center gap-4">
          <span className="text-slate-400 text-sm">{user?.name}</span>
          <button
            onClick={logout}
            className="text-sm text-slate-300 hover:text-white transition"
          >
            Log out
          </button>
        </div>
      </div>

      <div className="flex items-center justify-between mb-2">
        <h2 className="text-lg font-medium text-slate-200">Your monitors</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 hover:bg-blue-500 text-white text-sm rounded-md px-4 py-2 transition"
        >
          {showForm ? 'Cancel' : '+ Add monitor'}
        </button>
      </div>

      <div className="flex items-center gap-4 text-xs text-slate-500 mb-4">
        <span className="flex items-center gap-1.5">
          <span className="w-2.5 h-2.5 rounded-full bg-green-500" /> Up
        </span>
        <span className="flex items-center gap-1.5">
          <span className="w-2.5 h-2.5 rounded-full bg-red-500" /> Down
        </span>
        <span className="flex items-center gap-1.5">
          <span className="w-2.5 h-2.5 rounded-full bg-slate-500" /> Checking
        </span>
      </div>

      {error && <p className="text-red-400 text-sm mb-4">{error}</p>}

      {showForm && (
        <form
          onSubmit={handleCreate}
          className="bg-slate-800 rounded-lg p-6 mb-6 space-y-4"
        >
          <div>
            <label className="block text-sm text-slate-300 mb-1">Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-md bg-slate-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm text-slate-300 mb-1">URL</label>
            <input
              type="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              placeholder="https://example.com"
              className="w-full rounded-md bg-slate-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className="block text-sm text-slate-300 mb-1">Method</label>
              <select
                value={method}
                onChange={(e) => setMethod(e.target.value)}
                className="w-full rounded-md bg-slate-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="GET">GET</option>
                <option value="POST">POST</option>
              </select>
            </div>

            <div className="flex-1">
              <label className="block text-sm text-slate-300 mb-1">Expected status</label>
              <input
                type="number"
                value={expectedStatusCode}
                onChange={(e) => setExpectedStatusCode(e.target.value)}
                className="w-full rounded-md bg-slate-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white rounded-md px-4 py-2 text-sm font-medium transition"
          >
            {isSubmitting ? 'Creating...' : 'Create monitor'}
          </button>
        </form>
      )}

      {loading ? (
        <p className="text-slate-400">Loading monitors...</p>
      ) : monitors.length === 0 ? (
        <p className="text-slate-500 text-sm">
          No monitors yet. Add one to get started.
        </p>
      ) : (
        <div className="space-y-3">
          {monitors.map((monitor) => (
            <div
              key={monitor.id}
              className="bg-slate-800 rounded-lg p-4 flex items-center justify-between"
            >
              <div className="flex items-center gap-3">
                {monitor.currentStatus === 'UNKNOWN' ? (
                  <span className="text-slate-500 text-xs w-8">•••</span>
                ) : (
                  <span
                    className={`w-3 h-3 rounded-full ${statusColors[monitor.currentStatus]}`}
                    title={monitor.currentStatus}
                  />
                )}
                <div>
                  <p className="font-medium">{monitor.name}</p>
                  <p className="text-slate-400 text-sm">{monitor.url}</p>
                </div>
              </div>

              <div className="flex items-center gap-4">
                <span className="text-slate-500 text-xs">
                  {monitor.method} · expects {monitor.expectedStatusCode}
                </span>
                <button
                  onClick={() => handleDelete(monitor.id)}
                  className="text-red-400 hover:text-red-300 text-sm transition"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}