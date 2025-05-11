import { FC, FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import {CustomerController} from "../controller/CustomerController.ts";
import {UserCreationRequestDto} from "../dto/UserCreationRequestDto.ts";
import {UserDto} from "../dto/UserDto.ts";
import {AuthController} from "../controller/AuthConrtoller.ts";

export const Register: FC = () => {
    const navigate = useNavigate()
    const [username, setUsername] = useState('')
    const [email, setEmail]       = useState('')
    const [password, setPassword] = useState('')
    const [confirm, setConfirm]   = useState('')
    const [role, setRole]         = useState<'producer'|'customer'>('producer')
    const [avatar, setAvatar]     = useState<File | null>(null)
    const [error, setError]       = useState<string | null>(null)
    const [success, setSuccess]   = useState<string | null>(null)

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        // Debugging role state
        console.log('Selected role:', role);

        if (password !== confirm) {
            setError("Passwords don’t match");
            return;
        }

        const dto: UserCreationRequestDto = {
            username,
            email,
            password,
            confirmPassword: confirm,
            role,
            avatar: avatar || undefined,
        };

        try {
            const user: UserDto = await AuthController.register(dto);
            setSuccess('Registration successful! Redirecting…');
            setTimeout(() => navigate('/login'), 1500);
        } catch (err: any) {
            setError(err.message);
        }
    };

    return (
        <main
            className="form-page flex items-center justify-center min-h-screen p-5
                 bg-gradient-to-b from-transparent via-[rgba(20,20,20,0.5)] to-[#01202e]
                 bg-[#141414]"
        >
            <div
                className="form-container w-full max-w-[400px] p-10
                   bg-[#1e1e1e] rounded-[10px]
                   shadow-[0_4px_20px_rgba(0,0,0,0.5)] text-white"
            >
                <h1 className="form-container__title text-center text-xl font-semibold mb-8">
                    Registration
                </h1>

                {error && (
                    <p className="form-container__error text-red-500 text-sm mb-4">
                        {error}
                    </p>
                )}
                {success && (
                    <p className="form-container__success text-green-400 text-sm mb-4">
                        {success}
                    </p>
                )}

                <form onSubmit={handleSubmit} encType="multipart/form-data" className="grid gap-1.5">
                    <div className="form__group flex flex-col">
                        <label htmlFor="username" className="form__label mb-2 text-sm font-medium">
                            Username *
                        </label>
                        <input
                            id="username"
                            type="text"
                            required
                            minLength={4}
                            maxLength={16}
                            className="form__input w-full p-3 bg-[#2e2e2e] rounded-[5px]
                         text-white placeholder-gray-400
                         focus:bg-[#3e3e3e] focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                        />
                    </div>

                    <div className="form__group flex flex-col">
                        <label htmlFor="email" className="form__label mb-2 text-sm font-medium">
                            Email *
                        </label>
                        <input
                            id="email"
                            type="email"
                            required
                            className="form__input w-full p-3 bg-[#2e2e2e] rounded-[5px]
                         text-white placeholder-gray-400
                         focus:bg-[#3e3e3e] focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                        />
                    </div>

                    <div className="form__group flex flex-col">
                        <label htmlFor="password" className="form__label mb-2 text-sm font-medium">
                            Password *
                        </label>
                        <input
                            id="password"
                            type="password"
                            required
                            minLength={6}
                            maxLength={24}
                            className="form__input w-full p-3 bg-[#2e2e2e] rounded-[5px]
                         text-white placeholder-gray-400
                         focus:bg-[#3e3e3e] focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                        />
                    </div>

                    <div className="form__group flex flex-col">
                        <label htmlFor="confirm" className="form__label mb-2 text-sm font-medium">
                            Confirm password *
                        </label>
                        <input
                            id="confirm"
                            type="password"
                            required
                            className="form__input w-full p-3 bg-[#2e2e2e] rounded-[5px]
                         text-white placeholder-gray-400
                         focus:bg-[#3e3e3e] focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={confirm}
                            onChange={e => setConfirm(e.target.value)}
                        />
                    </div>

                    <div className="form__group flex flex-col">
                        <span className="form__label mb-2 text-sm font-medium">Choose role *</span>
                        <div className="role-options flex gap-5">
                            <label className="role-options__label inline-flex items-center text-white">
                                <input
                                    type="radio"
                                    name="role"
                                    value="producer"
                                    checked={role === 'producer'}
                                    onChange={() => setRole('producer')}
                                    className="form-radio text-blue-500"
                                />
                                <span className="ml-2">Producer</span>
                            </label>
                            <label className="role-options__label inline-flex items-center text-white">
                                <input
                                    type="radio"
                                    name="role"
                                    value="customer"
                                    checked={role === 'customer'}
                                    onChange={() => setRole('customer')}
                                />
                                <span className="ml-2">Artist</span>
                            </label>
                        </div>
                    </div>

                    <div className="form__group flex flex-col">
                        <label htmlFor="avatar" className="form__label mb-2 text-sm font-medium">
                            Avatar (profile picture)
                        </label>
                        <input
                            id="avatar"
                            type="file"
                            accept="image/*"
                            onChange={e => setAvatar(e.target.files?.[0] || null)}
                            className="form__input block w-full text-gray-300 bg-[#2e2e2e] rounded-[5px]
                         cursor-pointer focus:outline-none"
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
                    Already have an account?{' '}
                    <Link to="/login" className="form-container__link text-[#1db954] hover:text-[#17a44a] hover:underline">
                        Sign In
                    </Link>
                </p>
            </div>
        </main>
    )
}