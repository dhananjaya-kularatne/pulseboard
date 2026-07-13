const API_BASE_URL = 'http://localhost:8080/api'

/**
 * Thin wrapper around the backend's /api/auth endpoints.
 * Throws an Error with the backend's message on failure, so callers
 * can catch it and display it directly.
 */
async function handleResponse(response) {
  const body = await response.json()

  if (!response.ok) {
    throw new Error(body.message || 'Something went wrong')
  }

  return body.data
}

export async function registerUser({ name, email, password }) {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password }),
  })

  return handleResponse(response)
}

export async function loginUser({ email, password }) {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  })

  return handleResponse(response)
}