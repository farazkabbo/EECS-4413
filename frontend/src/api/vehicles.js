import { api } from './client.js' 

// Catalogue (M5) - UC-04..UC-07, hot deals 
export const listVehicles = (filters) => api('/api/vehicles', { params: filters }) 
export const getVehicle = (id) => api(`/api/vehicles/${id}`) 
export const getVehicleBrands = () => api('/api/vehicles/brands') 
export const getVehicleShapes = () => api('/api/vehicles/shapes') 
export const getHotDeals = (limit = 6) => api('/api/vehicles/hot-deals', { params: { limit } }) 
export const compareVehicles = (ids) => api('/api/vehicles/compare', { params: { ids: ids.join(',') } }) 

// Configurator (M6) - UC-08 
export const getOptions = (id) => api(`/api/vehicles/${id}/options`) 
export const priceConfiguration = (vehicleId, optionIds) => 
  api(`/api/vehicles/${vehicleId}/configuration`, { method: 'POST', body: { optionIds } }) 

// Reviews (M12) - UC-15 
export const getReviews = (id) => api(`/api/vehicles/${id}/reviews`) 
export const submitReview = (id, review) => api(`/api/vehicles/${id}/reviews`, { method: 'POST', body: review }) 
