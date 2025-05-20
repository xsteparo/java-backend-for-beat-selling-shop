import { PurchaseDto } from "../../dto/newDto/purchase/PurchaseDto";
import {UserDto} from "../../dto/newDto/auth/UserDto.tsx";
import {UpdateUserRoleRequest} from "../../dto/newDto/user/UpdateUserRoleRequest.tsx";

const BASE = "/api/v1/admin";

function getAuthHeader(): Record<string, string> {
    const token = localStorage.getItem("beatshop_jwt");
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export class AdminController {
    /** Получить все покупки */
    static async getAllPurchases(): Promise<PurchaseDto[]> {
        const res = await fetch(`${BASE}/purchases`, {
            headers: getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Failed to fetch purchases: ${res.status}`);
        return res.json();
    }

    // /** Обновить покупку */
    // static async updatePurchase(purchaseId: number, dto: PurchaseUpdateRequestDto): Promise<PurchaseDto> {
    //     const res = await fetch(`${BASE}/purchases/${purchaseId}`, {
    //         method: "PUT",
    //         headers: {
    //             "Content-Type": "application/json",
    //             ...getAuthHeader(),
    //         },
    //         body: JSON.stringify(dto),
    //     });
    //     if (!res.ok) throw new Error(`Failed to update purchase: ${res.status}`);
    //     return res.json();
    // }

    /** Удалить трек */
    static async deleteTrack(trackId: number): Promise<void> {
        const res = await fetch(`${BASE}/tracks/${trackId}`, {
            method: "DELETE",
            headers: getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Failed to delete track: ${res.status}`);
    }

    /** Получить всех пользователей */
    static async getAllUsers(): Promise<UserDto[]> {
        const res = await fetch(`${BASE}/users`, {
            headers: getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Failed to fetch users: ${res.status}`);
        return res.json();
    }

    /** Удалить пользователя */
    static async deleteUser(userId: number): Promise<void> {
        const res = await fetch(`${BASE}/users/${userId}`, {
            method: "DELETE",
            headers: getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Failed to delete user: ${res.status}`);
    }

    static async deletePurchase(purchaseId: number): Promise<void> {
        const res = await fetch(`/api/v1/admin/purchases/${purchaseId}`, {
            method: "DELETE",
            headers: getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Failed to delete purchase: ${res.status}`);
    }

    static async updateUserRole(userId: number, role: UpdateUserRoleRequest['role']): Promise<UserDto> {
        const res = await fetch(`/api/v1/admin/users/${userId}/role`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                ...getAuthHeader(),
            },
            body: JSON.stringify({ role }),
        });
        if (!res.ok) throw new Error(`Failed to update user role: ${res.status}`);
        return res.json();
    }
}
