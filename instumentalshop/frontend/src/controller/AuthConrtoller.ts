import {UserCreationRequestDto} from "../dto/UserCreationRequestDto.ts";
import {UserDto} from "../dto/UserDto.ts";

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

    static async register(
        dto: UserCreationRequestDto
    ): Promise<UserDto> {
        const formData = new FormData()
        formData.append("username", dto.username)
        formData.append("email", dto.email)
        formData.append("password", dto.password)
        formData.append("confirmPassword", dto.confirmPassword)
        formData.append("role", dto.role.toUpperCase())
        if (dto.avatar) {
            formData.append("avatar", dto.avatar, dto.avatar.name)
        }

        const res = await fetch("/api/v1/auth/register", {
            method: "POST",
            body: formData,
        })

        if (!res.ok) {
            const text = await res.text()
            throw new Error(text || "Registration failed")
        }
        return res.json()
    }

    static async me(token: string): Promise<UserDto> {
        const res = await fetch('/api/v1/auth/me', {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        })
        if (!res.ok) {
            throw new Error('Failed to fetch profile')
        }
        return res.json()
    }
}