package com.cz.cvut.fel.instumentalshop.service.newTests;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatMessageRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatRoomRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private ChatRoomRepository roomRepo;
    @Mock
    private ChatMessageRepository msgRepo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ChatServiceImpl service;

    private ChatRoom room1;
    private ChatMessage msg1;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // připravení společných entit pro testy
        room1 = new ChatRoom();
        room1.setId(100L);
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");
        user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");
        msg1 = new ChatMessage();
        msg1.setId(200L);
        msg1.setContent("Ahoj");
        msg1.setChatRoom(room1);
        msg1.setSender(user1);
    }

    @Test
    void testGetUserRooms() {
        // pokud repozitář vrátí seznam místností, služba je vrátí beze změny
        when(roomRepo.findAllByParticipant(1L)).thenReturn(List.of(room1));

        var rooms = service.getUserRooms(1L);

        assertEquals(1, rooms.size());
        assertSame(room1, rooms.get(0));
        verify(roomRepo).findAllByParticipant(1L);
    }

    @Test
    void testOpenRoom_Existing() {
        // existující místnost mezi user1 a user2
        room1.getParticipants().add(user1);
        room1.getParticipants().add(user2);
        when(roomRepo.findAllByParticipant(1L)).thenReturn(List.of(room1));

        var room = service.openRoom(1L, 2L);

        assertSame(room1, room);
        verify(roomRepo, never()).save(any());
    }

    @Test
    void testOpenRoom_New() {
        // žádná existující místnost, musí se vytvořit nová
        when(roomRepo.findAllByParticipant(1L)).thenReturn(Collections.emptyList());
        when(userRepo.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user2));
        // simulace uložení nové místnosti
        ArgumentCaptor<ChatRoom> captor = ArgumentCaptor.forClass(ChatRoom.class);
        when(roomRepo.save(captor.capture())).thenAnswer(inv -> {
            ChatRoom saved = inv.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        var room = service.openRoom(1L, 2L);

        // ověření, že nová místnost má oba účastníky a přiřazené ID
        assertEquals(Long.valueOf(101L), room.getId());
        assertTrue(room.getParticipants().containsAll(List.of(user1, user2)));
        verify(roomRepo).save(any());
    }

    @Test
    void testGetMessages() {
        // zprávy z místnosti by měly přicházet seřazené
        when(msgRepo.findByChatRoomIdOrderBySentAtAsc(100L)).thenReturn(List.of(msg1));

        var msgs = service.getMessages(100L);

        assertEquals(1, msgs.size());
        assertSame(msg1, msgs.get(0));
        verify(msgRepo).findByChatRoomIdOrderBySentAtAsc(100L);
    }

    @Test
    void testSaveMessage_Success() {
        // ukládání nové zprávy a aktualizace poslední zprávy v místnosti
        when(roomRepo.findById(100L)).thenReturn(Optional.of(room1));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user1));
        // simulace uložení zprávy
        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(inv -> {
            ChatMessage m = inv.getArgument(0);
            m.setId(300L);
            return m;
        });
        when(roomRepo.save(any(ChatRoom.class))).thenAnswer(inv -> inv.getArgument(0));

        var saved = service.saveMessage(100L, 1L, "Zpráva");

        assertEquals(Long.valueOf(300L), saved.getId());
        assertEquals("Zpráva", saved.getContent());
        assertSame(room1, saved.getChatRoom());
        assertSame(user1, saved.getSender());
        // poslední zpráva v místnosti byla nastavena
        assertSame(saved, room1.getLastMessage());
        verify(roomRepo).save(room1);
    }

    @Test
    void testSaveMessage_RoomNotFound() {
        // neexistující místnost -> EntityNotFoundException
        when(roomRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                service.saveMessage(999L, 1L, "x"));
    }

    @Test
    void testSaveMessage_UserNotFound() {
        // neexistující uživatel -> EntityNotFoundException
        when(roomRepo.findById(100L)).thenReturn(Optional.of(room1));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                service.saveMessage(100L, 1L, "x"));
    }
}





