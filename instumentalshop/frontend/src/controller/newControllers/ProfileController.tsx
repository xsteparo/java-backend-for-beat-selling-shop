import { UpdateProfileDto } from "../../dto/newDto/profile/UpdateProfileDto.tsx";
import {UserProfileDto} from "../../dto/newDto/profile/UserProfileDto.tsx";
import {UserDto} from "../../dto/newDto/auth/UserDto.tsx";

export class ProfileController {
    private static readonly BASE = '/api/v1/me'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async getProfile(): Promise<UserProfileDto> {
        const res = await fetch(this.BASE, {
            headers: this.getAuthHeader()
        })
        if (!res.ok) {
            throw new Error(`Fetch profile failed: ${res.status}`)
        }
        return res.json()
    }

    static async updateProfile(dto: UpdateProfileDto): Promise<UserProfileDto> {
        const res = await fetch(this.BASE, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeader()
            },
            body: JSON.stringify(dto)
        })
        if (!res.ok) {
            throw new Error(`Update profile failed: ${res.status}`)
        }
        return res.json()
    }

    static async updateAvatar(file: File): Promise<void> {
        const form = new FormData()
        form.append('file', file)
        const res = await fetch(`${this.BASE}/avatar`, {
            method: 'PUT',
            headers: this.getAuthHeader(),
            body: form
        })
        if (!res.ok) {
            throw new Error(`Update avatar failed: ${res.status}`)
        }
    }

    static async depositBalance(amount: number): Promise<UserDto> {
        const res = await fetch(`${this.BASE}/deposit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeader(),
            },
            body: JSON.stringify({ amount }),
        });

        if (!res.ok) {
            throw new Error(`Deposit failed: ${res.status}`);
        }

        return res.json();
    }
}