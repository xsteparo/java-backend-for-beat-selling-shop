import { TrackFilter } from "../../dto/newDto/tracks/TrackFilter.tsx";
import {TrackDto} from "../../dto/newDto/tracks/TrackDto.tsx";
import {TrackRequestDto} from "../../dto/newDto/tracks/TrackRequestDto.tsx";

export class TrackController {
    private static readonly BASE = '/api/v1/tracks'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? {Authorization: `Bearer ${token}`} : {}
    }

    static async createTrack(dto: TrackRequestDto): Promise<TrackDto> {
        const form = new FormData()
        form.append('name', dto.name)
        form.append(
            'genreType',
            dto.genreType.toUpperCase().replace(/-/g, '_')
        )
        form.append('bpm', String(dto.bpm))
        if (dto.key) form.append('key', dto.key)
        if (dto.price != null) form.append('price', String(dto.price))
        form.append('nonExclusiveFile', dto.nonExclusiveFile)
        if (dto.premiumFile) form.append('premiumFile', dto.premiumFile)
        if (dto.exclusiveFile) form.append('exclusiveFile', dto.exclusiveFile)

        const res = await fetch(`${this.BASE}/create`, {
            method: 'POST',
            headers: this.getAuthHeader(),
            body: form
        })
        if (!res.ok) {
            throw new Error(`Create track failed: ${res.status}`)
        }
        return res.json()
    }

    static async listTracks(
        filter: TrackFilter,
        page: number,
        size: number
    ): Promise<{ content: TrackDto[]; totalPages: number }> {
        const params: Record<string, string> = {}
        if (filter.tab) params.tab = filter.tab
        if (filter.search) params.search = filter.search
        if (filter.genre) params.genre = filter.genre
        if (filter.tempoRange) params.tempoRange = filter.tempoRange
        if (filter.key) params.key = filter.key
        if (filter.sort) params.sort = filter.sort
        params.page = String(page - 1)
        params.size = String(size)

        const query = new URLSearchParams(params)
        const res = await fetch(`${this.BASE}?${query}`, {
            headers: this.getAuthHeader()
        })
        if (!res.ok) {
            throw new Error(`Fetch tracks failed: ${res.status}`)
        }
        return res.json()
    }

    static async getTrack(id: number): Promise<TrackDto> {
        const res = await fetch(`${this.BASE}/${id}`, {
            headers: this.getAuthHeader()
        })
        if (!res.ok) {
            throw new Error(`Fetch track failed: ${res.status}`)
        }
        return res.json()
    }

    static async streamTrack(
        id: number,
        range?: string
    ): Promise<{ data: Blob; contentType: string; headers: Headers }> {
        const headers: Record<string, string> = this.getAuthHeader()
        if (range) headers['Range'] = range
        const res = await fetch(`${this.BASE}/${id}/stream`, {
            method: 'GET',
            headers
        })
        if (!(res.ok || res.status === 206)) {
            throw new Error(`Stream track failed: ${res.status}`)
        }
        const data = await res.blob()
        const contentType = res.headers.get('Content-Type') || 'application/octet-stream'
        return {data, contentType, headers: res.headers}
    }
}