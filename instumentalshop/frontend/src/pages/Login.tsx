import { FC, FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {AuthController} from "../controller/AuthConrtoller.ts";

export const Login: FC = () => {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState<string | null>(null)



    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setError(null)
        try {
            const { token } = await AuthController.login(username, password)
            login(token)
            navigate('/')
        } catch (err: any) {
            setError(err.message)
        }
    }

    return (
        <main
            className="form-page flex items-center justify-center min-h-screen p-5
                 bg-gradient-to-b from-transparent via-[rgba(20,20,20,0.5)] to-[#0B2918]
                 bg-[#141414]"
        >
            <div
                className="form-container w-full max-w-[400px] p-10
                   bg-[#1e1e1e] rounded-[10px]
                   shadow-[0_4px_20px_rgba(0,0,0,0.5)] text-white"
            >
                <h1 className="form-container__title text-center text-xl font-semibold mb-8">
                    Sign In
                </h1>

                {error && (
                    <p className="form-container__error text-red-500 text-sm mb-4">
                        {error}
                    </p>
                )}

                <form onSubmit={handleSubmit} className="grid gap-1.5">
                    <div className="form__group flex flex-col">
                        <label htmlFor="username" className="form__label mb-2 text-sm font-medium">
                            Username
                        </label>
                        <input
                            id="username"
                            type="text"
                            required
                            className="form__input w-full p-3 bg-[#2e2e2e] rounded-[5px]
                         text-white placeholder-gray-400
                         focus:bg-[#3e3e3e] focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                        />
                    </div>

                    <div className="form__group flex flex-col">
                        <label htmlFor="password" className="form__label mb-2 text-sm font-medium">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            required
                            className="form__input w-full p-3 bg-[#2e2e2e] rounded-[5px]
                         text-white placeholder-gray-400
                         focus:bg-[#3e3e3e] focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                        />
                    </div>

                    <button
                        type="submit"
                        className="form__button w-full py-4 bg-[#2f2f2f] rounded-[5px]
                       text-white font-medium transition-colors hover:bg-[#00aaff]"
                    >
                        Continue
                    </button>
                </form>

                <p className="form__footer mt-8 text-sm text-gray-400">
                    Don’t have an account?{' '}
                    <Link to="/register" className="form-container__link text-[#1db954] hover:text-[#17a44a] hover:underline">
                        Create an account
                    </Link>
                </p>
            </div>
        </main>
    )
}
