import {FC} from 'react'
import {Link} from 'react-router-dom'
import {useAuth} from '../context/AuthContext'

export const Header: FC = () => {
    const {token, role, logout, user} = useAuth()
    const API_URL = import.meta.env.VITE_API_URL ?? ''
    console.log(role)
    return (
        <header className="bg-[#141414]">
            <div className="relative container mx-auto max-w-[1400px] px-5 h-[80px] flex items-center">
                <div className="flex items-center">
                    <Link to="/" className="flex items-center">
                        <img
                            src="/images/logo.png"
                            alt="Logo"
                            className="h-14 w-auto"
                        />
                    </Link>
                </div>

                <nav className="absolute inset-x-0 flex justify-center pointer-events-none">
                    <ul className="flex space-x-8 gap-x-4 pointer-events-auto">
                        {/* Public */}
                        <li>
                            <Link
                                to="/"
                                className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                            >
                                Home
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/tracks"
                                className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                            >
                                All Tracks
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/about"
                                className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                            >
                                About
                            </Link>
                        </li>

                        {token && (
                            <>
                                <li>
                                    <Link
                                        to="/purchases"
                                        className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                                    >
                                        Purchases
                                    </Link>
                                </li>
                                <li>
                                    <Link
                                        to="/chats"
                                        className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                                    >
                                        Chats
                                    </Link>
                                </li>
                            </>
                        )}

                        {/* Producer-only */}
                        {token && role === 'producer' && (
                            <>
                                <li>
                                    <Link
                                        to="/upload"
                                        className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                                    >
                                        Upload
                                    </Link>
                                </li>
                                <li>
                                    <Link
                                        to="/sales"
                                        className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                                    >
                                        Sales
                                    </Link>
                                </li>
                            </>
                        )}

                        {token && role === 'admin' && (
                            <li>
                                <Link
                                    to="/admin/purchases"
                                    className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors"
                                >
                                    Admin Purchases
                                </Link>
                            </li>
                        )}
                    </ul>
                </nav>

                <div className="ml-auto flex items-center space-x-4 relative z-10 gap-x-4">
                    {!token ? (
                        <>
                            <Link
                                to="/login"
                                className="px-5 py-1 bg-[#0088a9] rounded-full text-white text-base hover:bg-[#0088a9]/80 transition"
                            >
                                Sign In
                            </Link>
                            <Link
                                to="/register"
                                className="px-5 py-1 bg-[#0088a9] rounded-full text-white text-base hover:bg-[#0088a9]/80 transition"
                            >
                                Sign Up
                            </Link>
                        </>
                    ) : (
                        <div className="flex items-center space-x-4 gap-x-4">
                            <Link
                                to="/profile"
                                className="flex items-center space-x-2"
                            >
                                <img
                                    src={
                                        user?.avatarUrl
                                            ? `${API_URL}${user.avatarUrl}`    // "http://localhost:8080/uploads/avatars/..."
                                            : "/images/default-avatar.png"
                                    } alt={user?.username || "User Avatar"}
                                    className="w-10 h-10 rounded-full object-cover mr-2"
                                />
                                <span className="text-white text-base px-2">
                  {user?.username || 'User'}
                </span>
                            </Link>
                            <button
                                onClick={logout}
                                className="text-base text-[#edf0f1] hover:text-[#0088a9] transition"
                            >
                                Logout
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </header>
    )
}