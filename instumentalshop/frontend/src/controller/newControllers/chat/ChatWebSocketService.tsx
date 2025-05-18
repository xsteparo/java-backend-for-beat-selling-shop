import { Client, IMessage, StompSubscription } from '@stomp/stompjs'
import {ChatMessageDto} from "../../../dto/newDto/chat/ChatMessageDto.tsx";


export class ChatWebSocketService {
    private static readonly WS_ENDPOINT = 'ws://localhost:8080/ws'
    private client: Client
    private subscriptions = new Map<number, StompSubscription>()

    constructor() {
        this.client = new Client({
            brokerURL: ChatWebSocketService.WS_ENDPOINT,
            reconnectDelay: 5000,
        })
    }

    connect(): void {
        if (!this.client.active) {
            this.client.activate()
        }
    }

    disconnect(): void {
        if (this.client.active) {
            this.subscriptions.forEach(sub => sub.unsubscribe())
            this.subscriptions.clear()
            this.client.deactivate()
        }
    }

    subscribe(roomId: number, onMessage: (msg: ChatMessageDto) => void): void {
        this.connect()
        const sub = this.client.subscribe(`/topic/chat/${roomId}`, (frame: IMessage) => {
            const msg = JSON.parse(frame.body) as ChatMessageDto
            onMessage(msg)
        })
        this.subscriptions.set(roomId, sub)
    }

    unsubscribe(roomId: number): void {
        const sub = this.subscriptions.get(roomId)
        if (sub) {
            sub.unsubscribe()
            this.subscriptions.delete(roomId)
        }
    }

    send(roomId: number, content: string): void {
        this.connect()
        const payload = { content }
        this.client.publish({
            destination: `/app/chat/${roomId}/send`,
            body: JSON.stringify(payload),
        })
    }
}