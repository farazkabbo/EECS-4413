import { useAuth } from '../../context/AuthContext.jsx'
import ComingSoon from '../../components/ui/ComingSoon.jsx'

// UC-17 / UC-18 / UC-19 - owner: Ronak. The API already rejects non-admins (401/403).
export default function AdminDashboardPage() {
  const { isAdmin } = useAuth()
  if (!isAdmin) return <p className="text-slate-500">Administrators only.</p>
  return <ComingSoon title="Admin dashboard" useCase="UC-17 / UC-18 / UC-19" owner="Ronak" />
}
