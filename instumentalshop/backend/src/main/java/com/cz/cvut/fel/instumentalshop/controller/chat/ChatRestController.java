package com.cz.cvut.fel.instumentalshop.controller.chat;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatMessageDto;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatRoomDto;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatServiceImpl chatService;

    @GetMapping
    public List<ChatRoomDto> listRooms(@AuthenticationPrincipal User user) {
        return chatService.getUserRooms(user.getId()).stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /api/chats/{roomId}/messages — history of message in the chat room
    @GetMapping("/{roomId}/messages")
    public List<ChatMessageDto> getMessages(
            @AuthenticationPrincipal User user,
            @PathVariable Long roomId) {
        return chatService.getMessages(roomId).stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }
}
