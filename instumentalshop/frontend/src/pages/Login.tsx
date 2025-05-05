import React, { FormEvent, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

interface LoginFormState {
    email: string
    password: string
}

export const Login: React.FC = () => {
    const { login } = useAuth()
    const [form, setForm] = useState<LoginFormState>({ email: '', password: '' })
    const [error, setError] = useState<string | null>(null)

    const handleChange = useCallback(
        (e: React.ChangeEvent<HTMLInputElement>) => {
            const { name, value } = e.target
            setForm(prev => ({ ...prev, [name]: value }))
        },
        []
    )

    const handleSubmit = useCallback(
        async (e: FormEvent) => {
            e.preventDefault()
            setError(null)
            try {
                const res = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(form),
                })
                if (!res.ok) {
                    const message = await res.text()
                    throw new Error(message || 'Login failed')
                }
                const { token } = await res.json()
                login(token)
            } catch (err: any) {
                setError(err.message)
            }
        },
        [form, login]
    )

    return (
        <main className="form-page form-page--login">
            <div className="form-container">
                <h1 className="form-container__title">Sign In</h1>
                {error && <p className="form__error">{error}</p>}
                <form className="form" onSubmit={handleSubmit} noValidate>
                    <div className="form__group">
                        <label htmlFor="email" className="form__label">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            className="form__input"
                            value={form.email}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="form__group">
                        <label htmlFor="password" className="form__label">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            className="form__input"
                            value={form.password}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <button type="submit" className="form__button">Continue</button>
                    <p className="form__footer">Don't have an account?</p>
                    <Link to="/register" className="form-container__link">
                        Create an account
                    </Link>
                </form>
            </div>
        </main>
    )
}