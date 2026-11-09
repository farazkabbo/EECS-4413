import { useSearchParams } from 'react-router-dom'
import { listVehicles } from '../api/vehicles.js'
import { useApi } from '../hooks/useApi.js'
import VehicleCard from '../components/vehicles/VehicleCard.jsx'
import Spinner from '../components/ui/Spinner.jsx'
import ErrorMessage from '../components/ui/ErrorMessage.jsx'

// UC-04 List + UC-05 Sort/Filter. Owner going forward: Tamjeed.
const SHAPES = ['SEDAN', 'SUV', 'TRUCK', 'HATCHBACK', 'COUPE']
const SORTS = { '': 'Featured', price_asc: 'Price: low to high', price_desc: 'Price: high to low',
  mileage_asc: 'Mileage: low to high', mileage_desc: 'Mileage: high to low', year_desc: 'Newest' }

export default function CatalogPage() {
  const [params, setParams] = useSearchParams()
  const filters = Object.fromEntries(params)
  const { data: vehicles, loading, error } = useApi(() => listVehicles(filters), [params.toString()])

  const set = (key, value) => {
    const next = new URLSearchParams(params)
    value ? next.set(key, value) : next.delete(key)
    setParams(next)
  }
  const select = 'rounded-md border border-slate-300 bg-white px-3 py-2 text-sm'

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Vehicles</h1>
      <div className="flex flex-wrap gap-3">
        <input className={select} placeholder="Brand" defaultValue={filters.brand ?? ''}
               onKeyDown={(e) => e.key === 'Enter' && set('brand', e.target.value.trim())} />
        <select className={select} value={filters.shape ?? ''} onChange={(e) => set('shape', e.target.value)}>
          <option value="">Any shape</option>
          {SHAPES.map((s) => <option key={s} value={s}>{s.toLowerCase()}</option>)}
        </select>
        <select className={select} value={filters.condition ?? ''} onChange={(e) => set('condition', e.target.value)}>
          <option value="">New and used</option>
          <option value="NEW">New</option>
          <option value="USED">Used</option>
        </select>
        <label className="flex items-center gap-2 text-sm">
          <input type="checkbox" checked={filters.accidentFree === 'true'}
                 onChange={(e) => set('accidentFree', e.target.checked ? 'true' : '')} />
          No accidents or damage
        </label>
        <select className={`${select} ml-auto`} value={filters.sort ?? ''} onChange={(e) => set('sort', e.target.value)}>
          {Object.entries(SORTS).map(([k, label]) => <option key={k} value={k}>{label}</option>)}
        </select>
      </div>
      {loading && <Spinner />}
      <ErrorMessage error={error} />
      {vehicles?.length === 0 && <p className="text-slate-500">No vehicles match these filters.</p>}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {vehicles?.map((v) => <VehicleCard key={v.id} vehicle={v} />)}
      </div>
    </div>
  )
}
