import {UserCreationRequestDto} from "../dto/UserCreationRequestDto.ts";
import {UserDto} from "../dto/UserDto.ts";
import {IncreaseBalanceRequestDto} from "../dto/IncreaseBalanceRequestDto.ts";
import {BalanceResponseDto} from "../dto/BalanceResponseDto.ts";
import {UserUpdateRequestDto} from "../dto/UserUpdateRequestDto.ts";

export class CustomerController {
    private static readonly BASE = '/api/v1/customers';

    static async register(data: UserCreationRequestDto): Promise<UserDto> {
        const fd = new FormData()
        fd.append('username', data.username)
        fd.append('email', data.email)
        fd.append('password', data.password)
        fd.append('role', (data.role as any).toUpperCase());
        if (data.avatar) fd.append('avatar', data.avatar)

        const res = await fetch('/api/v1/customers/register', {
            method: 'POST',
            body: fd,
        })
        if (!res.ok) {
            const text = await res.text()
            throw new Error(text || 'Registration failed')
        }
        return res.json()
    }

    static async increaseBalance(
        dto: IncreaseBalanceRequestDto
    ): Promise<BalanceResponseDto> {
        const res = await fetch(`${this.BASE}/increase-balance`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto),
        });
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Increase balance failed');
        }
        return res.json();
    }

    static async getBalance(): Promise<BalanceResponseDto> {
        const res = await fetch(`${this.BASE}/balance`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Fetch balance failed');
        }
        return res.json();
    }

    static async getCustomerById(id: number): Promise<UserDto> {
        const res = await fetch(`${this.BASE}/${id}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Fetch user failed');
        }
        return res.json();
    }

    static async updateCustomer(
        dto: UserUpdateRequestDto
    ): Promise<UserDto> {
        const res = await fetch(`${this.BASE}/update`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto),
        });
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Update user failed');
        }
        return res.json();
    }

    static async deleteCustomer(): Promise<void> {
        const res = await fetch(`${this.BASE}/delete`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
        });
        if (!res.ok) {
            const err = await res.text();
            throw new Error(err || 'Delete user failed');
        }
    }
}