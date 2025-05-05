import React, { FormEvent, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'

interface RegisterFormState {
    username: string
    email: string
    password: string
    confirmPassword: string
    role: 'producer' | 'performer'
    avatar?: File
}

export const Register: React.FC = () => {
    const navigate = useNavigate()
    const [form, setForm] = useState<RegisterFormState>({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: 'producer',
    })
    const [errors, setErrors] = useState<Partial<Record<keyof RegisterFormState | 'form', string>>>({})

    const handleChange = useCallback(
        (e: React.ChangeEvent<HTMLInputElement>) => {
            const { name, value, files, type } = e.target
            setForm(prev => ({
                ...prev,
                [name]: type === 'file' && files ? files[0] : value,
            } as any))
        },
        []
    )

    const validate = (): boolean => {
        const errs: typeof errors = {}
        if (!form.username) errs.username = 'Username is required'
        if (!form.email) errs.email = 'Email is required'
        if (!form.password) errs.password = 'Password is required'
        if (form.password !== form.confirmPassword) errs.confirmPassword = 'Passwords do not match'
        if (!form.role) errs.role = 'Role is required'
        setErrors(errs)
        return Object.keys(errs).length === 0
    }

    const handleSubmit = useCallback(
        async (e: FormEvent) => {
            e.preventDefault()
            if (!validate()) return
            const data = new FormData()
            Object.entries(form).forEach(([key, val]) => {
                if (val !== undefined) data.append(key, val as any)
            })
            try {
                const res = await fetch('/api/auth/register', { method: 'POST', body: data })
                if (!res.ok) throw new Error(await res.text())
                navigate('/login')
            } catch (err: any) {
                setErrors({ form: err.message })
            }
        },
        [form, navigate]
    )

    return (
        <main className="form-page form-page--register">
            <div className="form-container">
                <h1 className="form-container__title">Registration</h1>
                {errors.form && <p className="form__error">{errors.form}</p>}
                <form className="form" onSubmit={handleSubmit} noValidate encType="multipart/form-data">
                    {(['username', 'email', 'password', 'confirmPassword'] as const).map(field => (
                        <div key={field} className="form__group">
                            <label htmlFor={field} className="form__label">
                                {field === 'confirmPassword' ? 'Confirm password *' : `${field.charAt(0).toUpperCase() + field.slice(1)} *`}
                            </label>
                            <input
                                id={field}
                                name={field}
                                type={field.includes('password') ? 'password' : 'text'}
                                className="form__input"
                                value={form[field] as string}
                                onChange={handleChange}
                                required
                            />
                            <p className="form__error">{errors[field]}</p>
                        </div>
                    ))}

                    <div className="form__group">
                        <label className="form__label">Choose role *</label>
                        <div className="role-options">
                            {(['producer', 'performer'] as const).map(r => (
                                <label key={r} className="role-options__label">
                                    <input
                                        type="radio"
                                        name="role"
                                        value={r}
                                        className="role-options__radio"
                                        checked={form.role === r}
                                        onChange={handleChange}
                                        required
                                    />
                                    {r === 'producer' ? 'Producer' : 'Artist'}
                                </label>
                            ))}
                        </div>
                        <p className="form__error">{errors.role}</p>
                    </div>

                    <div className="form__group">
                        <label htmlFor="avatar" className="form__label">Avatar (profile picture)</label>
                        <input
                            type="file"
                            id="avatar"
                            name="avatar"
                            accept="image/*"
                            className="form__input"
                            onChange={handleChange}
                        />
                        <p className="form__error">{errors.avatar}</p>
                    </div>

                    <button type="submit" className="form__button">Continue</button>
                </form>
            </div>
        </main>
    )
}