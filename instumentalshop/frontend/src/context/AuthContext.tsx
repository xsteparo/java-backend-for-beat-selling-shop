import React, {createContext, ReactNode, useCallback, useContext, useEffect, useState} from 'react'
import {useNavigate} from 'react-router-dom'
import {jwtDecode} from 'jwt-decode'

interface JWTPayload {
    sub: string
    role: string
    exp: number
}

type Role = 'guest' | 'user' | 'producer' | 'admin'

interface AuthContextType {
    token: string | null
    role: Role
    login: (token: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: ReactNode }> = ({children}) => {
    const [token, setToken] = useState<string | null>(() => localStorage.getItem('beatshop_jwt'))
    const [role, setRole] = useState<Role>(() => {
        const saved = localStorage.getItem('beatshop_jwt')
        if (saved) {
            try {
                const {role: r} = jwtDecode<JWTPayload>(saved)
                return r as Role
            } catch {
                return 'guest'
            }
        }
        return 'guest'
    })
    const navigate = useNavigate()

    useEffect(() => {
        if (token) {
            localStorage.setItem('beatshop_jwt', token)
            const {role: r} = jwtDecode<JWTPayload>(token)
            setRole(r as Role)
        }
    }, [token])

    const login = useCallback((newToken: string) => {
        setToken(newToken)
        navigate('/')
    }, [navigate])

    const logout = useCallback(() => {
        setToken(null)
        setRole('guest')
        localStorage.removeItem('beatshop_jwt')
        navigate('/login')
    }, [navigate])

    return (
        <AuthContext.Provider value={{token, role, login, logout}}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext)
    if (!context) throw new Error('useAuth must be used within AuthProvider')
    return context
}