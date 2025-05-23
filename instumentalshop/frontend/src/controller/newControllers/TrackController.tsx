import { TrackFilter } from "../../dto/newDto/tracks/TrackFilter.tsx";
import {TrackDto} from "../../dto/newDto/tracks/TrackDto.tsx";
import {TrackRequestDto} from "../../dto/newDto/tracks/TrackRequestDto.tsx";


export class TrackController {
    private static readonly BASE = '/api/v1/tracks'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async createTrack(dto: TrackRequestDto): Promise<TrackDto> {
        const form = new FormData()
        form.append('name', dto.name)
        form.append(
            'genreType',
            dto.genreType.toUpperCase().replace(/-/g, '_')
        )
        form.append('bpm', String(dto.bpm))
        if (dto.key) {
            form.append('key', dto.key)
        }
        if (dto.price != null) {
            form.append('price', String(dto.price))
        }
        form.append('nonExclusiveFile', dto.nonExclusiveFile)
        if (dto.premiumFile) {
            form.append('premiumFile', dto.premiumFile)
        }
        if (dto.exclusiveFile) {
            form.append('exclusiveFile', dto.exclusiveFile)
        }

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

    /** Universal list: tabs, search, filters, pagination */
    static async listTracks(
        filter: TrackFilter,
        page: number,
        size: number
    ): Promise<{ content: TrackDto[]; totalPages: number }> {
        const params = new URLSearchParams();
        Object.entries(filter).forEach(([k, v]) => {
            if (v) params.set(k, v);
        });
        params.set("page", String(page - 1));
        params.set("size", String(size));

        const res = await fetch(`${this.BASE}?${params.toString()}`, {
            headers: this.getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Fetch tracks failed: ${res.status}`);
        return res.json();
    }

    /** Get single track by ID */
    static async getTrack(id: number): Promise<TrackDto> {
        const res = await fetch(`${this.BASE}/${id}`, {
            headers: this.getAuthHeader()
        })
        if (!res.ok) {
            throw new Error(`Fetch track failed: ${res.status}`)
        }
        return res.json()
    }

    /** Stream track (supports HTTP Range) */
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
        return { data, contentType, headers: res.headers }
    }

    static async getMyTracks(): Promise<TrackDto[]> {
        const res = await fetch(`/api/v1/producers/me/tracks`, {
            headers: this.getAuthHeader(),
        });
        if (!res.ok) throw new Error(`Failed to fetch producer tracks: ${res.status}`);
        return res.json();
    }

    static async updateTrack(id: number, dto: TrackRequestDto): Promise<TrackDto> {
        const form = new FormData();
        form.append('name', dto.name);
        form.append('genreType', dto.genreType.toUpperCase().replace(/-/g, '_'));
        form.append('bpm', String(dto.bpm));
        if (dto.key) form.append('key', dto.key);
        if (dto.price != null) form.append('price', String(dto.price));
        if (dto.nonExclusiveFile) form.append('nonExclusiveFile', dto.nonExclusiveFile);
        if (dto.premiumFile) form.append('premiumFile', dto.premiumFile);
        if (dto.exclusiveFile) form.append('exclusiveFile', dto.exclusiveFile);

        const res = await fetch(`${this.BASE}/${id}`, {
            method: 'PUT',
            headers: this.getAuthHeader(), // FormData сам выставляет content-type
            body: form,
        });
        if (!res.ok) throw new Error(`Update track failed: ${res.status}`);
        return res.json();
    }
}