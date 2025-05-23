import { Client, IMessage, StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export class ChatWebSocketService {
    private static readonly WS_ENDPOINT = '/ws';
    private client: Client
    private subscriptions = new Map<number, StompSubscription>()

    constructor() {
        this.client = new Client({
            webSocketFactory: () => new SockJS(ChatWebSocketService.WS_ENDPOINT),

            reconnectDelay: 5000,
            connectHeaders: (() => {
                const token = localStorage.getItem('beatshop_jwt');
                const headers: Record<string, string> = {};
                if (token) {
                    headers.Authorization = `Bearer ${token}`;
                }
                return headers;
            })(),
            onConnect: frame => console.log('STOMP connected', frame),
            onStompError: frame => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            }
        });
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

    subscribe(roomId: number, onMessage: (msg: any) => void): void {
        if (!this.client.active) {
            throw new Error('There is no underlying STOMP connection')
        }
        const sub = this.client.subscribe(`/topic/chat/${roomId}`, (frame: IMessage) => {
            const msg = JSON.parse(frame.body)
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
        if (!this.client.active) {
            this.connect()
        }
        this.client.publish({
            destination: `/app/chat/${roomId}/send`,
            body: JSON.stringify({ content }),
        })
    }
}