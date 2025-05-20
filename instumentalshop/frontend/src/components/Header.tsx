import {FC} from 'react'
import {Link} from 'react-router-dom'
import {useAuth} from '../context/AuthContext'
import BagIcon from "./icons/BagIcon.tsx";
import {useCart} from "../context/CartContext.tsx";

export const Header: FC = () => {
    const {token, role, logout, user} = useAuth();
    const {items, toggle} = useCart();
    const API_URL = import.meta.env.VITE_API_URL ?? '';

    return (
        <header className="bg-[#141414]">
            <div className="relative container mx-auto max-w-[1400px] px-5 h-[80px] flex items-center">
                {/* ───── logo ───── */}
                <Link to="/" className="flex items-center">
                    <img src="/images/logo.png" alt="Logo" className="h-14 w-auto"/>
                </Link>

                {/* ───── центр. навигация ───── */}
                <nav className="absolute inset-x-0 flex justify-center pointer-events-none">
                    <ul className="flex space-x-8 gap-x-4 pointer-events-auto">
                        <li>
                            <Link to="/"
                                  className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                Home
                            </Link>
                        </li>
                        <li>
                            <Link to="/tracks"
                                  className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                All Tracks
                            </Link>
                        </li>
                        <li>
                            <Link to="/about"
                                  className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                About
                            </Link>
                        </li>
                        {token && role === 'customer' && (
                            <>
                                <li>
                                    <Link to="/purchases"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        Purchases
                                    </Link>
                                </li>
                            </>
                        )}
                        {token && (
                            <>
                                <li>
                                    <Link to="/chats"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        Chats
                                    </Link>
                                </li>
                            </>
                        )}

                        {token && role === 'producer' && (
                            <>
                                <li>
                                    <Link to="/upload"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        Upload
                                    </Link>
                                </li>
                                <li>
                                    <Link to="/sales"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        Sales
                                    </Link>
                                </li>
                                <li>
                                    <Link to="/producer/tracks"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        My tracks
                                    </Link>
                                </li>
                            </>
                        )}

                        {token && role === 'admin' && (
                            <>
                                <li>
                                    <Link to="/admin/purchases"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        All Purchases
                                    </Link>
                                </li>
                                <li>

                                    <Link to="/admin/users"
                                          className="text-[#edf0f1] text-base font-medium hover:text-[#0088a9] transition-colors">
                                        All Users
                                    </Link>

                                </li>
                            </>
                        )}
                    </ul>
                </nav>

                {/* ───── правый блок ───── */}
                <div className="ml-auto flex items-center space-x-4 gap-x-4 relative z-10">
                    {/* иконка корзины */}
                    {token && (
                        <button onClick={toggle} className="relative">
                            <BagIcon className="w-6 h-6 text-[#edf0f1] hover:text-[#0088a9] transition-colors"/>
                            {items.length > 0 && (
                                <span
                                    className="absolute -top-1 -right-2 bg-red-600 text-xs rounded-full
                           h-5 min-w-[20px] px-1 flex items-center justify-center text-white"
                                >
                {items.length}
              </span>
                            )}
                        </button>
                    )}

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
                        <>
                            <Link to="/profile" className="flex items-center space-x-2">
                                <img
                                    src={
                                        user?.avatarUrl
                                            ? `${API_URL}${user.avatarUrl}`
                                            : '/images/default-avatar.png'
                                    }
                                    alt={user?.username || 'User Avatar'}
                                    className="w-10 h-10 rounded-full object-cover"
                                />
                                <div className="flex flex-col text-white text-base px-2">
                                    <span>{user?.username || 'User'}</span>
                                    <span
                                        className="text-sm text-gray-400">Balance: ${user?.balance?.toFixed(2) ?? '0.00'}</span>
                                </div>
                            </Link>
                            <button
                                onClick={logout}
                                className="text-base text-[#edf0f1] hover:text-[#0088a9] transition-colors"
                            >
                                Logout
                            </button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
};
