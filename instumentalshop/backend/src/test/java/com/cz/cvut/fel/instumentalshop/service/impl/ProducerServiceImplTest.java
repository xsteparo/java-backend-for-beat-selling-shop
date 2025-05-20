package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;

import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.producer.in.TopProducerRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProducerServiceImplTest {

    @Mock private EntityManager entityManager;
    @Mock private AuthenticationService authService;
    @Mock private ProducerRepository producerRepo;
    @Mock private UserRepository userRepo;
    @Mock private UserMapper userMapper;
    @Mock private ProducerMapper producerMapper;
    @Mock private BalanceMapper balanceMapper;
    @Mock private UserValidator userValidator;
    @Mock private PurchasedLicenceRepository purchasedLicenceRepo;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private ProducerServiceImpl producerService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTopProducers() {
        Producer p = new Producer();
        p.setId(1L);
        p.setUsername("top");
        p.setRating(4.5);
        when(producerRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(p)));

        List<TopProducerRequestDto> result = producerService.getTopProducers(5);

        assertEquals(1, result.size());
        assertEquals("top", result.get(0).getUsername());
    }

    @Test
    void testRegisterProducer() {
        UserCreationRequestDto dto = new UserCreationRequestDto();
        dto.setUsername("producer");
        dto.setPassword("pass");
        dto.setRole(Role.PRODUCER);

        Producer saved = new Producer();
        saved.setId(1L);

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(producerRepo.save(any())).thenReturn(saved);
        when(producerMapper.toResponseDto(saved)).thenReturn(new UserDto());

        UserDto result = producerService.register(dto);
        assertNotNull(result);
        verify(userValidator).validateUserCreationRequest(userRepo, "producer");
    }

    @Test
    void testGetBalance() {
        Producer p = new Producer();
        p.setBalance(BigDecimal.TEN);
        when(authService.getRequestingProducerFromSecurityContext()).thenReturn(p);
        when(balanceMapper.toResponseDto(p)).thenReturn(new BalanceResponseDto(10L, BigDecimal.TEN));

        BalanceResponseDto result = producerService.getBalance();
        assertEquals(BigDecimal.TEN, result.getBalance());
    }

    @Test
    void testGetAllProducers() {
        when(producerRepo.findAll()).thenReturn(Collections.emptyList());
        when(producerMapper.toResponseDto(Collections.emptyList())).thenReturn(Collections.emptyList());
        List<UserDto> result = producerService.getAllProducers();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProducerById_found() {
        Producer producer = new Producer();
        producer.setId(1L);
        when(producerRepo.findById(1L)).thenReturn(Optional.of(producer));
        when(producerMapper.toResponseDto(producer)).thenReturn(new UserDto());

        UserDto result = producerService.getProducerById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetProducerById_notFound() {
        when(producerRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> producerService.getProducerById(1L));
    }

    @Test
    void testUpdateProducer() {
        Producer producer = new Producer();
        producer.setUsername("old");

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setUsername("new");

        when(authService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(producerRepo.save(any())).thenReturn(producer);
        when(producerMapper.toResponseDto(producer)).thenReturn(new UserDto());

        UserDto result = producerService.updateProducer(dto);
        assertNotNull(result);
        assertEquals("new", producer.getUsername());
    }

    @Test
    void testDeleteProducer() {
        Producer p = new Producer();
        p.setUsername("toDelete");
        when(authService.getRequestingProducerFromSecurityContext()).thenReturn(p);

        producerService.deleteProducer();
        verify(userValidator).validateProducerDeletionRequest(p, "toDelete");
        verify(producerRepo).delete(p);
    }

    @Test
    void testGetCustomerPurchaseStats() {
        Producer p = new Producer();
        p.setId(42L);
        when(authService.getRequestingProducerFromSecurityContext()).thenReturn(p);
        when(purchasedLicenceRepo.findCustomerStatsByProducerId(42L)).thenReturn(Collections.emptyList());
        List<ProducerPurchaseStatisticDto> result = producerService.getCustomerPurchaseStatisticsForProducer();
        assertTrue(result.isEmpty());
    }
}