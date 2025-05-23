package com.cz.cvut.fel.instumentalshop.controller.chat;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatMessageDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.chat.ChatService;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {
    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    /**
     * Příjem STOMP zpráv z klienta a rozeslání všem odběratelům daného topicu.
     *
     * @param roomId    ID místnosti (využito jako součást topicu)
     * @param payload   DTO přijímané zprávy
     * @param principal přihlášený uživatel (automatic injection)
     */
    @MessageMapping("/chat/{roomId}/send")
    public void handleSend(@DestinationVariable Long roomId, ChatMessageDto dto, Principal principal) {
        Long senderId = ((User) ((Authentication) principal).getPrincipal()).getId();
        ChatMessage saved = chatService.saveMessage(roomId, senderId, dto.getContent());

        ChatMessageDto out = ChatMessageDto.fromEntity(saved);

        template.convertAndSend("/topic/chat/" + roomId, out);
    }
}
