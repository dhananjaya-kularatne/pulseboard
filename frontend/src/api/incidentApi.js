const API_BASE_URL = 'http://localhost:8080/api'

/**
 * Fetches incident history for a single monitor, most recent first
 * (as ordered by the backend).
 */
export async function getMonitorIncidents(token, monitorId) {
  const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}/incidents`, {
    headers: { Authorization: `Bearer ${token}` },
  })

  const body = await response.json()

  if (!response.ok) {
    throw new Error(body.message || 'Failed to load incidents')
  }

  return body.data
}