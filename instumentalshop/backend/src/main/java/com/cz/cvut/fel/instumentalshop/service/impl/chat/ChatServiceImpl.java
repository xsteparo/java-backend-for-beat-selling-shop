package com.cz.cvut.fel.instumentalshop.service.impl.chat;


import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatMessageRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatRoomRepository;
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

    public List<ChatRoom> getUserRooms(Long userId) {
        return roomRepo.findAllByParticipant(userId);
    }

    public ChatRoom openRoom(Long userId1, Long userId2) {
        // find existing room or create new one
        List<ChatRoom> rooms = roomRepo.findAllByParticipant(userId1).stream()
                .filter(r -> r.getParticipants().stream().anyMatch(u -> u.getId().equals(userId2)))
                .collect(Collectors.toList());
        if (!rooms.isEmpty()) return rooms.get(0);

        ChatRoom room = new ChatRoom();
        room.getParticipants().add(userRepo.getOne(userId1));
        room.getParticipants().add(userRepo.getOne(userId2));
        return roomRepo.save(room);
    }

    public List<ChatMessage> getMessages(Long roomId) {
        return msgRepo.findByChatRoomIdOrderBySentAtAsc(roomId);
    }

    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setSender(sender);
        msg.setContent(content);
        msg = msgRepo.save(msg);

        // update last message in room
        room.setLastMessage(msg);
        roomRepo.save(room);
        return msg;
    }
}
