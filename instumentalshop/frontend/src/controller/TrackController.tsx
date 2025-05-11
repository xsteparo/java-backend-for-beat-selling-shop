import {TrackRequestDto} from "../dto/TrackRequestDto.ts";
import {TrackDto} from "../dto/TrackDto.ts";
import {ProducerTrackInfoDto} from "../dto/ProducerTrackInfoDto.ts";

export class TrackController {
    private static BASE = '/api/v1/tracks'

    /**
     * Всегда возвращает объект string→string,
     * даже если токена нет — просто пустой.
     */
    // private static getAuthHeader(): Record<string, string> {
    //     const token = localStorage.getItem('beatshop_jwt')
    //     if (!token) {
    //         return {}
    //     }
    //     return {Authorization: `Bearer ${token}`}
    // }

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt');
        return token ? { Authorization: `Bearer ${token}` } : {};
    }

    /** Универсальный list: таб, поиск, фильтры, пагинация */
    static async listTracks(params: {
        tab: 'top' | 'trending' | 'new';
        search: string;
        genre: string;
        tempoRange: string;
        key: string;
        sort: string;
        page: number;  // фронт 1-based
        size: number;
    }): Promise<{ content: TrackDto[]; totalPages: number }> {
        const { tab, search, genre, tempoRange, key, sort, page, size } = params;
        const query = new URLSearchParams({
            tab,
            search,
            genre,
            tempoRange,
            key,
            sort,
            page: String(page - 1),  // преобразуем в 0-based
            size: String(size),
        });
        const res = await fetch(`${this.BASE}?${query}`, {
            headers: this.getAuthHeader(),
        });
        if (!res.ok) {
            throw new Error(`Fetch tracks failed: ${res.status}`);
        }
        return res.json();
    }


    /** Producer only: create (multipart/form-data) */
    static async createTrack(
        dto: TrackRequestDto
    ): Promise<TrackDto> {
        const form = new FormData()
        form.append('name', dto.name)
        form.append('genreType',
            dto.genreType
                .toUpperCase()
                .replace(/-/g, '_')
        ),
            form.append('bpm', String(dto.bpm))
        if (dto.mainProducerPercentage != null) {
            form.append(
                'mainProducerPercentage',
                dto.mainProducerPercentage.toString()
            )
        }
        dto.producerShares?.forEach((ps, i) => {
            form.append(`producerShares[${i}].producerName`, ps.producerName)
            form.append(
                `producerShares[${i}].profitPercentage`,
                ps.profitPercentage.toString()
            )
        })
        form.append('nonExclusiveFile', dto.nonExclusiveFile)
        if (dto.premiumFile) form.append('premiumFile', dto.premiumFile)
        if (dto.exclusiveFile) form.append('exclusiveFile', dto.exclusiveFile)

        const res = await fetch(`${this.BASE}/create`, {
            method: 'POST',
            headers: {
                ...this.getAuthHeader(),
            },
            body: form
        })
        if (!res.ok) {
            throw new Error(`Create track failed: ${res.status}`)
        }
        return res.json()
    }

    /** Producer only: confirm agreement */
    static async confirmProducerAgreement(
        trackId: number
    ): Promise<ProducerTrackInfoDto[]> {
        const res = await fetch(
            `${this.BASE}/${trackId}/confirm-agreement`,
            {
                method: 'PATCH',
                headers: this.getAuthHeader()
            }
        )
        if (!res.ok) {
            throw new Error(`Confirm agreement failed: ${res.status}`)
        }
        return res.json()
    }

    /** Producer only: list approvals */
    static async getAllTrackApprovals(): Promise<ProducerTrackInfoDto[]> {
        const res = await fetch(
            `${this.BASE}/track-approvals-list`,
            {headers: this.getAuthHeader()}
        )
        if (!res.ok) {
            throw new Error(`Load approvals failed: ${res.status}`)
        }
        return res.json()
    }

    /** Public: get one track */
    static async getTrackById(trackId: number): Promise<TrackDto> {
        const res = await fetch(`${this.BASE}/${trackId}`)
        if (!res.ok) {
            throw new Error(`Fetch track failed: ${res.status}`)
        }
        return res.json()
    }

    /** Public: list all tracks */
    static async getAllTracks(): Promise<TrackDto[]> {
        const res = await fetch(this.BASE)
        if (!res.ok) {
            throw new Error(`Fetch tracks failed: ${res.status}`)
        }
        return res.json()
    }

    /** Public: by producer */
    static async getAllTracksByProducer(
        producerId: number
    ): Promise<TrackDto[]> {
        const res = await fetch(
            `${this.BASE}/by-producer/${producerId}`
        )
        if (!res.ok) {
            throw new Error(
                `Fetch producer tracks failed: ${res.status}`
            )
        }
        return res.json()
    }

    /** Producer only: customer bought tracks */
    static async getCustomerBoughtTracksForProducer(
        customerId: number
    ): Promise<TrackDto[]> {
        const res = await fetch(
            `${this.BASE}/bought-by-customer/${customerId}`,
            {headers: this.getAuthHeader()}
        )
        if (!res.ok) {
            throw new Error(
                `Fetch customer-bought tracks failed: ${res.status}`
            )
        }
        return res.json()
    }

    /** Producer only: delete */
    static async deleteTrack(trackId: number): Promise<void> {
        const res = await fetch(`${this.BASE}/${trackId}`, {
            method: 'DELETE',
            headers: this.getAuthHeader()
        })
        if (!res.ok) {
            throw new Error(`Delete track failed: ${res.status}`)
        }
    }

    /** Producer only: update metadata (JSON) */
    static async updateTrack(
        trackId: number,
        dto: TrackRequestDto
    ): Promise<TrackDto> {
        const res = await fetch(`${this.BASE}/${trackId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeader()
            },
            body: JSON.stringify({
                name: dto.name,
                genreType: dto.genreType,
                bpm: dto.bpm,
                mainProducerPercentage: dto.mainProducerPercentage,
                producerShares: dto.producerShares
            })
        })
        if (!res.ok) {
            throw new Error(`Update track failed: ${res.status}`)
        }
        return res.json()
    }
}