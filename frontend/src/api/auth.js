import { api, setCsrfToken } from './client.js'

// Account (M4) - UC-01..UC-03
export const register = (data) => api('/api/auth/register', { method: 'POST', body: data })
export const login = (username, password) => api('/api/auth/login', { method: 'POST', body: { username, password } })
export const logout = () => api('/api/auth/logout', { method: 'POST' })
export const me = () => api('/api/auth/me')
export const csrf = async () => { const result = await api('/api/auth/csrf'); setCsrfToken(result.token); return result }
