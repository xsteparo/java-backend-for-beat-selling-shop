import {ChatRoomDto} from "../../../dto/newDto/chat/ChatRoomDto.tsx";
import {ChatMessageDto} from "../../../dto/newDto/chat/ChatMessageDto.tsx";
import {SendMessageDto} from "../../../dto/newDto/chat/SendMessageDto.tsx";

export class ChatController {
    private static readonly BASE = '/api/v1/chats'

    private static getAuthHeader(): Record<string, string> {
        const token = localStorage.getItem('beatshop_jwt')
        return token ? { Authorization: `Bearer ${token}` } : {}
    }

    static async listRooms(): Promise<ChatRoomDto[]> {
        const res = await fetch(this.BASE, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Fetch chat rooms failed: ${res.status}`)
        }
        return res.json()
    }

    static async openRoom(otherUserId: number): Promise<ChatRoomDto> {
        const res = await fetch(`${this.BASE}/open/${otherUserId}`, {
            method: 'POST',
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Open chat room failed: ${res.status}`)
        }
        return res.json()
    }

    static async getMessages(roomId: number): Promise<ChatMessageDto[]> {
        const res = await fetch(`${this.BASE}/${roomId}/messages`, {
            headers: this.getAuthHeader(),
        })
        if (!res.ok) {
            throw new Error(`Fetch messages failed: ${res.status}`)
        }
        return res.json()
    }

    static async postMessage(
        roomId: number,
        payload: SendMessageDto
    ): Promise<ChatMessageDto> {
        const res = await fetch(`${this.BASE}/${roomId}/messages`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeader(),
            },
            body: JSON.stringify(payload),
        })
        if (!res.ok) {
            throw new Error(`Post message failed: ${res.status}`)
        }
        return res.json()
    }
}