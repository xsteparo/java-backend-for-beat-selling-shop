package com.cz.cvut.fel.instumentalshop.controller.chat;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatMessageDto;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatRoomDto;
import com.cz.cvut.fel.instumentalshop.service.chat.ChatService;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API kontroler pro správu chatovacích místností a zpráv.
 */
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    /**
     * FR19: Zobrazí seznam všech chatovacích místností,
     * kde je přihlášený uživatel účastníkem.
     *
     * @param userId ID přihlášeného uživatele (vkládá Spring Security)
     * @return seznam DTO místností
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatRoomDto>> listRooms(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        List<ChatRoomDto> rooms = chatService.getUserRooms(userId).stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rooms);
    }

    /**
     * FR19: Otevře (nebo vrátí existující) chatovací místnost mezi dvěma uživateli.
     *
     * @param userId      ID přihlášeného uživatele
     * @param otherUserId ID druhého uživatele
     * @return DTO otevřené nebo existující místnosti
     */
    @PostMapping("/open/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> openRoom(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long otherUserId
    ) {
        var room = chatService.openRoom(userId, otherUserId);
        return ResponseEntity.ok(ChatRoomDto.fromEntity(room));
    }

    /**
     * FR19: Vrátí historii zpráv v dané místnosti (seřazeno podle času odeslání).
     *
     * @param userId ID přihlášeného uživatele
     * @param roomId ID chatovací místnosti
     * @return seznam zpráv jako DTO
     */
    @GetMapping("/{roomId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDto>> getMessages(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long roomId
    ) {
        var messages = chatService.getMessages(roomId).stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    /**
     * FR19: Odešle novou zprávu do místnosti REST voláním (alternativa k WebSocketu).
     *
     * @param userId  ID přihlášeného uživatele
     * @param roomId  ID chatovací místnosti
     * @param payload tělo požadavku obsahující text zprávy
     * @return DTO nově uložené zprávy
     */
    @PostMapping("/{roomId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDto> postMessage(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long roomId,
            @RequestBody ChatMessageDto payload
    ) {
        var msg = chatService.saveMessage(roomId, userId, payload.getContent());
        return ResponseEntity.ok(ChatMessageDto.fromEntity(msg));
    }

}
