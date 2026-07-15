const API_BASE_URL = 'http://localhost:8080/api'

/**
 * Fetches uptime and response-time analytics for a single monitor.
 * hours controls the time window (default 24h, matching the backend's default).
 */
export async function getMonitorAnalytics(token, monitorId, hours = 24) {
  const response = await fetch(
    `${API_BASE_URL}/monitors/${monitorId}/analytics?hours=${hours}`,
    {
      headers: { Authorization: `Bearer ${token}` },
    }
  )

  const body = await response.json()

  if (!response.ok) {
    throw new Error(body.message || 'Failed to load analytics')
  }

  return body.data
}