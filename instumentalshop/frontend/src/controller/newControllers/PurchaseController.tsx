import {PurchaseRequestDto} from "../../dto/newDto/purchase/PurchaseRequestDto.tsx";
import {PurchaseDto} from "../../dto/newDto/purchase/PurchaseDto.tsx";

export class PurchaseController {
    private static readonly BASE = '/api/v1/purchases'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async purchase(
        trackId: number,
        dto: PurchaseRequestDto
    ): Promise<PurchaseDto> {
        const res = await fetch(`${this.BASE}/${trackId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeader(),
            },
            body: JSON.stringify(dto),
        })
        if (!res.ok) {
            throw new Error(`Purchase failed: ${res.status}`)
        }
        return res.json()
    }

    static async getMyPurchases(): Promise<PurchaseDto[]> {
        const res = await fetch(this.BASE, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Fetch purchases failed: ${res.status}`)
        }
        return res.json()
    }

    static async downloadLicense(
        purchaseId: number
    ): Promise<{ data: Blob; filename: string }> {
        const res = await fetch(`${this.BASE}/${purchaseId}/license`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Download license failed: ${res.status}`)
        }
        const data = await res.blob()
        const disp = res.headers.get('Content-Disposition') || ''
        const filename = disp.split('filename=')[1]?.replace(/"/g, '') || 'license.pdf'
        return { data, filename }
    }

    static async downloadTrack(
        purchaseId: number
    ): Promise<{ data: Blob; filename: string }> {
        const res = await fetch(`${this.BASE}/${purchaseId}/download`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Download track failed: ${res.status}`)
        }
        const data = await res.blob()
        const disp = res.headers.get('Content-Disposition') || ''
        const filename = disp.split('filename=')[1]?.replace(/"/g, '') || ''
        return { data, filename }
    }
}