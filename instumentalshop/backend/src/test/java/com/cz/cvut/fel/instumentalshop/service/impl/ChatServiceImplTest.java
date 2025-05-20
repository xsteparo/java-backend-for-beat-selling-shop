package com.cz.cvut.fel.instumentalshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatMessageDto;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatRoomDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatMessageRepository;
import com.cz.cvut.fel.instumentalshop.repository.chat.ChatRoomRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ChatServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ChatServiceImplTest {
    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatServiceImpl chatServiceImpl;

    @MockBean
    private UserRepository userRepository;

    /**
     * Test {@link ChatServiceImpl#getUserRooms(Long)}.
     * <p>
     * Method under test: {@link ChatServiceImpl#getUserRooms(Long)}
     */
    @Test
    @DisplayName("Test getUserRooms(Long)")
    void testGetUserRooms() {
        // Arrange
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> chatServiceImpl.getUserRooms(1L));
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
    }

    /**
     * Test {@link ChatServiceImpl#getUserRooms(Long)}.
     * <p>
     * Method under test: {@link ChatServiceImpl#getUserRooms(Long)}
     */
    @Test
    @DisplayName("Test getUserRooms(Long)")
    void testGetUserRooms2() {
        // Arrange
        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(new ChatRoom());
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(new User());
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(lastMessage);
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBalance(new BigDecimal("2.3"));
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatMessage lastMessage3 = new ChatMessage();
        lastMessage3.setChatRoom(new ChatRoom());
        lastMessage3.setContent("Not all who wander are lost");
        lastMessage3.setId(1L);
        lastMessage3.setSender(new User());
        lastMessage3.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage3);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("https://example.org/example");
        sender2.setBalance(new BigDecimal("2.3"));
        sender2.setBio("Bio");
        sender2.setEmail("jane.doe@example.org");
        sender2.setId(1L);
        sender2.setPassword("iloveyou");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.PRODUCER);
        sender2.setUsername("janedoe");

        ChatMessage lastMessage4 = new ChatMessage();
        lastMessage4.setChatRoom(chatRoom2);
        lastMessage4.setContent("Not all who wander are lost");
        lastMessage4.setId(1L);
        lastMessage4.setSender(sender2);
        lastMessage4.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(1L);
        chatRoom3.setLastMessage(lastMessage4);
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());

        User sender3 = new User();
        sender3.setAvatarUrl("https://example.org/example");
        sender3.setBalance(new BigDecimal("2.3"));
        sender3.setBio("Bio");
        sender3.setEmail("jane.doe@example.org");
        sender3.setId(1L);
        sender3.setPassword("iloveyou");
        sender3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender3.setRole(Role.PRODUCER);
        sender3.setUsername("janedoe");

        ChatRoom chatRoom4 = new ChatRoom();
        chatRoom4.setId(1L);
        chatRoom4.setLastMessage(new ChatMessage());
        chatRoom4.setMessages(new ArrayList<>());
        chatRoom4.setParticipants(new HashSet<>());

        User sender4 = new User();
        sender4.setAvatarUrl("https://example.org/example");
        sender4.setBio("Bio");
        sender4.setEmail("jane.doe@example.org");
        sender4.setId(1L);
        sender4.setPassword("iloveyou");
        sender4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender4.setRole(Role.PRODUCER);
        sender4.setUsername("janedoe");

        ChatMessage lastMessage5 = new ChatMessage();
        lastMessage5.setChatRoom(chatRoom4);
        lastMessage5.setContent("Not all who wander are lost");
        lastMessage5.setId(1L);
        lastMessage5.setSender(sender4);
        lastMessage5.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom5 = new ChatRoom();
        chatRoom5.setId(1L);
        chatRoom5.setLastMessage(lastMessage5);
        chatRoom5.setMessages(new ArrayList<>());
        chatRoom5.setParticipants(new HashSet<>());

        User sender5 = new User();
        sender5.setAvatarUrl("https://example.org/example");
        sender5.setBalance(new BigDecimal("2.3"));
        sender5.setBio("Bio");
        sender5.setEmail("jane.doe@example.org");
        sender5.setId(1L);
        sender5.setPassword("iloveyou");
        sender5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender5.setRole(Role.PRODUCER);
        sender5.setUsername("janedoe");

        ChatMessage lastMessage6 = new ChatMessage();
        lastMessage6.setChatRoom(chatRoom5);
        lastMessage6.setContent("Not all who wander are lost");
        lastMessage6.setId(1L);
        lastMessage6.setSender(sender5);
        lastMessage6.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom6 = new ChatRoom();
        chatRoom6.setId(1L);
        chatRoom6.setLastMessage(lastMessage6);
        chatRoom6.setMessages(new ArrayList<>());
        chatRoom6.setParticipants(new HashSet<>());

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        ChatMessage chatMessage = mock(ChatMessage.class);
        when(chatMessage.getContent()).thenThrow(new EntityNotFoundException("An error occurred"));
        when(chatMessage.getSender()).thenReturn(user);
        when(chatMessage.getChatRoom()).thenReturn(chatRoom6);
        when(chatMessage.getId()).thenReturn(1L);
        doNothing().when(chatMessage).setChatRoom(Mockito.<ChatRoom>any());
        doNothing().when(chatMessage).setContent(Mockito.<String>any());
        doNothing().when(chatMessage).setId(Mockito.<Long>any());
        doNothing().when(chatMessage).setSender(Mockito.<User>any());
        doNothing().when(chatMessage).setSentAt(Mockito.<LocalDateTime>any());
        chatMessage.setChatRoom(chatRoom3);
        chatMessage.setContent("Not all who wander are lost");
        chatMessage.setId(1L);
        chatMessage.setSender(sender3);
        chatMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ChatRoom chatRoom7 = mock(ChatRoom.class);
        when(chatRoom7.getLastMessage()).thenReturn(chatMessage);
        when(chatRoom7.getId()).thenReturn(1L);
        when(chatRoom7.getParticipants()).thenReturn(new HashSet<>());
        doNothing().when(chatRoom7).setId(Mockito.<Long>any());
        doNothing().when(chatRoom7).setLastMessage(Mockito.<ChatMessage>any());
        doNothing().when(chatRoom7).setMessages(Mockito.<List<ChatMessage>>any());
        doNothing().when(chatRoom7).setParticipants(Mockito.<Set<User>>any());
        chatRoom7.setId(1L);
        chatRoom7.setLastMessage(lastMessage2);
        chatRoom7.setMessages(new ArrayList<>());
        chatRoom7.setParticipants(new HashSet<>());

        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        chatRoomList.add(chatRoom7);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(chatRoomList);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> chatServiceImpl.getUserRooms(1L));
        verify(chatMessage).getChatRoom();
        verify(chatMessage).getContent();
        verify(chatMessage).getId();
        verify(chatMessage, atLeast(1)).getSender();
        verify(chatMessage).setChatRoom(isA(ChatRoom.class));
        verify(chatMessage).setContent(eq("Not all who wander are lost"));
        verify(chatMessage).setId(eq(1L));
        verify(chatMessage).setSender(isA(User.class));
        verify(chatMessage).setSentAt(isA(LocalDateTime.class));
        verify(chatRoom7).getId();
        verify(chatRoom7, atLeast(1)).getLastMessage();
        verify(chatRoom7).getParticipants();
        verify(chatRoom7).setId(eq(1L));
        verify(chatRoom7).setLastMessage(isA(ChatMessage.class));
        verify(chatRoom7).setMessages(isA(List.class));
        verify(chatRoom7).setParticipants(isA(Set.class));
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
    }

    /**
     * Test {@link ChatServiceImpl#getUserRooms(Long)}.
     * <ul>
     *   <li>Given {@link ArrayList#ArrayList()} add {@link ChatRoom} (default constructor).</li>
     *   <li>Then return size is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#getUserRooms(Long)}
     */
    @Test
    @DisplayName("Test getUserRooms(Long); given ArrayList() add ChatRoom (default constructor); then return size is one")
    void testGetUserRooms_givenArrayListAddChatRoom_thenReturnSizeIsOne() {
        // Arrange
        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(new ChatRoom());
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(new User());
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(lastMessage);
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBalance(new BigDecimal("2.3"));
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage2);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        chatRoomList.add(chatRoom2);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(chatRoomList);

        // Act
        List<ChatRoomDto> actualUserRooms = chatServiceImpl.getUserRooms(1L);

        // Assert
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        assertEquals(1, actualUserRooms.size());
        ChatRoomDto getResult = actualUserRooms.get(0);
        ChatMessageDto lastMessage3 = getResult.getLastMessage();
        assertEquals("Not all who wander are lost", lastMessage3.getContent());
        assertEquals("janedoe", lastMessage3.getSenderUsername());
        assertEquals(1L, lastMessage3.getId().longValue());
        assertEquals(1L, lastMessage3.getRoomId().longValue());
        assertEquals(1L, lastMessage3.getSenderId().longValue());
        assertEquals(1L, getResult.getId().longValue());
    }

    /**
     * Test {@link ChatServiceImpl#getUserRooms(Long)}.
     * <ul>
     *   <li>Given {@link ChatMessage} (default constructor) Content is {@code Content}.</li>
     *   <li>Then return size is two.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#getUserRooms(Long)}
     */
    @Test
    @DisplayName("Test getUserRooms(Long); given ChatMessage (default constructor) Content is 'Content'; then return size is two")
    void testGetUserRooms_givenChatMessageContentIsContent_thenReturnSizeIsTwo() {
        // Arrange
        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(new ChatRoom());
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(new User());
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(lastMessage);
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBalance(new BigDecimal("2.3"));
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage2);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        ChatMessage lastMessage3 = new ChatMessage();
        lastMessage3.setChatRoom(new ChatRoom());
        lastMessage3.setContent("Content");
        lastMessage3.setId(2L);
        lastMessage3.setSender(new User());
        lastMessage3.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(2L);
        chatRoom3.setLastMessage(lastMessage3);
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("Avatar Url");
        sender2.setBalance(new BigDecimal("2.3"));
        sender2.setBio("com.cz.cvut.fel.instumentalshop.domain.User");
        sender2.setEmail("john.smith@example.org");
        sender2.setId(2L);
        sender2.setPassword("Password");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.CUSTOMER);
        sender2.setUsername("Username");

        ChatMessage lastMessage4 = new ChatMessage();
        lastMessage4.setChatRoom(chatRoom3);
        lastMessage4.setContent("Content");
        lastMessage4.setId(2L);
        lastMessage4.setSender(sender2);
        lastMessage4.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom4 = new ChatRoom();
        chatRoom4.setId(2L);
        chatRoom4.setLastMessage(lastMessage4);
        chatRoom4.setMessages(new ArrayList<>());
        chatRoom4.setParticipants(new HashSet<>());

        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        chatRoomList.add(chatRoom4);
        chatRoomList.add(chatRoom2);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(chatRoomList);

        // Act
        List<ChatRoomDto> actualUserRooms = chatServiceImpl.getUserRooms(1L);

        // Assert
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        assertEquals(2, actualUserRooms.size());
        ChatRoomDto getResult = actualUserRooms.get(0);
        ChatMessageDto lastMessage5 = getResult.getLastMessage();
        assertEquals("Content", lastMessage5.getContent());
        ChatRoomDto getResult2 = actualUserRooms.get(1);
        ChatMessageDto lastMessage6 = getResult2.getLastMessage();
        assertEquals("Not all who wander are lost", lastMessage6.getContent());
        assertEquals("Username", lastMessage5.getSenderUsername());
        assertEquals("janedoe", lastMessage6.getSenderUsername());
        assertEquals(1L, lastMessage6.getId().longValue());
        assertEquals(1L, lastMessage6.getRoomId().longValue());
        assertEquals(1L, lastMessage6.getSenderId().longValue());
        assertEquals(1L, getResult2.getId().longValue());
        assertEquals(2L, lastMessage5.getId().longValue());
        assertEquals(2L, lastMessage5.getRoomId().longValue());
        assertEquals(2L, lastMessage5.getSenderId().longValue());
        assertEquals(2L, getResult.getId().longValue());
        assertTrue(getResult2.getParticipants().isEmpty());
    }

    /**
     * Test {@link ChatServiceImpl#getUserRooms(Long)}.
     * <ul>
     *   <li>Then calls {@link ChatMessage#getSentAt()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#getUserRooms(Long)}
     */
    @Test
    @DisplayName("Test getUserRooms(Long); then calls getSentAt()")
    void testGetUserRooms_thenCallsGetSentAt() {
        // Arrange
        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(new ChatRoom());
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(new User());
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(lastMessage);
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBalance(new BigDecimal("2.3"));
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatMessage lastMessage3 = new ChatMessage();
        lastMessage3.setChatRoom(new ChatRoom());
        lastMessage3.setContent("Not all who wander are lost");
        lastMessage3.setId(1L);
        lastMessage3.setSender(new User());
        lastMessage3.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage3);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("https://example.org/example");
        sender2.setBalance(new BigDecimal("2.3"));
        sender2.setBio("Bio");
        sender2.setEmail("jane.doe@example.org");
        sender2.setId(1L);
        sender2.setPassword("iloveyou");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.PRODUCER);
        sender2.setUsername("janedoe");

        ChatMessage lastMessage4 = new ChatMessage();
        lastMessage4.setChatRoom(chatRoom2);
        lastMessage4.setContent("Not all who wander are lost");
        lastMessage4.setId(1L);
        lastMessage4.setSender(sender2);
        lastMessage4.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(1L);
        chatRoom3.setLastMessage(lastMessage4);
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());

        User sender3 = new User();
        sender3.setAvatarUrl("https://example.org/example");
        sender3.setBalance(new BigDecimal("2.3"));
        sender3.setBio("Bio");
        sender3.setEmail("jane.doe@example.org");
        sender3.setId(1L);
        sender3.setPassword("iloveyou");
        sender3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender3.setRole(Role.PRODUCER);
        sender3.setUsername("janedoe");

        ChatRoom chatRoom4 = new ChatRoom();
        chatRoom4.setId(1L);
        chatRoom4.setLastMessage(new ChatMessage());
        chatRoom4.setMessages(new ArrayList<>());
        chatRoom4.setParticipants(new HashSet<>());

        User sender4 = new User();
        sender4.setAvatarUrl("https://example.org/example");
        sender4.setBio("Bio");
        sender4.setEmail("jane.doe@example.org");
        sender4.setId(1L);
        sender4.setPassword("iloveyou");
        sender4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender4.setRole(Role.PRODUCER);
        sender4.setUsername("janedoe");

        ChatMessage lastMessage5 = new ChatMessage();
        lastMessage5.setChatRoom(chatRoom4);
        lastMessage5.setContent("Not all who wander are lost");
        lastMessage5.setId(1L);
        lastMessage5.setSender(sender4);
        lastMessage5.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom5 = new ChatRoom();
        chatRoom5.setId(1L);
        chatRoom5.setLastMessage(lastMessage5);
        chatRoom5.setMessages(new ArrayList<>());
        chatRoom5.setParticipants(new HashSet<>());

        User sender5 = new User();
        sender5.setAvatarUrl("https://example.org/example");
        sender5.setBalance(new BigDecimal("2.3"));
        sender5.setBio("Bio");
        sender5.setEmail("jane.doe@example.org");
        sender5.setId(1L);
        sender5.setPassword("iloveyou");
        sender5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender5.setRole(Role.PRODUCER);
        sender5.setUsername("janedoe");

        ChatMessage lastMessage6 = new ChatMessage();
        lastMessage6.setChatRoom(chatRoom5);
        lastMessage6.setContent("Not all who wander are lost");
        lastMessage6.setId(1L);
        lastMessage6.setSender(sender5);
        lastMessage6.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom6 = new ChatRoom();
        chatRoom6.setId(1L);
        chatRoom6.setLastMessage(lastMessage6);
        chatRoom6.setMessages(new ArrayList<>());
        chatRoom6.setParticipants(new HashSet<>());

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        ChatMessage chatMessage = mock(ChatMessage.class);
        when(chatMessage.getContent()).thenReturn("Not all who wander are lost");
        when(chatMessage.getSentAt()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
        when(chatMessage.getSender()).thenReturn(user);
        when(chatMessage.getChatRoom()).thenReturn(chatRoom6);
        when(chatMessage.getId()).thenReturn(1L);
        doNothing().when(chatMessage).setChatRoom(Mockito.<ChatRoom>any());
        doNothing().when(chatMessage).setContent(Mockito.<String>any());
        doNothing().when(chatMessage).setId(Mockito.<Long>any());
        doNothing().when(chatMessage).setSender(Mockito.<User>any());
        doNothing().when(chatMessage).setSentAt(Mockito.<LocalDateTime>any());
        chatMessage.setChatRoom(chatRoom3);
        chatMessage.setContent("Not all who wander are lost");
        chatMessage.setId(1L);
        chatMessage.setSender(sender3);
        chatMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ChatRoom chatRoom7 = mock(ChatRoom.class);
        when(chatRoom7.getLastMessage()).thenReturn(chatMessage);
        when(chatRoom7.getId()).thenReturn(1L);
        when(chatRoom7.getParticipants()).thenReturn(new HashSet<>());
        doNothing().when(chatRoom7).setId(Mockito.<Long>any());
        doNothing().when(chatRoom7).setLastMessage(Mockito.<ChatMessage>any());
        doNothing().when(chatRoom7).setMessages(Mockito.<List<ChatMessage>>any());
        doNothing().when(chatRoom7).setParticipants(Mockito.<Set<User>>any());
        chatRoom7.setId(1L);
        chatRoom7.setLastMessage(lastMessage2);
        chatRoom7.setMessages(new ArrayList<>());
        chatRoom7.setParticipants(new HashSet<>());

        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        chatRoomList.add(chatRoom7);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(chatRoomList);

        // Act
        List<ChatRoomDto> actualUserRooms = chatServiceImpl.getUserRooms(1L);

        // Assert
        verify(chatMessage).getChatRoom();
        verify(chatMessage).getContent();
        verify(chatMessage).getId();
        verify(chatMessage, atLeast(1)).getSender();
        verify(chatMessage).getSentAt();
        verify(chatMessage).setChatRoom(isA(ChatRoom.class));
        verify(chatMessage).setContent(eq("Not all who wander are lost"));
        verify(chatMessage).setId(eq(1L));
        verify(chatMessage).setSender(isA(User.class));
        verify(chatMessage).setSentAt(isA(LocalDateTime.class));
        verify(chatRoom7).getId();
        verify(chatRoom7, atLeast(1)).getLastMessage();
        verify(chatRoom7).getParticipants();
        verify(chatRoom7).setId(eq(1L));
        verify(chatRoom7).setLastMessage(isA(ChatMessage.class));
        verify(chatRoom7).setMessages(isA(List.class));
        verify(chatRoom7).setParticipants(isA(Set.class));
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        assertEquals(1, actualUserRooms.size());
        ChatRoomDto getResult = actualUserRooms.get(0);
        ChatMessageDto lastMessage7 = getResult.getLastMessage();
        assertEquals("Not all who wander are lost", lastMessage7.getContent());
        assertEquals("janedoe", lastMessage7.getSenderUsername());
        assertEquals(1L, lastMessage7.getId().longValue());
        assertEquals(1L, lastMessage7.getRoomId().longValue());
        assertEquals(1L, lastMessage7.getSenderId().longValue());
        assertEquals(1L, getResult.getId().longValue());
    }

    /**
     * Test {@link ChatServiceImpl#getUserRooms(Long)}.
     * <ul>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#getUserRooms(Long)}
     */
    @Test
    @DisplayName("Test getUserRooms(Long); then return Empty")
    void testGetUserRooms_thenReturnEmpty() {
        // Arrange
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        // Act
        List<ChatRoomDto> actualUserRooms = chatServiceImpl.getUserRooms(1L);

        // Assert
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        assertTrue(actualUserRooms.isEmpty());
    }

    /**
     * Test {@link ChatServiceImpl#openRoom(Long, Long)}.
     * <p>
     * Method under test: {@link ChatServiceImpl#openRoom(Long, Long)}
     */
    @Test
    @DisplayName("Test openRoom(Long, Long)")
    void testOpenRoom() {
        // Arrange
        when(chatRoomRepository.save(Mockito.<ChatRoom>any())).thenThrow(new EntityNotFoundException("An error occurred"));
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> chatServiceImpl.openRoom(1L, 1L));
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        verify(userRepository, atLeast(1)).findById(eq(1L));
        verify(chatRoomRepository).save(isA(ChatRoom.class));
    }

    /**
     * Test {@link ChatServiceImpl#openRoom(Long, Long)}.
     * <p>
     * Method under test: {@link ChatServiceImpl#openRoom(Long, Long)}
     */
    @Test
    @DisplayName("Test openRoom(Long, Long)")
    void testOpenRoom2() {
        // Arrange
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(new ChatMessage());
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(chatRoom);
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(sender);
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("https://example.org/example");
        sender2.setBalance(new BigDecimal("2.3"));
        sender2.setBio("Bio");
        sender2.setEmail("jane.doe@example.org");
        sender2.setId(1L);
        sender2.setPassword("iloveyou");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.PRODUCER);
        sender2.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom2);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender2);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatMessage lastMessage3 = new ChatMessage();
        lastMessage3.setChatRoom(new ChatRoom());
        lastMessage3.setContent("Not all who wander are lost");
        lastMessage3.setId(1L);
        lastMessage3.setSender(new User());
        lastMessage3.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(1L);
        chatRoom3.setLastMessage(lastMessage3);
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());

        User sender3 = new User();
        sender3.setAvatarUrl("https://example.org/example");
        sender3.setBalance(new BigDecimal("2.3"));
        sender3.setBio("Bio");
        sender3.setEmail("jane.doe@example.org");
        sender3.setId(1L);
        sender3.setPassword("iloveyou");
        sender3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender3.setRole(Role.PRODUCER);
        sender3.setUsername("janedoe");

        ChatMessage lastMessage4 = new ChatMessage();
        lastMessage4.setChatRoom(chatRoom3);
        lastMessage4.setContent("Not all who wander are lost");
        lastMessage4.setId(1L);
        lastMessage4.setSender(sender3);
        lastMessage4.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom4 = new ChatRoom();
        chatRoom4.setId(1L);
        chatRoom4.setLastMessage(lastMessage4);
        chatRoom4.setMessages(new ArrayList<>());
        chatRoom4.setParticipants(new HashSet<>());

        User sender4 = new User();
        sender4.setAvatarUrl("https://example.org/example");
        sender4.setBalance(new BigDecimal("2.3"));
        sender4.setBio("Bio");
        sender4.setEmail("jane.doe@example.org");
        sender4.setId(1L);
        sender4.setPassword("iloveyou");
        sender4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender4.setRole(Role.PRODUCER);
        sender4.setUsername("janedoe");

        ChatRoom chatRoom5 = new ChatRoom();
        chatRoom5.setId(1L);
        chatRoom5.setLastMessage(new ChatMessage());
        chatRoom5.setMessages(new ArrayList<>());
        chatRoom5.setParticipants(new HashSet<>());

        User sender5 = new User();
        sender5.setAvatarUrl("https://example.org/example");
        sender5.setBio("Bio");
        sender5.setEmail("jane.doe@example.org");
        sender5.setId(1L);
        sender5.setPassword("iloveyou");
        sender5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender5.setRole(Role.PRODUCER);
        sender5.setUsername("janedoe");

        ChatMessage lastMessage5 = new ChatMessage();
        lastMessage5.setChatRoom(chatRoom5);
        lastMessage5.setContent("Not all who wander are lost");
        lastMessage5.setId(1L);
        lastMessage5.setSender(sender5);
        lastMessage5.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom6 = new ChatRoom();
        chatRoom6.setId(1L);
        chatRoom6.setLastMessage(lastMessage5);
        chatRoom6.setMessages(new ArrayList<>());
        chatRoom6.setParticipants(new HashSet<>());

        User sender6 = new User();
        sender6.setAvatarUrl("https://example.org/example");
        sender6.setBalance(new BigDecimal("2.3"));
        sender6.setBio("Bio");
        sender6.setEmail("jane.doe@example.org");
        sender6.setId(1L);
        sender6.setPassword("iloveyou");
        sender6.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender6.setRole(Role.PRODUCER);
        sender6.setUsername("janedoe");

        ChatMessage lastMessage6 = new ChatMessage();
        lastMessage6.setChatRoom(chatRoom6);
        lastMessage6.setContent("Not all who wander are lost");
        lastMessage6.setId(1L);
        lastMessage6.setSender(sender6);
        lastMessage6.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom7 = new ChatRoom();
        chatRoom7.setId(1L);
        chatRoom7.setLastMessage(lastMessage6);
        chatRoom7.setMessages(new ArrayList<>());
        chatRoom7.setParticipants(new HashSet<>());

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        ChatMessage chatMessage = mock(ChatMessage.class);
        when(chatMessage.getContent()).thenThrow(new EntityNotFoundException("An error occurred"));
        when(chatMessage.getSender()).thenReturn(user);
        when(chatMessage.getChatRoom()).thenReturn(chatRoom7);
        when(chatMessage.getId()).thenReturn(1L);
        doNothing().when(chatMessage).setChatRoom(Mockito.<ChatRoom>any());
        doNothing().when(chatMessage).setContent(Mockito.<String>any());
        doNothing().when(chatMessage).setId(Mockito.<Long>any());
        doNothing().when(chatMessage).setSender(Mockito.<User>any());
        doNothing().when(chatMessage).setSentAt(Mockito.<LocalDateTime>any());
        chatMessage.setChatRoom(chatRoom4);
        chatMessage.setContent("Not all who wander are lost");
        chatMessage.setId(1L);
        chatMessage.setSender(sender4);
        chatMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ChatRoom chatRoom8 = mock(ChatRoom.class);
        when(chatRoom8.getLastMessage()).thenReturn(chatMessage);
        when(chatRoom8.getId()).thenReturn(1L);
        when(chatRoom8.getParticipants()).thenReturn(new HashSet<>());
        doNothing().when(chatRoom8).setId(Mockito.<Long>any());
        doNothing().when(chatRoom8).setLastMessage(Mockito.<ChatMessage>any());
        doNothing().when(chatRoom8).setMessages(Mockito.<List<ChatMessage>>any());
        doNothing().when(chatRoom8).setParticipants(Mockito.<Set<User>>any());
        chatRoom8.setId(1L);
        chatRoom8.setLastMessage(lastMessage2);
        chatRoom8.setMessages(new ArrayList<>());
        chatRoom8.setParticipants(new HashSet<>());
        when(chatRoomRepository.save(Mockito.<ChatRoom>any())).thenReturn(chatRoom8);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        User user2 = new User();
        user2.setAvatarUrl("https://example.org/example");
        user2.setBalance(new BigDecimal("2.3"));
        user2.setBio("Bio");
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setPassword("iloveyou");
        user2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setRole(Role.PRODUCER);
        user2.setUsername("janedoe");
        Optional<User> ofResult = Optional.of(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> chatServiceImpl.openRoom(1L, 1L));
        verify(chatMessage).getChatRoom();
        verify(chatMessage).getContent();
        verify(chatMessage).getId();
        verify(chatMessage, atLeast(1)).getSender();
        verify(chatMessage).setChatRoom(isA(ChatRoom.class));
        verify(chatMessage).setContent(eq("Not all who wander are lost"));
        verify(chatMessage).setId(eq(1L));
        verify(chatMessage).setSender(isA(User.class));
        verify(chatMessage).setSentAt(isA(LocalDateTime.class));
        verify(chatRoom8).getId();
        verify(chatRoom8, atLeast(1)).getLastMessage();
        verify(chatRoom8).getParticipants();
        verify(chatRoom8).setId(eq(1L));
        verify(chatRoom8).setLastMessage(isA(ChatMessage.class));
        verify(chatRoom8).setMessages(isA(List.class));
        verify(chatRoom8).setParticipants(isA(Set.class));
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        verify(userRepository, atLeast(1)).findById(eq(1L));
        verify(chatRoomRepository).save(isA(ChatRoom.class));
    }

    /**
     * Test {@link ChatServiceImpl#openRoom(Long, Long)}.
     * <ul>
     *   <li>Given {@link ChatRoomRepository} {@link CrudRepository#save(Object)} return {@link ChatRoom} (default constructor).</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#openRoom(Long, Long)}
     */
    @Test
    @DisplayName("Test openRoom(Long, Long); given ChatRoomRepository save(Object) return ChatRoom (default constructor)")
    void testOpenRoom_givenChatRoomRepositorySaveReturnChatRoom() {
        // Arrange
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(new ChatMessage());
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(chatRoom);
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(sender);
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("https://example.org/example");
        sender2.setBalance(new BigDecimal("2.3"));
        sender2.setBio("Bio");
        sender2.setEmail("jane.doe@example.org");
        sender2.setId(1L);
        sender2.setPassword("iloveyou");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.PRODUCER);
        sender2.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom2);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender2);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        lastMessage2.setSentAt(ofResult.atStartOfDay());

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(1L);
        chatRoom3.setLastMessage(lastMessage2);
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());
        when(chatRoomRepository.save(Mockito.<ChatRoom>any())).thenReturn(chatRoom3);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);

        // Act
        ChatRoomDto actualOpenRoomResult = chatServiceImpl.openRoom(1L, 1L);

        // Assert
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        verify(userRepository, atLeast(1)).findById(eq(1L));
        verify(chatRoomRepository).save(isA(ChatRoom.class));
        ChatMessageDto lastMessage3 = actualOpenRoomResult.getLastMessage();
        LocalDateTime sentAt = lastMessage3.getSentAt();
        assertEquals("00:00", sentAt.toLocalTime().toString());
        LocalDate toLocalDateResult = sentAt.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Not all who wander are lost", lastMessage3.getContent());
        assertEquals("janedoe", lastMessage3.getSenderUsername());
        assertEquals(1L, lastMessage3.getId().longValue());
        assertEquals(1L, lastMessage3.getRoomId().longValue());
        assertEquals(1L, lastMessage3.getSenderId().longValue());
        assertEquals(1L, actualOpenRoomResult.getId().longValue());
        assertTrue(actualOpenRoomResult.getParticipants().isEmpty());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link ChatServiceImpl#openRoom(Long, Long)}.
     * <ul>
     *   <li>Then calls {@link ChatMessage#getSentAt()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#openRoom(Long, Long)}
     */
    @Test
    @DisplayName("Test openRoom(Long, Long); then calls getSentAt()")
    void testOpenRoom_thenCallsGetSentAt() {
        // Arrange
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(new ChatMessage());
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(chatRoom);
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(sender);
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("https://example.org/example");
        sender2.setBalance(new BigDecimal("2.3"));
        sender2.setBio("Bio");
        sender2.setEmail("jane.doe@example.org");
        sender2.setId(1L);
        sender2.setPassword("iloveyou");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.PRODUCER);
        sender2.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom2);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender2);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatMessage lastMessage3 = new ChatMessage();
        lastMessage3.setChatRoom(new ChatRoom());
        lastMessage3.setContent("Not all who wander are lost");
        lastMessage3.setId(1L);
        lastMessage3.setSender(new User());
        lastMessage3.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(1L);
        chatRoom3.setLastMessage(lastMessage3);
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());

        User sender3 = new User();
        sender3.setAvatarUrl("https://example.org/example");
        sender3.setBalance(new BigDecimal("2.3"));
        sender3.setBio("Bio");
        sender3.setEmail("jane.doe@example.org");
        sender3.setId(1L);
        sender3.setPassword("iloveyou");
        sender3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender3.setRole(Role.PRODUCER);
        sender3.setUsername("janedoe");

        ChatMessage lastMessage4 = new ChatMessage();
        lastMessage4.setChatRoom(chatRoom3);
        lastMessage4.setContent("Not all who wander are lost");
        lastMessage4.setId(1L);
        lastMessage4.setSender(sender3);
        lastMessage4.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom4 = new ChatRoom();
        chatRoom4.setId(1L);
        chatRoom4.setLastMessage(lastMessage4);
        chatRoom4.setMessages(new ArrayList<>());
        chatRoom4.setParticipants(new HashSet<>());

        User sender4 = new User();
        sender4.setAvatarUrl("https://example.org/example");
        sender4.setBalance(new BigDecimal("2.3"));
        sender4.setBio("Bio");
        sender4.setEmail("jane.doe@example.org");
        sender4.setId(1L);
        sender4.setPassword("iloveyou");
        sender4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender4.setRole(Role.PRODUCER);
        sender4.setUsername("janedoe");

        ChatRoom chatRoom5 = new ChatRoom();
        chatRoom5.setId(1L);
        chatRoom5.setLastMessage(new ChatMessage());
        chatRoom5.setMessages(new ArrayList<>());
        chatRoom5.setParticipants(new HashSet<>());

        User sender5 = new User();
        sender5.setAvatarUrl("https://example.org/example");
        sender5.setBio("Bio");
        sender5.setEmail("jane.doe@example.org");
        sender5.setId(1L);
        sender5.setPassword("iloveyou");
        sender5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender5.setRole(Role.PRODUCER);
        sender5.setUsername("janedoe");

        ChatMessage lastMessage5 = new ChatMessage();
        lastMessage5.setChatRoom(chatRoom5);
        lastMessage5.setContent("Not all who wander are lost");
        lastMessage5.setId(1L);
        lastMessage5.setSender(sender5);
        lastMessage5.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom6 = new ChatRoom();
        chatRoom6.setId(1L);
        chatRoom6.setLastMessage(lastMessage5);
        chatRoom6.setMessages(new ArrayList<>());
        chatRoom6.setParticipants(new HashSet<>());

        User sender6 = new User();
        sender6.setAvatarUrl("https://example.org/example");
        sender6.setBalance(new BigDecimal("2.3"));
        sender6.setBio("Bio");
        sender6.setEmail("jane.doe@example.org");
        sender6.setId(1L);
        sender6.setPassword("iloveyou");
        sender6.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender6.setRole(Role.PRODUCER);
        sender6.setUsername("janedoe");

        ChatMessage lastMessage6 = new ChatMessage();
        lastMessage6.setChatRoom(chatRoom6);
        lastMessage6.setContent("Not all who wander are lost");
        lastMessage6.setId(1L);
        lastMessage6.setSender(sender6);
        lastMessage6.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom7 = new ChatRoom();
        chatRoom7.setId(1L);
        chatRoom7.setLastMessage(lastMessage6);
        chatRoom7.setMessages(new ArrayList<>());
        chatRoom7.setParticipants(new HashSet<>());

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        ChatMessage chatMessage = mock(ChatMessage.class);
        when(chatMessage.getContent()).thenReturn("Not all who wander are lost");
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        when(chatMessage.getSentAt()).thenReturn(ofResult.atStartOfDay());
        when(chatMessage.getSender()).thenReturn(user);
        when(chatMessage.getChatRoom()).thenReturn(chatRoom7);
        when(chatMessage.getId()).thenReturn(1L);
        doNothing().when(chatMessage).setChatRoom(Mockito.<ChatRoom>any());
        doNothing().when(chatMessage).setContent(Mockito.<String>any());
        doNothing().when(chatMessage).setId(Mockito.<Long>any());
        doNothing().when(chatMessage).setSender(Mockito.<User>any());
        doNothing().when(chatMessage).setSentAt(Mockito.<LocalDateTime>any());
        chatMessage.setChatRoom(chatRoom4);
        chatMessage.setContent("Not all who wander are lost");
        chatMessage.setId(1L);
        chatMessage.setSender(sender4);
        chatMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ChatRoom chatRoom8 = mock(ChatRoom.class);
        when(chatRoom8.getLastMessage()).thenReturn(chatMessage);
        when(chatRoom8.getId()).thenReturn(1L);
        when(chatRoom8.getParticipants()).thenReturn(new HashSet<>());
        doNothing().when(chatRoom8).setId(Mockito.<Long>any());
        doNothing().when(chatRoom8).setLastMessage(Mockito.<ChatMessage>any());
        doNothing().when(chatRoom8).setMessages(Mockito.<List<ChatMessage>>any());
        doNothing().when(chatRoom8).setParticipants(Mockito.<Set<User>>any());
        chatRoom8.setId(1L);
        chatRoom8.setLastMessage(lastMessage2);
        chatRoom8.setMessages(new ArrayList<>());
        chatRoom8.setParticipants(new HashSet<>());
        when(chatRoomRepository.save(Mockito.<ChatRoom>any())).thenReturn(chatRoom8);
        when(chatRoomRepository.findAllByParticipant(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        User user2 = new User();
        user2.setAvatarUrl("https://example.org/example");
        user2.setBalance(new BigDecimal("2.3"));
        user2.setBio("Bio");
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setPassword("iloveyou");
        user2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setRole(Role.PRODUCER);
        user2.setUsername("janedoe");
        Optional<User> ofResult2 = Optional.of(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);

        // Act
        ChatRoomDto actualOpenRoomResult = chatServiceImpl.openRoom(1L, 1L);

        // Assert
        verify(chatMessage).getChatRoom();
        verify(chatMessage).getContent();
        verify(chatMessage).getId();
        verify(chatMessage, atLeast(1)).getSender();
        verify(chatMessage).getSentAt();
        verify(chatMessage).setChatRoom(isA(ChatRoom.class));
        verify(chatMessage).setContent(eq("Not all who wander are lost"));
        verify(chatMessage).setId(eq(1L));
        verify(chatMessage).setSender(isA(User.class));
        verify(chatMessage).setSentAt(isA(LocalDateTime.class));
        verify(chatRoom8).getId();
        verify(chatRoom8, atLeast(1)).getLastMessage();
        verify(chatRoom8).getParticipants();
        verify(chatRoom8).setId(eq(1L));
        verify(chatRoom8).setLastMessage(isA(ChatMessage.class));
        verify(chatRoom8).setMessages(isA(List.class));
        verify(chatRoom8).setParticipants(isA(Set.class));
        verify(chatRoomRepository).findAllByParticipant(eq(1L));
        verify(userRepository, atLeast(1)).findById(eq(1L));
        verify(chatRoomRepository).save(isA(ChatRoom.class));
        ChatMessageDto lastMessage7 = actualOpenRoomResult.getLastMessage();
        LocalDateTime sentAt = lastMessage7.getSentAt();
        assertEquals("00:00", sentAt.toLocalTime().toString());
        LocalDate toLocalDateResult = sentAt.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Not all who wander are lost", lastMessage7.getContent());
        assertEquals("janedoe", lastMessage7.getSenderUsername());
        assertEquals(1L, lastMessage7.getId().longValue());
        assertEquals(1L, lastMessage7.getRoomId().longValue());
        assertEquals(1L, lastMessage7.getSenderId().longValue());
        assertEquals(1L, actualOpenRoomResult.getId().longValue());
        assertTrue(actualOpenRoomResult.getParticipants().isEmpty());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link ChatServiceImpl#getMessages(Long)}.
     * <ul>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#getMessages(Long)}
     */
    @Test
    @DisplayName("Test getMessages(Long); then return Empty")
    void testGetMessages_thenReturnEmpty() {
        // Arrange
        when(chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        // Act
        List<ChatMessage> actualMessages = chatServiceImpl.getMessages(1L);

        // Assert
        verify(chatMessageRepository).findByChatRoomIdOrderBySentAtAsc(eq(1L));
        assertTrue(actualMessages.isEmpty());
    }

    /**
     * Test {@link ChatServiceImpl#getMessages(Long)}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#getMessages(Long)}
     */
    @Test
    @DisplayName("Test getMessages(Long); then throw EntityNotFoundException")
    void testGetMessages_thenThrowEntityNotFoundException() {
        // Arrange
        when(chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(Mockito.<Long>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> chatServiceImpl.getMessages(1L));
        verify(chatMessageRepository).findByChatRoomIdOrderBySentAtAsc(eq(1L));
    }

    /**
     * Test {@link ChatServiceImpl#saveMessage(Long, Long, String)}.
     * <p>
     * Method under test: {@link ChatServiceImpl#saveMessage(Long, Long, String)}
     */
    @Test
    @DisplayName("Test saveMessage(Long, Long, String)")
    void testSaveMessage() {
        // Arrange
        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(new ChatRoom());
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(new User());
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(lastMessage);
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBalance(new BigDecimal("2.3"));
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage2);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());
        Optional<ChatRoom> ofResult = Optional.of(chatRoom2);
        when(chatRoomRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(chatMessageRepository.save(Mockito.<ChatMessage>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);

        // Act and Assert
        assertThrows(EntityNotFoundException.class,
                () -> chatServiceImpl.saveMessage(1L, 1L, "Not all who wander are lost"));
        verify(userRepository).findById(eq(1L));
        verify(chatRoomRepository).findById(eq(1L));
        verify(chatMessageRepository).save(isA(ChatMessage.class));
    }

    /**
     * Test {@link ChatServiceImpl#saveMessage(Long, Long, String)}.
     * <ul>
     *   <li>Given {@link ChatRoomRepository} {@link CrudRepository#findById(Object)} return empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#saveMessage(Long, Long, String)}
     */
    @Test
    @DisplayName("Test saveMessage(Long, Long, String); given ChatRoomRepository findById(Object) return empty")
    void testSaveMessage_givenChatRoomRepositoryFindByIdReturnEmpty() {
        // Arrange
        ChatRoomRepository roomRepo = mock(ChatRoomRepository.class);
        Optional<ChatRoom> emptyResult = Optional.empty();
        when(roomRepo.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class,
                () -> (new ChatServiceImpl(roomRepo, mock(ChatMessageRepository.class), mock(UserRepository.class)))
                        .saveMessage(1L, 1L, "Not all who wander are lost"));
        verify(roomRepo).findById(eq(1L));
    }

    /**
     * Test {@link ChatServiceImpl#saveMessage(Long, Long, String)}.
     * <ul>
     *   <li>Given {@link ChatRoomRepository} {@link CrudRepository#save(Object)} return {@link ChatRoom} (default constructor).</li>
     *   <li>Then return {@link ChatMessage} (default constructor).</li>
     * </ul>
     * <p>
     * Method under test: {@link ChatServiceImpl#saveMessage(Long, Long, String)}
     */
    @Test
    @DisplayName("Test saveMessage(Long, Long, String); given ChatRoomRepository save(Object) return ChatRoom (default constructor); then return ChatMessage (default constructor)")
    void testSaveMessage_givenChatRoomRepositorySaveReturnChatRoom_thenReturnChatMessage() {
        // Arrange
        ChatMessage lastMessage = new ChatMessage();
        lastMessage.setChatRoom(new ChatRoom());
        lastMessage.setContent("Not all who wander are lost");
        lastMessage.setId(1L);
        lastMessage.setSender(new User());
        lastMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setLastMessage(lastMessage);
        chatRoom.setMessages(new ArrayList<>());
        chatRoom.setParticipants(new HashSet<>());

        User sender = new User();
        sender.setAvatarUrl("https://example.org/example");
        sender.setBalance(new BigDecimal("2.3"));
        sender.setBio("Bio");
        sender.setEmail("jane.doe@example.org");
        sender.setId(1L);
        sender.setPassword("iloveyou");
        sender.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender.setRole(Role.PRODUCER);
        sender.setUsername("janedoe");

        ChatMessage lastMessage2 = new ChatMessage();
        lastMessage2.setChatRoom(chatRoom);
        lastMessage2.setContent("Not all who wander are lost");
        lastMessage2.setId(1L);
        lastMessage2.setSender(sender);
        lastMessage2.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(1L);
        chatRoom2.setLastMessage(lastMessage2);
        chatRoom2.setMessages(new ArrayList<>());
        chatRoom2.setParticipants(new HashSet<>());
        Optional<ChatRoom> ofResult = Optional.of(chatRoom2);

        ChatRoom chatRoom3 = new ChatRoom();
        chatRoom3.setId(1L);
        chatRoom3.setLastMessage(new ChatMessage());
        chatRoom3.setMessages(new ArrayList<>());
        chatRoom3.setParticipants(new HashSet<>());

        User sender2 = new User();
        sender2.setAvatarUrl("https://example.org/example");
        sender2.setBio("Bio");
        sender2.setEmail("jane.doe@example.org");
        sender2.setId(1L);
        sender2.setPassword("iloveyou");
        sender2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender2.setRole(Role.PRODUCER);
        sender2.setUsername("janedoe");

        ChatMessage lastMessage3 = new ChatMessage();
        lastMessage3.setChatRoom(chatRoom3);
        lastMessage3.setContent("Not all who wander are lost");
        lastMessage3.setId(1L);
        lastMessage3.setSender(sender2);
        lastMessage3.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom4 = new ChatRoom();
        chatRoom4.setId(1L);
        chatRoom4.setLastMessage(lastMessage3);
        chatRoom4.setMessages(new ArrayList<>());
        chatRoom4.setParticipants(new HashSet<>());

        User sender3 = new User();
        sender3.setAvatarUrl("https://example.org/example");
        sender3.setBalance(new BigDecimal("2.3"));
        sender3.setBio("Bio");
        sender3.setEmail("jane.doe@example.org");
        sender3.setId(1L);
        sender3.setPassword("iloveyou");
        sender3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender3.setRole(Role.PRODUCER);
        sender3.setUsername("janedoe");

        ChatMessage lastMessage4 = new ChatMessage();
        lastMessage4.setChatRoom(chatRoom4);
        lastMessage4.setContent("Not all who wander are lost");
        lastMessage4.setId(1L);
        lastMessage4.setSender(sender3);
        lastMessage4.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom5 = new ChatRoom();
        chatRoom5.setId(1L);
        chatRoom5.setLastMessage(lastMessage4);
        chatRoom5.setMessages(new ArrayList<>());
        chatRoom5.setParticipants(new HashSet<>());
        when(chatRoomRepository.save(Mockito.<ChatRoom>any())).thenReturn(chatRoom5);
        when(chatRoomRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        ChatMessage lastMessage5 = new ChatMessage();
        lastMessage5.setChatRoom(new ChatRoom());
        lastMessage5.setContent("Not all who wander are lost");
        lastMessage5.setId(1L);
        lastMessage5.setSender(new User());
        lastMessage5.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom6 = new ChatRoom();
        chatRoom6.setId(1L);
        chatRoom6.setLastMessage(lastMessage5);
        chatRoom6.setMessages(new ArrayList<>());
        chatRoom6.setParticipants(new HashSet<>());

        User sender4 = new User();
        sender4.setAvatarUrl("https://example.org/example");
        sender4.setBalance(new BigDecimal("2.3"));
        sender4.setBio("Bio");
        sender4.setEmail("jane.doe@example.org");
        sender4.setId(1L);
        sender4.setPassword("iloveyou");
        sender4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender4.setRole(Role.PRODUCER);
        sender4.setUsername("janedoe");

        ChatMessage lastMessage6 = new ChatMessage();
        lastMessage6.setChatRoom(chatRoom6);
        lastMessage6.setContent("Not all who wander are lost");
        lastMessage6.setId(1L);
        lastMessage6.setSender(sender4);
        lastMessage6.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ChatRoom chatRoom7 = new ChatRoom();
        chatRoom7.setId(1L);
        chatRoom7.setLastMessage(lastMessage6);
        chatRoom7.setMessages(new ArrayList<>());
        chatRoom7.setParticipants(new HashSet<>());

        User sender5 = new User();
        sender5.setAvatarUrl("https://example.org/example");
        sender5.setBalance(new BigDecimal("2.3"));
        sender5.setBio("Bio");
        sender5.setEmail("jane.doe@example.org");
        sender5.setId(1L);
        sender5.setPassword("iloveyou");
        sender5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        sender5.setRole(Role.PRODUCER);
        sender5.setUsername("janedoe");

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom7);
        chatMessage.setContent("Not all who wander are lost");
        chatMessage.setId(1L);
        chatMessage.setSender(sender5);
        chatMessage.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        when(chatMessageRepository.save(Mockito.<ChatMessage>any())).thenReturn(chatMessage);

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);

        // Act
        ChatMessage actualSaveMessageResult = chatServiceImpl.saveMessage(1L, 1L, "Not all who wander are lost");

        // Assert
        verify(userRepository).findById(eq(1L));
        verify(chatRoomRepository).findById(eq(1L));
        verify(chatMessageRepository).save(isA(ChatMessage.class));
        verify(chatRoomRepository).save(isA(ChatRoom.class));
        assertSame(chatMessage, actualSaveMessageResult);
    }
}
