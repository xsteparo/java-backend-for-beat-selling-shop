export interface ChatMessageDto {
    id: number
    roomId: number
    senderId: number
    senderUsername: string
    content: string
    sentAt: string
}