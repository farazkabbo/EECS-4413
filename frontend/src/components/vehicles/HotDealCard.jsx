import { Link } from 'react-router-dom' 
import { formatPrice, vehicleTitle } from '../../utils/format.js' 

export default function HotDealCard({ deal }) { 
  return ( 
    <Link to={`/vehicles/${deal.id}`} 
          className="relative flex flex-col gap-1 rounded-xl border border-amber-200 bg-white p-4 shadow-sm hover:shadow-md"> 
      <span className="absolute right-3 top-3 rounded-full bg-accent px-2 py-0.5 text-xs font-bold text-white"> 
        -{deal.discountPercent}% 
      </span> 
      <h3 className="pr-12 font-semibold">{vehicleTitle(deal)}</h3> 
      <p className="text-lg font-bold text-brand">{formatPrice(deal.dealPrice)}</p> 
      <p className="text-sm text-slate-500"> 
        <span className="line-through">{formatPrice(deal.price)}</span> - save {formatPrice(deal.savings)} 
      </p> 
    </Link> 
  ) 
} 
