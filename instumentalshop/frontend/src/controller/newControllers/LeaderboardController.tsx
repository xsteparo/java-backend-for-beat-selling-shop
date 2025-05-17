import { TopProducerDto } from "../../dto/newDto/leaderboard/TopProducerDto"
import { TrackDto } from "../../dto/newDto/tracks/TrackDto"

export class LeaderboardController {
    private static readonly BASE = '/api/v1/leaderboard'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async getTopTracks(limit?: number): Promise<TrackDto[]> {
        const q = new URLSearchParams({
            limit: String(limit ?? 10),
        })
        const res = await fetch(`${this.BASE}/tracks?${q}`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Fetch top tracks failed: ${res.status}`)
        }
        return res.json()
    }

    static async getTopProducers(limit?: number): Promise<TopProducerDto[]> {
        const q = new URLSearchParams({
            limit: String(limit ?? 10),
        })
        const res = await fetch(`${this.BASE}/producers?${q}`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Fetch top producers failed: ${res.status}`)
        }
        return res.json()
    }
}