// Placeholder for pages a teammate has not built yet. 
export default function ComingSoon({ title, useCase, owner }) { 
  return ( 
    <div className="rounded-xl border border-dashed border-slate-300 bg-white p-10 text-center"> 
      <h1 className="text-2xl font-semibold">{title}</h1> 
      <p className="mt-2 text-slate-500">{useCase} - owner: {owner}</p> 
    </div> 
  ) 
} 
