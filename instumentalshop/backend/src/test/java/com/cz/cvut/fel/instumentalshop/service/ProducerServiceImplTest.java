package com.cz.cvut.fel.instumentalshop.service;


import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.producer.in.TopProducerRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.impl.ProducerServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerServiceImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ProducerRepository producerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ProducerMapper producerMapper;
    @Mock
    private BalanceMapper balanceMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private ProducerServiceImpl service;

    private Producer producer1;
    private Producer producer2;

    @BeforeEach
    void setUp() {
        producer1 = Producer.builder()
                .id(1L)
                .username("alice")
                .rating(4.5)
                .build();
        producer2 = Producer.builder()
                .id(2L)
                .username("bob")
                .rating(3.0)
                .build();
    }

    @Test
    void testGetTopProducers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Producer> page = new PageImpl<>(List.of(producer1, producer2), pageable, 2);
        when(producerRepository.findAll(pageable)).thenReturn(page);

        // Act
        List<TopProducerRequestDto> result = service.getTopProducers(2);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals(4.5, result.get(0).getRating());
    }

    @Test
    void testGetBalance() {
        // Arrange
        Producer p = Producer.builder().id(5L).balance(BigDecimal.valueOf(123.45)).build();
        BalanceResponseDto dto = new BalanceResponseDto(5L, BigDecimal.valueOf(123.45));
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(p);
        when(balanceMapper.toResponseDto(p)).thenReturn(dto);

        // Act
        BalanceResponseDto result = service.getBalance();

        // Assert
        assertSame(dto, result);
    }

    @Test
    void testGetCustomerPurchaseStatisticsForProducer() {
        // Arrange
        Producer p = Producer.builder().id(7L).build();
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(p);
        TypedQuery<ProducerPurchaseStatisticDto> query = mock(TypedQuery.class);
        when(entityManager.createNamedQuery(eq("Customer.getCustomerPurchaseInfoForProducer"), eq(ProducerPurchaseStatisticDto.class))).thenReturn(query);
        ProducerPurchaseStatisticDto stat = new ProducerPurchaseStatisticDto(10L, "cust1", 5L, LocalDateTime.now());
        when(query.setParameter(eq("producerId"), eq(7L))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(stat));

        // Act
        List<ProducerPurchaseStatisticDto> stats = service.getCustomerPurchaseStatisticsForProducer();

        // Assert
        assertEquals(1, stats.size());
        assertSame(stat, stats.get(0));
    }

    @Test
    void testGetAllProducers() {
        // Arrange
        List<Producer> list = List.of(producer1);
        List<UserDto> mapped = List.of(new UserDto());
        when(producerRepository.findAll()).thenReturn(list);
        when(producerMapper.toResponseDto(list)).thenReturn(mapped);

        // Act
        List<UserDto> result = service.getAllProducers();

        // Assert
        assertSame(mapped, result);
    }

    @Test
    void testGetProducerById_Found() {
        // Arrange
        UserDto dto = new UserDto();
        when(producerRepository.findById(1L)).thenReturn(Optional.of(producer1));
        when(producerMapper.toResponseDto(producer1)).thenReturn(dto);

        // Act
        UserDto result = service.getProducerById(1L);

        // Assert
        assertSame(dto, result);
    }

    @Test
    void testGetProducerById_NotFound() {
        // Arrange
        when(producerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getProducerById(99L));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testUpdateProducer() {
        // Arrange
        Producer p = Producer.builder().id(8L).username("old").build();
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(p);
        UserUpdateRequestDto req = new UserUpdateRequestDto("newName", "pass");
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(producerRepository.save(p)).thenReturn(p);
        UserDto mapped = new UserDto();
        when(producerMapper.toResponseDto(p)).thenReturn(mapped);

        // Act
        UserDto result = service.updateProducer(req);

        // Assert
        assertEquals("newName", p.getUsername());
        assertEquals("encoded", p.getPassword());
        assertSame(mapped, result);
    }

    @Test
    void testDeleteProducer() {
        // Arrange
        Producer p = Producer.builder().id(9L).username("u").build();
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(p);
        doNothing().when(userValidator).validateProducerDeletionRequest(p, "u");

        // Act
        service.deleteProducer();

        // Assert
        verify(producerRepository).delete(p);
    }

    @Test
    void testUpdateProducerRatings() {
        // Arrange
        Producer p = new Producer();
        p.setId(3L);
        Track t1 = new Track();
        t1.setRating(BigDecimal.valueOf(5.0));
        t1.setCreatedAt(LocalDateTime.now().minusDays(1));
        t1.setProducer(p);
        Track t2 = new Track();
        t2.setRating(BigDecimal.valueOf(1.0));
        t2.setCreatedAt(LocalDateTime.now().minusDays(2));
        t2.setProducer(p);
        p.setTracks(Set.of(t1, t2));
        when(producerRepository.findAll()).thenReturn(List.of(p));

        // Act
        service.updateProducerRatings();

        // Assert: avg of [5.0,1.0] = 3.0
        assertEquals(3.0, p.getRating(), 0.001);
        verify(producerRepository).save(p);
    }
}

