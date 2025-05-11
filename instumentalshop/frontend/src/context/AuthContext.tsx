import React, {
    createContext,
    useState,
    useEffect,
    useCallback,
    useContext,
    ReactNode
} from 'react'
import { jwtDecode } from 'jwt-decode'
import { useNavigate } from 'react-router-dom'
import { AuthController } from '../controller/AuthConrtoller.ts'
import { UserDto } from '../dto/UserDto'

interface JWTPayload {
    sub: string
    role: string
    exp: number
}

export type Role = 'guest' | 'customer' | 'producer' | 'admin'

interface AuthContextType {
    token: string | null
    role: Role
    user: UserDto | null
    login: (token: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const navigate = useNavigate()

    const [token, setToken] = useState<string | null>(() =>
        localStorage.getItem('beatshop_jwt')
    )
    const [role, setRole] = useState<Role>('guest')
    const [user, setUser] = useState<UserDto | null>(null)

    useEffect(() => {
        if (!token) {
            // no token → guest
            localStorage.removeItem('beatshop_jwt')
            setRole('guest')
            setUser(null)
            return
        }

        // have token → store and fetch profile
        localStorage.setItem('beatshop_jwt', token)

        AuthController.me(token)
            .then(u => {
                setUser(u)
                // backend returns e.g. "CUSTOMER", "PRODUCER", "ADMIN"
                const normalized = u.role.toLowerCase() as Role
                setRole(normalized)
            })
            .catch(err => {
                console.error('Failed to load profile, logging out', err)
                // invalid or expired token → clear
                setToken(null)
            })
    }, [token])

    const login = useCallback(
        (newToken: string) => {
            setToken(newToken)
            navigate('/')
        },
        [navigate]
    )

    const logout = useCallback(() => {
        setToken(null)
        navigate('/login')
    }, [navigate])

    return (
        <AuthContext.Provider value={{ token, role, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = (): AuthContextType => {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth must be used within AuthProvider')
    return ctx
}