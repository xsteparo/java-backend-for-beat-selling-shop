
export interface LoginResponse {
    token: string
}

export class AuthController {
    static async login(
        username: string,
        password: string
    ): Promise<LoginResponse> {
        const res = await fetch('/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        })
        if (!res.ok) {
            const text = await res.text()
            throw new Error(text || 'Login failed')
        }
        return res.json()
    }
}