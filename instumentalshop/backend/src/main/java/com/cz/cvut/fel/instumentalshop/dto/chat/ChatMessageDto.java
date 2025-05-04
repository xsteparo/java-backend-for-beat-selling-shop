package com.cz.cvut.fel.instumentalshop.dto.chat;


import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;

    /**
     * Преобразует сущность ChatMessage в DTO
     */
    public static ChatMessageDto fromEntity(ChatMessage msg) {
        return ChatMessageDto.builder()
                .id(msg.getId())
                .roomId(msg.getChatRoom().getId())
                .senderId(msg.getSender().getId())
                .senderUsername(msg.getSender().getUsername())
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .build();
    }
}
