import { api } from './client.js'

// Cart (M7) - UC-12, UC-13
export const getCart = () => api('/api/cart')
export const addToCart = (vehicleId, optionIds = [], quantity = 1) =>
  api('/api/cart/items', { method: 'POST', body: { vehicleId, optionIds, quantity } })
export const updateCartItem = (itemId, quantity) =>
  api(`/api/cart/items/${itemId}`, { method: 'PATCH', body: { quantity } })
export const removeCartItem = (itemId) => api(`/api/cart/items/${itemId}`, { method: 'DELETE' })

// Checkout (M8) - UC-14
export const startCheckout = () => api('/api/checkout/start', { method: 'POST' })
export const confirmShipping = (checkoutId, address) =>
  api(`/api/checkout/${checkoutId}/shipping`, { method: 'PUT', body: address })
export const placeOrder = (checkoutId, card) =>
  api(`/api/checkout/${checkoutId}/place-order`, { method: 'POST', body: { card } })
