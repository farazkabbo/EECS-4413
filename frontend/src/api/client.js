// Single place that talks to the Spring Boot API. 
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '' 
let csrfToken = '' 
export const setCsrfToken = (token) => { csrfToken = token ?? '' } 

export class ApiError extends Error { 
  constructor(status, body) { 
    super(body?.message || `Request failed (${status})`) 
    this.status = status 
    this.fieldErrors = body?.fieldErrors ?? {} 
  } 
} 

export async function api(path, { method = 'GET', body, params } = {}) { 
  const url = new URL(BASE_URL + path, window.location.origin) 
  if (params) { 
    Object.entries(params).forEach(([k, v]) => { 
      if (v !== undefined && v !== null && v !== '') url.searchParams.set(k, v) 
    }) 
  } 
  const csrf = document.cookie.split('; ').find((v) => v.startsWith('XSRF-TOKEN='))?.split('=').slice(1).join('=') 
  const headers = body ? { 'Content-Type': 'application/json' } : {} 
  if ((csrfToken || csrf) && method !== 'GET' && method !== 'HEAD') headers['X-XSRF-TOKEN'] = csrfToken || decodeURIComponent(csrf) 
  const res = await fetch(url, { 
    method, 
    credentials: 'include', 
    headers, 
    body: body ? JSON.stringify(body) : undefined, 
  }) 
  const text = await res.text() 
  const data = text ? JSON.parse(text) : null 
  if (!res.ok) throw new ApiError(res.status, data) 
  return data 
} 
