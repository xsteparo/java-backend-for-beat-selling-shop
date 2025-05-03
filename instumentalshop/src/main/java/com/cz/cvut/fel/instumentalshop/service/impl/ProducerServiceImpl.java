package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.ProducerService;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AuthenticationService authenticationService;

    private final ProducerRepository producerRepository;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ProducerMapper producerMapper;

    private final BalanceMapper balanceMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;

    @Override
    @Transactional
    public UserDto register(UserCreationRequestDto requestDto) {
        userValidator.validateUserCreationRequest(userRepository, requestDto.getUsername());

        Producer producer = buildProducer(requestDto);

        producer = producerRepository.save(producer);

        return userMapper.toProducerResponseDto(producer);
    }

    @Override
    @Transactional
    public BalanceResponseDto getBalance() {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        return balanceMapper.toResponseDto(producer);
    }

    @Override
    @Transactional
    public List<ProducerPurchaseStatisticDto> getCustomerPurchaseStatisticsForProducer() {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        TypedQuery<ProducerPurchaseStatisticDto> query = entityManager.createNamedQuery("Customer.getCustomerPurchaseInfoForProducer", ProducerPurchaseStatisticDto.class);
        query.setParameter("producerId", producer.getId());
        return query.getResultList();
    }

    @Override
    @Transactional
    public List<UserDto> getAllProducers() {
        return producerMapper.toResponseDto(producerRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto getProducerById(Long id) {
        return producerRepository.findById(id)
                .map(producerMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Producer with id " + id + " not found"));
    }

    @Override
    @Transactional
    public UserDto updateProducer(UserUpdateRequestDto requestDto) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        updateProducerEntity(producer, requestDto);

        producerRepository.save(producer);

        return userMapper.toProducerResponseDto(producer);
    }

    @Override
    @Transactional
    public void deleteProducer() {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        userValidator.validateProducerDeletionRequest(producer, producer.getUsername());

        producerRepository.delete(producer);
    }


    private Producer buildProducer(UserCreationRequestDto requestDto) {
        return Producer.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .registrationDate(LocalDateTime.now())
                .salary(BigDecimal.ZERO)
                .role(Role.PRODUCER)
                .build();
    }

    private void updateProducerEntity(Producer producer, UserUpdateRequestDto requestDto) {
        if (requestDto.getUsername() != null) {
            producer.setUsername(requestDto.getUsername());
        }
        if (requestDto.getPassword() != null) {
            producer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
    }

}
