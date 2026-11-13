import { Link } from 'react-router-dom' 
import { formatKm, formatPrice, titleCase, vehicleTitle } from '../../utils/format.js' 

export default function VehicleCard({ vehicle: v }) { 
  const onDeal = v.dealPrice != null && v.dealPrice < v.price 
  return ( 
    <Link to={`/vehicles/${v.id}`} 
          className="group flex flex-col overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm transition hover:shadow-md"> 
      <div className="flex aspect-video items-center justify-center bg-slate-100 text-slate-400"> 
        {/* TODO(D3): real images under frontend/public/images/vehicles */} 
        <span className="text-sm">{v.brand} {v.model}</span> 
      </div> 
      <div className="flex flex-1 flex-col gap-1 p-4"> 
        <div className="flex items-center gap-2 text-xs"> 
          <span className="rounded bg-slate-100 px-2 py-0.5">{titleCase(v.condition)}</span> 
          <span className="rounded bg-slate-100 px-2 py-0.5">{titleCase(v.shape)}</span> 
          {onDeal && <span className="rounded bg-accent px-2 py-0.5 font-semibold text-white">Hot deal</span>} 
        </div> 
        <h3 className="font-semibold group-hover:text-brand">{vehicleTitle(v)}</h3> 
        <p className="text-sm text-slate-500">{formatKm(v.rangeKm)} range - {formatKm(v.mileageKm)} driven</p> 
        <p className="mt-auto pt-2 text-lg font-bold"> 
          {onDeal ? ( 
            <>{formatPrice(v.dealPrice)} <span className="text-sm font-normal text-slate-400 line-through">{formatPrice(v.price)}</span></> 
          ) : formatPrice(v.price)} 
        </p> 
      </div> 
    </Link> 
  ) 
} 
