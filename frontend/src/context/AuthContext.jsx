import { createContext, useCallback, useContext, useEffect, useState } from 'react' 
import * as authApi from '../api/auth.js' 

// Who is signed in, shared by every page. Owner: Zaaid (UC-01..UC-03). 
const AuthContext = createContext(null) 

export function AuthProvider({ children }) { 
  const [user, setUser] = useState(null) 

  // Restore the session on page load (401 simply means "not signed in"). 
  useEffect(() => { 
    authApi.csrf().catch(() => {}).finally(() => authApi.me().then(setUser).catch(() => setUser(null))) 
  }, []) 

  const login = useCallback(async (username, password) => { 
    await authApi.csrf() 
    const u = await authApi.login(username, password) 
    setUser(u) 
    return u 
  }, []) 

  const logout = useCallback(async () => { 
    await authApi.logout().catch(() => {}) 
    setUser(null) 
  }, []) 

  return <AuthContext.Provider value={{ user, login, logout, isAdmin: user?.role === 'ADMIN' }}>{children}</AuthContext.Provider> 
} 

export const useAuth = () => useContext(AuthContext) 
