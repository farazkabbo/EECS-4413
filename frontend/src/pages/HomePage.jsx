import { Link } from 'react-router-dom'
import { getHotDeals } from '../api/vehicles.js'
import { useApi } from '../hooks/useApi.js'
import HotDealCard from '../components/vehicles/HotDealCard.jsx'
import Spinner from '../components/ui/Spinner.jsx'
import ErrorMessage from '../components/ui/ErrorMessage.jsx'

export default function HomePage() {
  const { data: deals, loading, error } = useApi(() => getHotDeals(3), [])
  return (
    <div className="space-y-10">
      <section className="rounded-2xl bg-brand px-8 py-14 text-white">
        <h1 className="text-4xl font-bold">Find your next electric vehicle</h1>
        <p className="mt-3 max-w-xl text-teal-50">New and pre-owned EVs with full history reports, financing estimates and at-home test drives.</p>
        <Link to="/vehicles" className="mt-6 inline-block rounded-lg bg-white px-5 py-3 font-semibold text-brand hover:bg-teal-50">
          Browse vehicles
        </Link>
      </section>
      <section>
        <div className="mb-4 flex items-baseline justify-between">
          <h2 className="text-2xl font-semibold">Hot deals</h2>
          <Link to="/hot-deals" className="text-sm text-brand hover:underline">See all</Link>
        </div>
        {loading ? <Spinner /> : <ErrorMessage error={error} />}
        <div className="grid gap-4 sm:grid-cols-3">
          {deals?.map((d) => <HotDealCard key={d.id} deal={d} />)}
        </div>
      </section>
    </div>
  )
}
