package com.cz.cvut.fel.instumentalshop.controller.chat;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatMessageDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final SimpMessagingTemplate template;
    private final ChatServiceImpl chatService;
    private final UserRepository userRepository;

    // client sends message to server via websocket
    @MessageMapping("/chat/{roomId}/send")
    public void send(
            @DestinationVariable Long roomId,
            @Payload ChatMessageDto payload,
            Principal principal  // use java.security.Principal
    ) {
        // Controller is now invoked, because Spring can always inject Principal
        String username = principal.getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ChatMessage msg = chatService.saveMessage(roomId, sender.getId(), payload.getContent());
        template.convertAndSend("/topic/chat/" + roomId, ChatMessageDto.fromEntity(msg));
    }
}
