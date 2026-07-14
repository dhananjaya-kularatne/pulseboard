const API_BASE_URL = 'http://localhost:8080/api'

async function handleResponse(response) {
  const body = await response.json()

  if (!response.ok) {
    throw new Error(body.message || 'Something went wrong')
  }

  return body.data
}

function authHeaders(token) {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  }
}

export async function getMonitors(token) {
  const response = await fetch(`${API_BASE_URL}/monitors`, {
    headers: authHeaders(token),
  })
  return handleResponse(response)
}

export async function createMonitor(token, { name, url, method, expectedStatusCode }) {
  const response = await fetch(`${API_BASE_URL}/monitors`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({ name, url, method, expectedStatusCode }),
  })
  return handleResponse(response)
}

export async function deleteMonitor(token, id) {
  const response = await fetch(`${API_BASE_URL}/monitors/${id}`, {
    method: 'DELETE',
    headers: authHeaders(token),
  })
  return handleResponse(response)
}