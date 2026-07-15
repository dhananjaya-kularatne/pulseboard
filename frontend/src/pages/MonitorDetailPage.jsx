import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'
import { useAuth } from '../hooks/AuthContext'
import { getMonitorAnalytics } from '../api/analyticsApi'

const TIME_WINDOWS = [
  { label: '24h', hours: 24 },
  { label: '7d', hours: 168 },
  { label: '30d', hours: 720 },
]

/**
 * Detail view for a single monitor. Shows uptime %, average/P95 response time, and a chart of response time over the selected time window.
 * Data comes from the backend's aggregation-pipeline-powered analytics endpoint, recomputed on demand whenever the window changes.
 */
export default function MonitorDetailPage() {
  const { id } = useParams()
  const { token } = useAuth()

  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedHours, setSelectedHours] = useState(24)

  useEffect(() => {
    setLoading(true)
    getMonitorAnalytics(token, id, selectedHours)
      .then(setAnalytics)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, selectedHours])

  // Recharts expects a simple label field for the X axis; trim the
  // ISO timestamp down to just hours:minutes for readability
  const chartData =
    analytics?.responseTimeSeries.map((point) => ({
      time: point.timestamp.slice(11, 16),
      responseTimeMs: point.responseTimeMs,
    })) ?? []

  return (
    <div className="min-h-screen bg-slate-900 text-white p-8">
      <Link to="/dashboard" className="text-slate-400 hover:text-white text-sm">
        ← Back to dashboard
      </Link>

      <h1 className="text-2xl font-bold mt-4 mb-6">Monitor details</h1>

      <div className="flex gap-2 mb-6">
        {TIME_WINDOWS.map((window) => (
          <button
            key={window.hours}
            onClick={() => setSelectedHours(window.hours)}
            className={`text-sm px-3 py-1.5 rounded-md transition ${
              selectedHours === window.hours
                ? 'bg-blue-600 text-white'
                : 'bg-slate-800 text-slate-400 hover:text-white'
            }`}
          >
            {window.label}
          </button>
        ))}
      </div>

      {error && <p className="text-red-400 text-sm mb-4">{error}</p>}

      {loading ? (
        <p className="text-slate-400">Loading analytics...</p>
      ) : analytics ? (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <StatCard label="Uptime" value={`${analytics.uptimePercentage}%`} />
            <StatCard label="Avg response" value={`${analytics.avgResponseTimeMs}ms`} />
            <StatCard label="P95 response" value={`${analytics.p95ResponseTimeMs}ms`} />
            <StatCard label="Total pings" value={analytics.totalPings} />
          </div>

          <div className="bg-slate-800 rounded-lg p-6">
            <h2 className="text-sm font-medium text-slate-300 mb-4">
              Response time over selected window
            </h2>
            {chartData.length === 0 ? (
              <p className="text-slate-500 text-sm">No ping data yet.</p>
            ) : (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                  <XAxis dataKey="time" stroke="#94a3b8" fontSize={12} />
                  <YAxis stroke="#94a3b8" fontSize={12} />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#1e293b', border: 'none', borderRadius: 8 }}
                    labelStyle={{ color: '#e2e8f0' }}
                  />
                  <Line
                    type="monotone"
                    dataKey="responseTimeMs"
                    stroke="#3b82f6"
                    strokeWidth={2}
                    dot={false}
                  />
                </LineChart>
              </ResponsiveContainer>
            )}
          </div>
        </>
      ) : null}
    </div>
  )
}

function StatCard({ label, value }) {
  return (
    <div className="bg-slate-800 rounded-lg p-4">
      <p className="text-slate-400 text-xs mb-1">{label}</p>
      <p className="text-xl font-bold">{value}</p>
    </div>
  )
}