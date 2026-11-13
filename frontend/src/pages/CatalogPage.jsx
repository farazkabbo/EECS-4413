import { useState } from 'react' 
import { useNavigate, useSearchParams } from 'react-router-dom' 
import { getVehicleBrands, getVehicleShapes, listVehicles } from '../api/vehicles.js' 
import { useApi } from '../hooks/useApi.js' 
import VehicleCard from '../components/vehicles/VehicleCard.jsx' 
import Spinner from '../components/ui/Spinner.jsx' 
import ErrorMessage from '../components/ui/ErrorMessage.jsx' 

const SORTS = { 
  '': 'Featured', 
  price_asc: 'Price: low to high', 
  price_desc: 'Price: high to low', 
  mileage_asc: 'Mileage: low to high', 
  mileage_desc: 'Mileage: high to low', 
  year_desc: 'Newest', 
} 

export default function CatalogPage() { 
  const [params, setParams] = useSearchParams() 
  const navigate = useNavigate() 
  const [selectedIds, setSelectedIds] = useState([]) 
  const filters = Object.fromEntries(params) 
  const { data: vehicles, loading, error } = useApi(() => listVehicles(filters), [params.toString()]) 
  const { data: brands } = useApi(getVehicleBrands, []) 
  const { data: shapes } = useApi(getVehicleShapes, []) 
  const inputClass = 'rounded-md border border-slate-300 bg-white px-3 py-2 text-sm' 

  function setFilter(key, value) { 
    const next = new URLSearchParams(params) 
    if (value) next.set(key, value) 
    else next.delete(key) 
    setParams(next) 
  } 

  function toggleVehicle(id) { 
    setSelectedIds((current) => current.includes(id) 
      ? current.filter((selectedId) => selectedId !== id) 
      : current.length < 4 ? [...current, id] : current) 
  } 

  function compareSelected() { 
    navigate(`/compare?ids=${selectedIds.join(',')}`) 
  } 

  return ( 
    <div className="space-y-6"> 
      <div> 
        <h1 className="text-3xl font-bold">Electric vehicles</h1> 
        <p className="mt-1 text-slate-600">Browse, filter, and compare available new and pre-owned EVs.</p> 
      </div> 

      <form className="grid gap-3 rounded-xl border border-slate-200 bg-white p-4 sm:grid-cols-2 lg:grid-cols-4" 
            onSubmit={(event) => event.preventDefault()}> 
        <label className="grid gap-1 text-sm font-medium"> 
          Brand 
          <select className={inputClass} value={filters.brand ?? ''} onChange={(event) => setFilter('brand', event.target.value)}> 
            <option value="">All brands</option> 
            {(brands ?? []).map((brand) => <option key={brand} value={brand}>{brand}</option>)} 
          </select> 
        </label> 
        <label className="grid gap-1 text-sm font-medium"> 
          Shape 
          <select className={inputClass} value={filters.shape ?? ''} onChange={(event) => setFilter('shape', event.target.value)}> 
            <option value="">All shapes</option> 
            {(shapes ?? []).map((shape) => <option key={shape} value={shape}>{shape.toLowerCase()}</option>)} 
          </select> 
        </label> 
        <label className="grid gap-1 text-sm font-medium"> 
          Condition 
          <select className={inputClass} value={filters.condition ?? ''} onChange={(event) => setFilter('condition', event.target.value)}> 
            <option value="">New and used</option> 
            <option value="NEW">New</option> 
            <option value="USED">Used</option> 
          </select> 
        </label> 
        <label className="grid gap-1 text-sm font-medium"> 
          Sort by 
          <select className={inputClass} value={filters.sort ?? ''} onChange={(event) => setFilter('sort', event.target.value)}> 
            {Object.entries(SORTS).map(([value, label]) => <option key={value} value={value}>{label}</option>)} 
          </select> 
        </label> 
        <label className="grid gap-1 text-sm font-medium"> 
          Year from 
          <input className={inputClass} type="number" min="2000" max="2100" value={filters.minYear ?? ''} 
                 onChange={(event) => setFilter('minYear', event.target.value)} /> 
        </label> 
        <label className="grid gap-1 text-sm font-medium"> 
          Year to 
          <input className={inputClass} type="number" min="2000" max="2100" value={filters.maxYear ?? ''} 
                 onChange={(event) => setFilter('maxYear', event.target.value)} /> 
        </label> 
        <label className="flex items-center gap-2 self-end pb-2 text-sm"> 
          <input type="checkbox" checked={filters.accidentFree === 'true'} 
                 onChange={(event) => setFilter('accidentFree', event.target.checked ? 'true' : '')} /> 
          No accidents or damage 
        </label> 
        <button type="button" className="self-end justify-self-start text-sm text-brand underline" 
                onClick={() => { setParams({}); setSelectedIds([]) }}> 
          Clear filters 
        </button> 
      </form> 

      <div className="flex flex-wrap items-center justify-between gap-3"> 
        <p className="text-sm text-slate-600"> 
          {loading ? 'Loading vehicles…' : `${vehicles?.length ?? 0} vehicle(s)`} 
        </p> 
        <button type="button" disabled={selectedIds.length < 2} 
                onClick={compareSelected} 
                className="rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-50"> 
          Compare selected ({selectedIds.length}/4) 
        </button> 
      </div> 

      {loading && <Spinner />} 
      <ErrorMessage error={error} /> 
      {!loading && vehicles?.length === 0 && <p className="rounded-lg bg-white p-6 text-slate-600">No vehicles match these filters.</p>} 
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3"> 
        {vehicles?.map((vehicle) => ( 
          <div key={vehicle.id} className="relative"> 
            <label className="absolute right-3 top-3 z-10 flex items-center gap-2 rounded-md bg-white/95 px-2 py-1 text-xs shadow"> 
              <input type="checkbox" checked={selectedIds.includes(vehicle.id)} 
                     disabled={!selectedIds.includes(vehicle.id) && selectedIds.length >= 4} 
                     onChange={() => toggleVehicle(vehicle.id)} /> 
              Compare 
            </label> 
            <VehicleCard vehicle={vehicle} /> 
          </div> 
        ))} 
      </div> 
    </div> 
  ) 
} 
