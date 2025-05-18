import { ChatMessageDto } from "./ChatMessageDto"
import {ParticipantDto} from "./ParticipantDto.tsx";

export interface ChatRoomDto {
    id: number
    participants: ParticipantDto[]
    lastMessage?: ChatMessageDto
}