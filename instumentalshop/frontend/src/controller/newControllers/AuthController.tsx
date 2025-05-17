import { LoginResponse } from "../AuthConrtoller"
import {RefreshTokenRequestDto} from "../../dto/newDto/auth/RefreshTokenRequestDto.tsx";
import {UserDto} from "../../dto/newDto/auth/UserDto.tsx";
import {UserCreationRequestDto} from "../../dto/newDto/auth/UserCreationRequestDto.tsx";

export class AuthController {
    private static readonly BASE = '/api/v1/auth'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? {Authorization: `Bearer ${token}`} : {}
    }

    static async login(username: string, password: string): Promise<LoginResponse> {
        const res = await fetch(`${this.BASE}/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password}),
        })
        if (!res.ok) {
            const text = await res.text()
            throw new Error(text || 'Login failed')
        }
        return res.json()
    }

    static async refresh(dto: RefreshTokenRequestDto): Promise<LoginResponse> {
        const res = await fetch(`${this.BASE}/refresh`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(dto),
        })
        if (!res.ok) {
            const text = await res.text()
            throw new Error(text || 'Refresh failed')
        }
        return res.json()
    }

    static async register(dto: UserCreationRequestDto): Promise<UserDto> {
        const formData = new FormData()
        formData.append('username', dto.username)
        formData.append('email', dto.email)
        formData.append('password', dto.password)
        formData.append('confirmPassword', dto.confirmPassword)
        formData.append('role', dto.role.toUpperCase())
        if (dto.avatar) {
            formData.append('avatar', dto.avatar, dto.avatar.name)
        }

        const res = await fetch(`${this.BASE}/register`, {
            method: 'POST',
            body: formData,
        })
        if (!res.ok) {
            const text = await res.text()
            throw new Error(text || 'Registration failed')
        }
        return res.json()
    }

    static async me(): Promise<UserDto> {
        const res = await fetch(`${this.BASE}/me`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error('Failed to fetch profile')
        }
        return res.json()
    }
}