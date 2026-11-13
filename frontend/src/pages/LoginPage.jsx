import { useState } from 'react' 
import { Link, useLocation, useNavigate } from 'react-router-dom' 
import { useAuth } from '../context/AuthContext.jsx' 

export default function LoginPage() { 
  const { login } = useAuth(); const navigate = useNavigate(); const location = useLocation() 
  const [form, setForm] = useState({ username: '', password: '' }); const [error, setError] = useState(''); const [busy, setBusy] = useState(false) 
  async function submit(e) { e.preventDefault(); setError(''); setBusy(true); try { await login(form.username, form.password); navigate(location.state?.from ?? '/cart') } catch (err) { setError(err.message) } finally { setBusy(false) } } 
  return <div className="mx-auto max-w-md rounded-xl border border-slate-200 bg-white p-6 shadow-sm"><h1 className="text-2xl font-bold">Sign in</h1>{error && <p className="mt-3 rounded bg-red-50 p-3 text-sm text-red-700">{error}</p>}<form onSubmit={submit} className="mt-5 space-y-4"><label className="block text-sm font-medium">Username<input required value={form.username} onChange={e => setForm({...form, username:e.target.value})} className="mt-1 w-full rounded border p-2" /></label><label className="block text-sm font-medium">Password<input required type="password" value={form.password} onChange={e => setForm({...form, password:e.target.value})} className="mt-1 w-full rounded border p-2" /></label><button disabled={busy} className="w-full rounded bg-brand py-2 font-semibold text-white disabled:opacity-50">{busy ? 'Signing in…' : 'Sign in'}</button></form><p className="mt-4 text-sm text-slate-600">New here? <Link className="text-brand underline" to="/register">Create an account</Link></p></div> 
} 
