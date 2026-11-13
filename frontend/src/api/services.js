import { api } from './client.js' 

// Calculators (M9) - UC-09, UC-20 
export const loanQuote = (data) => api('/api/calculators/loan', { method: 'POST', body: data }) 
export const savingsEstimate = (vehicleId, data) => 
  api(`/api/vehicles/${vehicleId}/savings`, { method: 'POST', body: data }) 

// Recommendations (M10) - UC-10 
export const recommend = (profile, max = 5) => api('/api/recommendations', { method: 'POST', body: profile, params: { max } }) 

// Test drives (M11) - UC-11 
export const getSlots = (vehicleId, type, date) => api(`/api/vehicles/${vehicleId}/test-drive-slots`, { params: { type, date } }) 
export const bookTestDrive = (slotId, address) => api('/api/test-drives', { method: 'POST', body: { slotId, address } }) 
export const myTestDrives = () => api('/api/test-drives/mine') 
export const cancelTestDrive = (id) => api(`/api/test-drives/${id}`, { method: 'DELETE' }) 

// Chatbot (M13) - UC-16 
export const sendChat = (message) => api('/api/chat', { method: 'POST', body: { message } }) 

// Analytics (M14) - UC-17..UC-19 (admin) + event tracking 
export const logEvent = (eventType, vehicleId) => api('/api/events', { method: 'POST', body: { eventType, vehicleId } }) 
export const salesReport = (year, month) => api('/api/admin/reports/sales', { params: { year, month } }) 
export const usageReport = (from, to) => api('/api/admin/reports/usage', { params: { from, to } }) 
export const dashboard = () => api('/api/admin/dashboard') 
