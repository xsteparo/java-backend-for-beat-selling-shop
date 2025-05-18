package com.cz.cvut.fel.instumentalshop.service.impl.chat;


import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatRoomDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatMessageRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatRoomRepository;
import com.cz.cvut.fel.instumentalshop.service.chat.ChatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getUserRooms(Long userId) {
        return roomRepo.findAllByParticipant(userId)
                .stream()
                .map(ChatRoomDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ChatRoomDto openRoom(Long userId1, Long userId2) {
        // существующая логика поиска/создания
        List<ChatRoom> existing = roomRepo.findAllByParticipant(userId1).stream()
                .filter(r -> r.getParticipants().stream()
                        .anyMatch(u -> u.getId().equals(userId2)))
                .toList();
        ChatRoom room;
        if (!existing.isEmpty()) {
            room = existing.get(0);
        } else {
            User u1 = userRepo.findById(userId1)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId1));
            User u2 = userRepo.findById(userId2)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId2));

            room = new ChatRoom();
            room.getParticipants().add(u1);
            room.getParticipants().add(u2);
            room = roomRepo.save(room);
        }

        // Маппим в DTO **пока сессия жива**
        return ChatRoomDto.fromEntity(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Long roomId) {
        return msgRepo.findByChatRoomIdOrderBySentAtAsc(roomId);
    }

    @Override
    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + roomId));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + senderId));

        // 1) создаём и сохраняем новое сообщение
        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setSender(sender);
        msg.setContent(content);
        msg = msgRepo.save(msg);

        // 2) Обязательно добавляем его в коллекцию сообщений комнаты
        room.getMessages().add(msg);

        // 3) Обновляем указатель lastMessage
        room.setLastMessage(msg);

        // 4) Сохраняем комнату — но теперь старые сообщения остаются в списке => не удаляются
        roomRepo.save(room);

        return msg;
    }
}