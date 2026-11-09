import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext.jsx'

const links = [
  { to: '/vehicles', label: 'Vehicles' },
  { to: '/hot-deals', label: 'Hot deals' },
  { to: '/compare', label: 'Compare' },
  { to: '/calculators', label: 'Calculators' },
  { to: '/recommendations', label: 'For you' },
]

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth()
  const linkClass = ({ isActive }) =>
    `rounded-md px-3 py-2 text-sm font-medium ${isActive ? 'bg-brand text-white' : 'text-slate-700 hover:bg-slate-100'}`

  return (
    <header className="border-b border-slate-200 bg-white">
      <nav className="mx-auto flex max-w-6xl flex-wrap items-center gap-2 px-4 py-3">
        <Link to="/" className="mr-4 text-xl font-bold text-brand">Fritz EV</Link>
        {links.map((l) => (
          <NavLink key={l.to} to={l.to} className={linkClass}>{l.label}</NavLink>
        ))}
        {isAdmin && <NavLink to="/admin" className={linkClass}>Admin</NavLink>}
        <div className="ml-auto flex items-center gap-2">
          <NavLink to="/cart" className={linkClass}>Cart</NavLink>
          {user ? (
            <button onClick={logout} className="rounded-md px-3 py-2 text-sm text-slate-700 hover:bg-slate-100">
              Sign out ({user.username})
            </button>
          ) : (
            <NavLink to="/login" className={linkClass}>Sign in</NavLink>
          )}
        </div>
      </nav>
    </header>
  )
}
