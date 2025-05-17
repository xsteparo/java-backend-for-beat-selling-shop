package com.cz.cvut.fel.instumentalshop.service.impl.chat;


import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatMessageRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatRoomRepository;
import com.cz.cvut.fel.instumentalshop.service.chat.ChatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    /**
     * Vrátí všechny chatovací místnosti, ve kterých se účastní daný uživatel.
     *
     * @param userId ID uživatele
     * @return seznam místností
     */
    public List<ChatRoom> getUserRooms(Long userId) {
        return roomRepo.findAllByParticipant(userId);
    }

    /**
     * Otevře (nebo najde) místnost mezi dvěma uživateli.
     * Pokud již existuje, vrátí ji; jinak vytvoří novou.
     *
     * @param userId1 ID prvního uživatele
     * @param userId2 ID druhého uživatele
     * @return existující či nově vytvořená místnost
     */
    public ChatRoom openRoom(Long userId1, Long userId2) {
        // pokud existuje, vrať první
        List<ChatRoom> existing = roomRepo.findAllByParticipant(userId1).stream()
                .filter(r -> r.getParticipants().stream()
                        .anyMatch(u -> u.getId().equals(userId2)))
                .toList();
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        // jinak načti oba uživatele z repozitáře nebo vyhoď výjimku
        User u1 = userRepo.findById(userId1)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId1));
        User u2 = userRepo.findById(userId2)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId2));

        ChatRoom room = new ChatRoom();
        room.getParticipants().add(u1);
        room.getParticipants().add(u2);
        return roomRepo.save(room);
    }

    /**
     * Načte historii zpráv v místnosti seřazenou vzestupně podle času.
     *
     * @param roomId ID místnosti
     * @return seznam zpráv
     */
    public List<ChatMessage> getMessages(Long roomId) {
        return msgRepo.findByChatRoomIdOrderBySentAtAsc(roomId);
    }

    /**
     * Uloží novou zprávu do místnosti a aktualizuje poslední zprávu v entitě místnosti.
     *
     * @param roomId   ID místnosti
     * @param senderId ID odesílatele
     * @param content  obsah zprávy
     * @return uložená zpráva
     */
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + senderId));

        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setSender(sender);
        msg.setContent(content);
        msg = msgRepo.save(msg);

        // aktualizuj poslední zprávu
        room.setLastMessage(msg);
        roomRepo.save(room);
        return msg;
    }
}
