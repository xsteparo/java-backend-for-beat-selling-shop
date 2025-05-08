// src/components/RequireGuest.tsx
import { FC } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export const RequireGuest: FC = () => {
    const { token } = useAuth()
    return token
        ? <Navigate to="/" replace />
        : <Outlet />
}
