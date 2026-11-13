import { useState } from 'react' 
import { Link, useParams } from 'react-router-dom' 
import { loanQuote, savingsEstimate } from '../api/services.js' 
import { getVehicle, listVehicles } from '../api/vehicles.js' 
import { useApi } from '../hooks/useApi.js' 
import ErrorMessage from '../components/ui/ErrorMessage.jsx' 
import Spinner from '../components/ui/Spinner.jsx' 
import { formatPrice, vehicleTitle } from '../utils/format.js' 

const fieldClass = 'mt-1 w-full rounded-md border border-slate-300 px-3 py-2' 

export default function CalculatorsPage() { 
  const { id } = useParams() 
  const [selectedId, setSelectedId] = useState(id ?? '') 
  const [loan, setLoan] = useState(null) 
  const [savings, setSavings] = useState(null) 
  const [loanError, setLoanError] = useState('') 
  const [savingsError, setSavingsError] = useState('') 
  const [loanBusy, setLoanBusy] = useState(false) 
  const [savingsBusy, setSavingsBusy] = useState(false) 
  const { data: vehicles, loading: listLoading, error: listError } = useApi( 
    () => id ? Promise.resolve([]) : listVehicles({}), 
    [id], 
  ) 
  const { data: vehicle, loading: vehicleLoading, error: vehicleError } = useApi( 
    () => selectedId ? getVehicle(selectedId) : Promise.resolve(null), 
    [selectedId], 
  ) 

  const price = vehicle ? (vehicle.dealPrice ?? vehicle.price) : null 

  async function submitLoan(event) { 
    event.preventDefault() 
    setLoanBusy(true) 
    setLoanError('') 
    setLoan(null) 
    const form = new FormData(event.currentTarget) 
    try { 
      const result = await loanQuote({ 
        price: Number(price), 
        downPayment: Number(form.get('downPayment')), 
        annualRatePercent: Number(form.get('annualRatePercent')), 
        months: Number(form.get('months')), 
      }) 
      setLoan(result) 
    } catch (error) { 
      setLoanError(error.message) 
    } finally { 
      setLoanBusy(false) 
    } 
  } 

  async function submitSavings(event) { 
    event.preventDefault() 
    setSavingsBusy(true) 
    setSavingsError('') 
    setSavings(null) 
    const form = new FormData(event.currentTarget) 
    try { 
      const result = await savingsEstimate(selectedId, { 
        annualKm: Number(form.get('annualKm')), 
        gasolineLPer100km: Number(form.get('gasolineLPer100km')), 
        gasolinePricePerL: Number(form.get('gasolinePricePerL')), 
        electricityPricePerKwh: Number(form.get('electricityPricePerKwh')), 
      }) 
      setSavings(result) 
    } catch (error) { 
      setSavingsError(error.message) 
    } finally { 
      setSavingsBusy(false) 
    } 
  } 

  return ( 
    <div className="space-y-6"> 
      <div> 
        <h1 className="text-3xl font-bold">EV calculators</h1> 
        <p className="mt-1 text-slate-600">Estimate monthly financing, fuel savings, and emissions avoided.</p> 
      </div> 

      {!id && ( 
        <label className="block max-w-xl text-sm font-medium"> 
          Select an electric vehicle 
          <select className={fieldClass} value={selectedId} onChange={(event) => { 
            setSelectedId(event.target.value) 
            setLoan(null) 
            setSavings(null) 
          }}> 
            <option value="">Choose a vehicle</option> 
            {(vehicles ?? []).map((item) => ( 
              <option key={item.id} value={item.id}>{vehicleTitle(item)}</option> 
            ))} 
          </select> 
        </label> 
      )} 
      {listLoading && !id && <Spinner />} 
      <ErrorMessage error={listError} /> 
      {vehicleLoading && <Spinner />} 
      <ErrorMessage error={vehicleError} /> 

      {vehicle && ( 
        <> 
          <div className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-4"> 
            <div> 
              <p className="font-semibold">{vehicleTitle(vehicle)}</p> 
              <p className="text-sm text-slate-600">Price used: {formatPrice(price)}</p> 
            </div> 
            <Link className="text-sm text-brand underline" to={`/vehicles/${vehicle.id}`}>View vehicle details</Link> 
          </div> 

          <div className="grid gap-6 lg:grid-cols-2"> 
            <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5"> 
              <div> 
                <h2 className="text-xl font-semibold">Loan calculator</h2> 
                <p className="text-sm text-slate-600">Estimate monthly payments using the vehicle price above.</p> 
              </div> 
              {loanError && <p role="alert" className="rounded bg-red-50 p-3 text-sm text-red-700">{loanError}</p>} 
              <form className="space-y-3" onSubmit={submitLoan}> 
                <label className="block text-sm">Down payment ($) 
                  <input className={fieldClass} name="downPayment" type="number" min="0" max={price} 
                         step="0.01" defaultValue="0" required /> 
                </label> 
                <label className="block text-sm">Annual interest rate (%) 
                  <input className={fieldClass} name="annualRatePercent" type="number" min="0" max="30" 
                         step="0.01" defaultValue="5" required /> 
                </label> 
                <label className="block text-sm">Loan term (months) 
                  <input className={fieldClass} name="months" type="number" min="1" max="120" 
                         step="1" defaultValue="60" required /> 
                </label> 
                <button disabled={loanBusy} className="rounded-lg bg-brand px-4 py-2 font-semibold text-white disabled:opacity-50"> 
                  {loanBusy ? 'Calculating…' : 'Calculate payment'} 
                </button> 
              </form> 
              {loan && ( 
                <dl className="grid grid-cols-2 gap-3 rounded-lg bg-slate-50 p-4 text-sm"> 
                  <dt>Principal</dt><dd className="text-right font-medium">{formatPrice(loan.principal)}</dd> 
                  <dt>Monthly payment</dt><dd className="text-right font-semibold">{formatPrice(loan.monthlyPayment)}</dd> 
                  <dt>Total paid</dt><dd className="text-right font-medium">{formatPrice(loan.totalPaid)}</dd> 
                  <dt>Total interest</dt><dd className="text-right font-medium">{formatPrice(loan.totalInterest)}</dd> 
                </dl> 
              )} 
            </section> 

            <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5"> 
              <div> 
                <h2 className="text-xl font-semibold">Fuel savings &amp; CO₂ impact</h2> 
                <p className="text-sm text-slate-600">Compare annual gasoline costs with charging this EV.</p> 
              </div> 
              {savingsError && <p role="alert" className="rounded bg-red-50 p-3 text-sm text-red-700">{savingsError}</p>} 
              <form className="space-y-3" onSubmit={submitSavings}> 
                <label className="block text-sm">Annual driving distance (km) 
                  <input className={fieldClass} name="annualKm" type="number" min="0.01" max="200000" 
                         step="0.01" defaultValue="20000" required /> 
                </label> 
                <label className="block text-sm">Gasoline vehicle consumption (L/100 km) 
                  <input className={fieldClass} name="gasolineLPer100km" type="number" min="0.01" max="30" 
                         step="0.01" defaultValue="8" required /> 
                </label> 
                <label className="block text-sm">Gasoline price ($/L) 
                  <input className={fieldClass} name="gasolinePricePerL" type="number" min="0.01" 
                         step="0.001" defaultValue="1.60" required /> 
                </label> 
                <label className="block text-sm">Electricity price ($/kWh) 
                  <input className={fieldClass} name="electricityPricePerKwh" type="number" min="0.01" 
                         step="0.001" defaultValue="0.15" required /> 
                </label> 
                <button disabled={savingsBusy} className="rounded-lg bg-brand px-4 py-2 font-semibold text-white disabled:opacity-50"> 
                  {savingsBusy ? 'Estimating…' : 'Estimate savings'} 
                </button> 
              </form> 
              {savings && ( 
                <div className="space-y-3 rounded-lg bg-slate-50 p-4 text-sm"> 
                  <dl className="grid grid-cols-2 gap-2"> 
                    <dt>Gasoline cost / year</dt><dd className="text-right">{formatPrice(savings.yearlyGasolineCost)}</dd> 
                    <dt>Charging cost / year</dt><dd className="text-right">{formatPrice(savings.yearlyChargingCost)}</dd> 
                    <dt>Yearly savings</dt><dd className="text-right font-semibold">{formatPrice(savings.yearlySavings)}</dd> 
                    <dt>Five-year savings</dt><dd className="text-right font-semibold">{formatPrice(savings.fiveYearSavings)}</dd> 
                    <dt>Gasoline CO₂ / year</dt><dd className="text-right">{savings.co2GasolineKg} kg</dd> 
                    <dt>EV CO₂ / year</dt><dd className="text-right">{savings.co2ElectricKg} kg</dd> 
                    <dt>CO₂ avoided / year</dt><dd className="text-right font-semibold">{savings.co2AvoidedKg} kg</dd> 
                  </dl> 
                  <div> 
                    <h3 className="font-semibold">Assumptions</h3> 
                    <ul className="mt-1 list-disc space-y-1 pl-5 text-slate-600"> 
                      {(savings.assumptions ?? []).map((assumption) => <li key={assumption}>{assumption}</li>)} 
                    </ul> 
                  </div> 
                </div> 
              )} 
            </section> 
          </div> 
        </> 
      )} 
      {!selectedId && !listLoading && <p className="rounded-lg bg-slate-50 p-4 text-slate-600">Choose a vehicle to use the calculators.</p>} 
    </div> 
  ) 
} 
