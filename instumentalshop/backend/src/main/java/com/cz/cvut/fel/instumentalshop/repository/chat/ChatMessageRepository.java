package com.cz.cvut.fel.instumentalshop.repository.chat;

import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long roomId);
}
