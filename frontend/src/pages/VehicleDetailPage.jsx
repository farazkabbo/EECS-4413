import { Link, useParams } from 'react-router-dom'
import { useState } from 'react'
import { getVehicle } from '../api/vehicles.js'
import { useApi } from '../hooks/useApi.js'
import Spinner from '../components/ui/Spinner.jsx'
import ErrorMessage from '../components/ui/ErrorMessage.jsx'
import { formatKm, formatPrice, titleCase, vehicleTitle } from '../utils/format.js'
import { addToCart } from '../api/cart.js'

// UC-06 View Vehicle Details. Teammates plug their features in here:
export default function VehicleDetailPage() {
  const { id } = useParams()
  const { data: v, loading, error } = useApi(() => getVehicle(id), [id])
  const [selected, setSelected] = useState([])
  const [cartMessage, setCartMessage] = useState('')
  if (loading) return <Spinner />
  if (error) return <ErrorMessage error={error} />

  async function add() { try { await addToCart(v.id, selected, 1); setCartMessage('Added to cart.') } catch (e) { setCartMessage(e.message) } }
  function chooseOption(category, id) {
    const categoryIds = (v.options?.[category] ?? []).map(o => o.id)
    setSelected(prev => [...prev.filter(optionId => !categoryIds.includes(optionId)), id])
  }

  const specs = [
    ['Range', formatKm(v.rangeKm)], ['Battery', `${v.batteryKwh} kWh`],
    ['Efficiency', `${v.energyKwhPer100km} kWh/100 km`], ['Mileage', formatKm(v.mileageKm)],
    ['Drivetrain', v.drivetrain], ['Seats', v.seats],
    ...(v.batteryHealthPct != null ? [['Battery health', `${v.batteryHealthPct}%`]] : []),
  ]

  return (
    <div className="grid gap-8 lg:grid-cols-3">
      <div className="space-y-6 lg:col-span-2">
        <div className="flex aspect-video items-center justify-center rounded-xl bg-slate-200 text-slate-500">{v.brand} {v.model}</div>
        <div>
          <h1 className="text-3xl font-bold">{vehicleTitle(v)}</h1>
          <p className="mt-2 text-slate-600">{v.description}</p>
        </div>
        <dl className="grid grid-cols-2 gap-4 rounded-xl border border-slate-200 bg-white p-5 sm:grid-cols-3">
          {specs.map(([k, val]) => (
            <div key={k}><dt className="text-xs uppercase text-slate-500">{k}</dt><dd className="font-medium">{val}</dd></div>
          ))}
        </dl>
        {v.history && (
          <section className="rounded-xl border border-slate-200 bg-white p-5">
            <h2 className="mb-2 text-lg font-semibold">Vehicle history report</h2>
            <p className="text-sm text-slate-600">{v.history.reportSummary}</p>
            <p className="mt-2 text-sm">
              Accidents: {v.history.accidentCount} - Damage reported: {v.history.damageReported ? 'yes' : 'no'} -
              Previous owners: {v.history.previousOwners}
            </p>
          </section>
        )}
        <section className="rounded-xl border border-slate-200 bg-white p-5">
          <h2 className="mb-3 text-lg font-semibold">Available options</h2>
          {Object.entries(v.options ?? {}).map(([cat, opts]) => (
            <div key={cat} className="mb-3">
              <h3 className="text-sm font-medium text-slate-500">{titleCase(cat)}</h3>
              <div className="mt-1 flex flex-wrap gap-2">
                {opts.map((o) => (
                  <button type="button" key={o.id} onClick={() => chooseOption(cat, o.id)} className={`flex items-center gap-2 rounded-full border px-3 py-1 text-sm ${selected.includes(o.id) ? 'border-brand bg-blue-50' : 'border-slate-200'}`}>
                    {o.hexCode && <span className="h-3 w-3 rounded-full border" style={{ background: o.hexCode }} />}
                    {o.name}{o.priceDelta > 0 && ` +${formatPrice(o.priceDelta)}`}
                  </button>
                ))}
              </div>
            </div>
          ))}
        </section>
      </div>
      <aside className="space-y-4">
        <div className="rounded-xl border border-slate-200 bg-white p-5">
          <p className="text-3xl font-bold">{formatPrice(v.dealPrice ?? v.price)}</p>
          {v.dealPrice && <p className="text-sm text-slate-400 line-through">{formatPrice(v.price)}</p>}
          <p className="mt-1 text-sm text-slate-500">
            {v.averageRating ? `${v.averageRating} / 5 from ${v.reviewCount} owner review(s)` : 'No reviews yet'}
          </p>
          <button onClick={add} className="mt-4 w-full rounded-lg bg-brand py-3 font-semibold text-white">Add to cart</button>
          {cartMessage && <p className="mt-2 text-sm text-slate-600">{cartMessage}</p>}
          <Link to={`/vehicles/${v.id}/test-drive`} className="mt-2 block w-full rounded-lg border border-brand py-3 text-center font-semibold text-brand">
            Book a test drive
          </Link>
        </div>
        {/* TODO(Tamjeed): loan calculator (UC-09) and savings estimator (UC-20) widgets */}
      </aside>
    </div>
  )
}
