package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.ProducerServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProducerServiceImplTest {

    private ProducerRepository producerRepository;
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private ProducerMapper producerMapper;
    private BalanceMapper balanceMapper;
    private UserValidator userValidator;
    private PasswordEncoder passwordEncoder;

    private ProducerServiceImpl producerService;

    @BeforeEach
    void setUp() {
        producerRepository = mock(ProducerRepository.class);
        authenticationService = mock(AuthenticationService.class);
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        producerMapper = mock(ProducerMapper.class);
        balanceMapper = mock(BalanceMapper.class);
        userValidator = mock(UserValidator.class);
        passwordEncoder = mock(PasswordEncoder.class);

        producerService = new ProducerServiceImpl(
                authenticationService, producerRepository, userRepository,
                userMapper, producerMapper, balanceMapper, passwordEncoder, userValidator
        );
    }

    // Test: register a new producer
    @Test
    void testRegisterProducer() {
        UserCreationRequestDto requestDto = new UserCreationRequestDto("testUser", "Password123");

        Producer savedProducer = Producer.builder()
                .id(1L)
                .username("testUser")
                .registrationDate(LocalDateTime.now())
                .role(Role.PRODUCER)
                .salary(BigDecimal.ZERO)
                .build();

        UserDto expectedDto = new UserDto(1L, "PRODUCER", "testUser", savedProducer.getRegistrationDate(), BigDecimal.ZERO);

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(producerRepository.save(any())).thenReturn(savedProducer);
        when(userMapper.toProducerResponseDto(any())).thenReturn(expectedDto);

        UserDto result = producerService.register(requestDto);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("PRODUCER", result.getRole());
        verify(producerRepository, times(1)).save(any());
    }

    // Test: get balance for the current producer
    @Test
    void testGetBalance() {
        Producer producer = Producer.builder()
                .id(1L)
                .salary(BigDecimal.valueOf(150))
                .build();

        BalanceResponseDto expectedDto = new BalanceResponseDto(1L, BigDecimal.valueOf(150));

        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(balanceMapper.toResponseDto(producer)).thenReturn(expectedDto);

        BalanceResponseDto result = producerService.getBalance();

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150), result.getBalance());
    }

    // Test: get all producers
    @Test
    void testGetAllProducers() {
        List<Producer> producers = List.of(
                Producer.builder().id(1L).username("producer1").build()
        );

        List<UserDto> dtoList = List.of(
                new UserDto(1L, "PRODUCER", "producer1", LocalDateTime.now(), null)
        );

        when(producerRepository.findAll()).thenReturn(producers);
        when(producerMapper.toResponseDto(producers)).thenReturn(dtoList);

        List<UserDto> result = producerService.getAllProducers();

        assertEquals(1, result.size());
        assertEquals("producer1", result.get(0).getUsername());
    }

    // Test: get producer by ID
    @Test
    void testGetProducerById() {
        Producer producer = Producer.builder()
                .id(1L)
                .username("producerX")
                .build();

        UserDto expectedDto = new UserDto(1L, "PRODUCER", "producerX", LocalDateTime.now(), null);

        when(producerRepository.findById(1L)).thenReturn(Optional.of(producer));
        when(producerMapper.toResponseDto(producer)).thenReturn(expectedDto);

        UserDto result = producerService.getProducerById(1L);

        assertNotNull(result);
        assertEquals("producerX", result.getUsername());
    }

    // Test: update producer (change username and password)
    @Test
    void testUpdateProducer() {
        Producer producer = Producer.builder()
                .id(1L)
                .username("oldName")
                .build();

        UserUpdateRequestDto updateDto = new UserUpdateRequestDto("newName", "NewPass123");

        UserDto expectedDto = new UserDto(1L, "PRODUCER", "newName", LocalDateTime.now(), null);

        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(passwordEncoder.encode("NewPass123")).thenReturn("encodedPass");
        when(producerRepository.save(producer)).thenReturn(producer);
        when(userMapper.toProducerResponseDto(producer)).thenReturn(expectedDto);

        UserDto result = producerService.updateProducer(updateDto);

        assertEquals("newName", result.getUsername());
        verify(producerRepository, times(1)).save(producer);
    }

    // Test: delete producer
    @Test
    void testDeleteProducer() {
        Producer producer = Producer.builder()
                .id(1L)
                .username("deleteMe")
                .build();

        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);

        producerService.deleteProducer();

        verify(userValidator, times(1)).validateProducerDeletionRequest(producer, "deleteMe");
        verify(producerRepository, times(1)).delete(producer);
    }

    // Test: update producer ratings based on top 10 tracks
    @Test
    void testUpdateProducerRatings() {
        Producer producer = Producer.builder()
                .id(1L)
                .username("ratingProducer")
                .build();

        Set<ProducerTrackInfo> trackInfos = new HashSet<>();
        for (int i = 1; i <= 12; i++) {
            Track track = Track.builder()
                    .id((long) i)
                    .rating(1000 + i * 10)
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .build();

            trackInfos.add(ProducerTrackInfo.builder()
                    .track(track)
                    .producer(producer)
                    .build());
        }
        producer.setTracks(trackInfos);

        when(producerRepository.findAll()).thenReturn(List.of(producer));

        producerService.updateProducerRatings();

        ArgumentCaptor<Producer> captor = ArgumentCaptor.forClass(Producer.class);
        verify(producerRepository, atLeastOnce()).save(captor.capture());

        Producer savedProducer = captor.getValue();
        double expectedAvg = (1010 + 1020 + 1030 + 1040 + 1050 + 1060 + 1070 + 1080 + 1090 + 1100) / 10.0;

        assertEquals(expectedAvg, savedProducer.getRating(), 0.001);
    }
}

