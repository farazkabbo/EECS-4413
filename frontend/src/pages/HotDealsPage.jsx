import { getHotDeals } from '../api/vehicles.js'
import { useApi } from '../hooks/useApi.js'
import HotDealCard from '../components/vehicles/HotDealCard.jsx'
import Spinner from '../components/ui/Spinner.jsx'
import ErrorMessage from '../components/ui/ErrorMessage.jsx'

export default function HotDealsPage() {
  const { data: deals, loading, error } = useApi(() => getHotDeals(50), [])
  return (
    <div>
      <h1 className="mb-6 text-3xl font-bold">Hot deals</h1>
      {loading && <Spinner />}
      <ErrorMessage error={error} />
      {deals?.length === 0 && <p className="text-slate-500">No deals right now.</p>}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {deals?.map((d) => <HotDealCard key={d.id} deal={d} />)}
      </div>
    </div>
  )
}
