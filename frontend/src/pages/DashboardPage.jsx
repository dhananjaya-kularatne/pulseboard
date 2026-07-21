import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../hooks/AuthContext'
import { useMonitorUpdates } from '../hooks/useMonitorUpdates'
import { getMonitors, createMonitor, deleteMonitor } from '../api/monitorApi'

// Maps backend MonitorStatus enum values to Tailwind color classes
const statusColors = {
  UP: 'bg-green-500',
  DOWN: 'bg-red-500',
}

/**
 * Main dashboard, shown after login. Shows a summary row (total monitors, how many are up/down), a form to create new monitors, and the list itself. 
 * Status updates arrive live via WebSocket — no polling or manual refresh needed to see a monitor's dot change color.
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

  const handleStatusUpdate = useCallback((update) => {
    setMonitors((prevMonitors) =>
      prevMonitors.map((monitor) =>
        monitor.id === update.monitorId
          ? { ...monitor, currentStatus: update.status }
          : monitor
      )
    )
  }, [])

  useMonitorUpdates(handleStatusUpdate)

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
        expectedStatusCode: Number(expectedStatusCode),
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

  const upCount = monitors.filter((m) => m.currentStatus === 'UP').length
  const downCount = monitors.filter((m) => m.currentStatus === 'DOWN').length
  const checkingCount = monitors.filter((m) => m.currentStatus === 'UNKNOWN').length

  return (
    <div className="min-h-screen bg-slate-900 text-white">
      <div className="border-b border-slate-800 px-8 py-5">
        <div className="flex items-center justify-between max-w-6xl mx-auto">
          <h1 className="text-xl font-bold">PulseBoard</h1>
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
      </div>

      <div className="max-w-6xl mx-auto px-8 py-8">
        {/* Summary stats row */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <SummaryCard label="Total monitors" value={monitors.length} />
          <SummaryCard label="Up" value={upCount} accent="text-green-400" />
          <SummaryCard label="Down" value={downCount} accent="text-red-400" />
          <SummaryCard label="Checking" value={checkingCount} accent="text-slate-400" />
        </div>

        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-medium text-slate-200">Your monitors</h2>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 hover:bg-blue-500 text-white text-sm rounded-md px-4 py-2 transition"
          >
            {showForm ? 'Cancel' : '+ Add monitor'}
          </button>
        </div>

        {error && <p className="text-red-400 text-sm mb-4">{error}</p>}

        {showForm && (
          <form
            onSubmit={handleCreate}
            className="bg-slate-800 rounded-xl p-6 mb-6 space-y-4 border border-slate-700"
          >
            <div>
              <label className="block text-sm text-slate-300 mb-1">Name</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full rounded-md bg-slate-900 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
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
                className="w-full rounded-md bg-slate-900 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-sm text-slate-300 mb-1">Method</label>
                <select
                  value={method}
                  onChange={(e) => setMethod(e.target.value)}
                  className="w-full rounded-md bg-slate-900 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
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
                  className="w-full rounded-md bg-slate-900 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
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
          <div className="bg-slate-800 border border-dashed border-slate-700 rounded-xl p-12 text-center">
            <p className="text-slate-300 font-medium mb-1">No monitors yet</p>
            <p className="text-slate-500 text-sm">
              Add a URL above and PulseBoard will start checking it automatically.
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {monitors.map((monitor) => (
              <Link
                key={monitor.id}
                to={`/monitors/${monitor.id}`}
                className="bg-slate-800 hover:bg-slate-700 border border-slate-800 hover:border-slate-600 rounded-xl p-4 flex items-center justify-between transition"
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
                    onClick={(e) => {
                      e.preventDefault()
                      e.stopPropagation()
                      handleDelete(monitor.id)
                    }}
                    className="text-red-400 hover:text-red-300 text-sm transition"
                  >
                    Delete
                  </button>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

function SummaryCard({ label, value, accent = 'text-white' }) {
  return (
    <div className="bg-slate-800 border border-slate-800 rounded-xl p-4">
      <p className="text-slate-400 text-xs mb-1">{label}</p>
      <p className={`text-2xl font-bold ${accent}`}>{value}</p>
    </div>
  )
}