import { useState } from 'react' 
import { Link, useParams } from 'react-router-dom' 
import { addToCart } from '../api/cart.js' 
import { getOptions, getVehicle, priceConfiguration } from '../api/vehicles.js' 
import { useApi } from '../hooks/useApi.js' 
import ErrorMessage from '../components/ui/ErrorMessage.jsx' 
import Spinner from '../components/ui/Spinner.jsx' 
import { formatPrice, titleCase, vehicleTitle } from '../utils/format.js' 

export default function ConfiguratorPage() { 
  const { id } = useParams() 
  const { data, loading, error } = useApi(() => Promise.all([getVehicle(id), getOptions(id)]), [id]) 
  const [selectedIds, setSelectedIds] = useState([]) 
  const [configuration, setConfiguration] = useState(null) 
  const [message, setMessage] = useState('') 
  const [saving, setSaving] = useState(false) 

  if (loading) return <Spinner /> 
  if (error) return <ErrorMessage error={error} /> 

  const [vehicle, options] = data 
  const basePrice = vehicle.dealPrice ?? vehicle.price 

  function choose(category, optionId) { 
    const categoryIds = (options[category] ?? []).map((option) => option.id) 
    setSelectedIds((current) => current.includes(optionId) 
      ? current.filter((id) => id !== optionId) 
      : [...current.filter((id) => !categoryIds.includes(id)), optionId]) 
    setConfiguration(null) 
    setMessage('') 
  } 

  async function calculate(event) { 
    event.preventDefault() 
    setSaving(true) 
    setMessage('') 
    try { 
      const result = await priceConfiguration(vehicle.id, selectedIds) 
      setConfiguration(result) 
    } catch (requestError) { 
      setMessage(requestError.message) 
    } finally { 
      setSaving(false) 
    } 
  } 

  async function addConfiguredVehicle() { 
    setSaving(true) 
    setMessage('') 
    try { 
      await addToCart(vehicle.id, selectedIds, 1) 
      setMessage('Configured vehicle added to your cart.') 
    } catch (requestError) { 
      setMessage(requestError.message) 
    } finally { 
      setSaving(false) 
    } 
  } 

  return ( 
    <div className="mx-auto max-w-4xl space-y-6"> 
      <p><Link className="text-sm text-brand underline" to={`/vehicles/${vehicle.id}`}>Back to vehicle details</Link></p> 
      <div> 
        <h1 className="text-3xl font-bold">Configure your {vehicleTitle(vehicle)}</h1> 
        <p className="mt-1 text-slate-600">Choose at most one option in each category.</p> 
      </div> 
      <ErrorMessage error={error} /> 
      {message && <p role="status" className="rounded-lg bg-slate-100 p-3 text-sm">{message}</p>} 
      <form onSubmit={calculate} className="space-y-5"> 
        {Object.entries(options ?? {}).map(([category, choices]) => ( 
          <fieldset key={category} className="rounded-xl border border-slate-200 bg-white p-5"> 
            <legend className="px-2 text-lg font-semibold">{titleCase(category)}</legend> 
            <div className="grid gap-3 sm:grid-cols-2"> 
              {choices.map((option) => ( 
                <label key={option.id} className={`flex cursor-pointer items-center justify-between gap-3 rounded-lg border p-3 ${selectedIds.includes(option.id) ? 'border-brand bg-teal-50' : 'border-slate-200'}`}> 
                  <span className="flex items-center gap-3"> 
                    <input type="checkbox" checked={selectedIds.includes(option.id)} 
                           onChange={() => choose(category, option.id)} /> 
                    {option.hexCode && <span className="h-5 w-5 rounded-full border" style={{ backgroundColor: option.hexCode }} />} 
                    <span>{option.name}</span> 
                  </span> 
                  <span className="text-sm text-slate-600">{formatPrice(option.priceDelta)}</span> 
                </label> 
              ))} 
            </div> 
          </fieldset> 
        ))} 
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-xl bg-slate-900 p-5 text-white"> 
          <div> 
            <p className="text-sm text-slate-300">Starting price</p> 
            <p className="text-xl font-bold">{formatPrice(basePrice)}</p> 
            {configuration && <p className="mt-1 text-sm text-slate-300">Options: {formatPrice(configuration.optionsTotal)}</p>} 
          </div> 
          {configuration && <p className="text-xl font-bold">Total {formatPrice(configuration.total)}</p>} 
          <div className="flex gap-2"> 
            <button type="submit" disabled={saving} 
                    className="rounded-lg bg-white px-4 py-2 font-semibold text-slate-900 disabled:opacity-50"> 
              {saving ? 'Working…' : 'Calculate total'} 
            </button> 
            {configuration && ( 
              <button type="button" disabled={saving} onClick={addConfiguredVehicle} 
                      className="rounded-lg bg-brand px-4 py-2 font-semibold text-white disabled:opacity-50"> 
                Add to cart 
              </button> 
            )} 
          </div> 
        </div> 
      </form> 
    </div> 
  ) 
} 
