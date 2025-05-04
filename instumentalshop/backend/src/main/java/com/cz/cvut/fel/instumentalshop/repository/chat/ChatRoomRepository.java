package com.cz.cvut.fel.instumentalshop.repository.chat;

import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select r from ChatRoom r join r.participants u where u.id = :userId")
    List<ChatRoom> findAllByParticipant(@Param("userId") Long userId);
}
