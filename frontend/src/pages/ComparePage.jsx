import { Link, useSearchParams } from 'react-router-dom'
import { compareVehicles } from '../api/vehicles.js'
import { useApi } from '../hooks/useApi.js'
import Spinner from '../components/ui/Spinner.jsx'
import ErrorMessage from '../components/ui/ErrorMessage.jsx'
import { formatKm, formatPrice } from '../utils/format.js'

export default function ComparePage() {
  const [params] = useSearchParams()
  const ids = (params.get('ids') ?? '').split(',').map((id) => id.trim()).filter(Boolean)
  const canCompare = ids.length >= 2 && ids.length <= 4 && ids.every((id) => /^\d+$/.test(id))
  const { data, loading, error } = useApi(
    () => canCompare ? compareVehicles(ids) : Promise.resolve([]),
    [ids.join(','), canCompare],
  )
  const vehicles = Array.isArray(data) ? data : data?.vehicles ?? []

  const rows = [
    ['Price', (vehicle) => formatPrice(vehicle.price)],
    ['Model year', (vehicle) => vehicle.year ?? vehicle.modelYear],
    ['Range', (vehicle) => formatKm(vehicle.rangeKm)],
    ['Battery', (vehicle) => `${vehicle.batteryKwh} kWh`],
    ['Energy consumption', (vehicle) => `${vehicle.energyKwhPer100km} kWh/100 km`],
    ['Mileage', (vehicle) => formatKm(vehicle.mileageKm)],
    ['Owner rating', (vehicle) => vehicle.averageRating == null ? 'No reviews' : `${vehicle.averageRating} / 5`],
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Compare vehicles</h1>
        <p className="mt-1 text-slate-600">Compare price, range, efficiency, mileage, and owner ratings side by side.</p>
      </div>
      {!canCompare && (
        <p className="rounded-lg bg-amber-50 p-4 text-amber-900">
          Select two to four vehicles from the <Link className="font-semibold underline" to="/vehicles">catalogue</Link> to compare.
        </p>
      )}
      {loading && canCompare && <Spinner />}
      {canCompare && <ErrorMessage error={error} />}
      {canCompare && !loading && vehicles.length > 0 && (
        <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white">
          <table className="w-full min-w-[36rem] border-collapse text-left text-sm">
            <thead className="bg-slate-50">
              <tr>
                <th scope="col" className="border-b p-4">Specification</th>
                {vehicles.map((vehicle) => (
                  <th scope="col" key={vehicle.id} className="border-b p-4">
                    <Link to={`/vehicles/${vehicle.id}`} className="font-semibold text-brand hover:underline">
                      {vehicle.title ?? `${vehicle.year ?? vehicle.modelYear} ${vehicle.brand ?? ''} ${vehicle.model ?? ''}`}
                    </Link>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {rows.map(([label, value]) => (
                <tr key={label} className="border-b last:border-0">
                  <th scope="row" className="bg-slate-50 p-4 font-medium">{label}</th>
                  {vehicles.map((vehicle) => <td key={vehicle.id} className="p-4">{value(vehicle)}</td>)}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
