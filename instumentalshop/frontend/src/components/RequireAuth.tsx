import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

interface RequireAuthProps {
    allowedRoles: Array<'customer' | 'producer' | 'admin'>
}

export function RequireAuth({ allowedRoles }: RequireAuthProps) {
    const { role } = useAuth()

    if (role === 'guest' || !allowedRoles.includes(role as any)) {
        return <Navigate to="/login" replace />
    }

    return <Outlet />
}
