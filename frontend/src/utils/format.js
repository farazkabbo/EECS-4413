const money = new Intl.NumberFormat('en-CA', { style: 'currency', currency: 'CAD', maximumFractionDigits: 0 })
const number = new Intl.NumberFormat('en-CA')

export const formatPrice = (v) => (v == null ? '' : money.format(v))
export const formatKm = (v) => (v == null ? '' : `${number.format(v)} km`)
export const vehicleTitle = (v) => `${v.modelYear} ${v.brand} ${v.model}${v.trimName ? ' ' + v.trimName : ''}`
export const titleCase = (s) => (s ? s.charAt(0) + s.slice(1).toLowerCase().replaceAll('_', ' ') : '')
