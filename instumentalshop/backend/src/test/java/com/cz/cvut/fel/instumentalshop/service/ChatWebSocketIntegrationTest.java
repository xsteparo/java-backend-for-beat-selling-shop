package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.chat.ChatMessageDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.chat.ChatServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatWebSocketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatServiceImpl chatService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String fakeToken = "Bearer FAKE.JWT.TOKEN";
    private Long roomId = 123L;
    private Long userId = 456L;

    @BeforeEach
    void setUp() {
        // Stub JWTService to accept any token and return username "testuser"
        // Stub UserRepository to return a User with id = userId
        // Stub ChatServiceImpl.saveMessage(...) to return a ChatMessage with content echo
        // You can use Mockito.when(...) here.
    }

    @Test
    void sendMessage_viaStompEndpoint_invokesServiceAndSendsToTopic() throws Exception {
        // Prepare payload
        ChatMessageDto payload = ChatMessageDto.builder()
                .content("Integration test")
                .build();

        // Perform a WebSocket upgrade + STOMP SEND to our endpoint
        mockMvc.perform(
                        post("/app/chat/{roomId}/send", roomId)
                                .header("Authorization", fakeToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload))
                )
                .andExpect(status().isOk());

        // Verify that chatService.saveMessage(...) was called with correct args
        Mockito.verify(chatService).saveMessage(eq(roomId), eq(userId), eq("Integration test"));
        // And verify that SimpMessagingTemplate.convertAndSend(...) was invoked
        // (you may need to @SpyBean the SimpMessagingTemplate to capture that)
    }
}