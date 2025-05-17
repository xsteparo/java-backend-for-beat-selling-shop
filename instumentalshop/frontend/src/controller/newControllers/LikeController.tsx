export class LikeController {
    private static readonly BASE = '/api/v1/tracks'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async like(trackId: number): Promise<void> {
        const res = await fetch(`${this.BASE}/${trackId}/like`, {
            method: 'POST',
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Like track failed: ${res.status}`)
        }
    }

    static async unlike(trackId: number): Promise<void> {
        const res = await fetch(`${this.BASE}/${trackId}/like`, {
            method: 'DELETE',
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Unlike track failed: ${res.status}`)
        }
    }
}