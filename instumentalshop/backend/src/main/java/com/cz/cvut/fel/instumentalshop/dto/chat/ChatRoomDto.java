package com.cz.cvut.fel.instumentalshop.dto.chat;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private Long id;
    private List<ParticipantDto> participants;
    private ChatMessageDto lastMessage;

    /**
     * Преобразует сущность ChatRoom в DTO
     */
    public static ChatRoomDto fromEntity(ChatRoom room) {
        return ChatRoomDto.builder()
                .id(room.getId())
                .participants(
                        room.getParticipants().stream()
                                .map(ParticipantDto::fromEntity)
                                .collect(Collectors.toList())
                )
                .lastMessage(
                        room.getLastMessage() != null
                                ? ChatMessageDto.fromEntity(room.getLastMessage())
                                : null
                )
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParticipantDto {
        private Long id;
        private String username;

        public static ParticipantDto fromEntity(User user) {
            return ParticipantDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .build();
        }
    }
}
