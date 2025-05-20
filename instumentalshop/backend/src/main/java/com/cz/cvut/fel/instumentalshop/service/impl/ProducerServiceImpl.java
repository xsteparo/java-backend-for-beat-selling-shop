package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
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
import com.cz.cvut.fel.instumentalshop.service.ProducerService;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementace služeb pro producenty.
 */
@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final EntityManager entityManager;          // Konstruktorová injekce
    private final AuthenticationService authenticationService;
    private final ProducerRepository producerRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProducerMapper producerMapper;
    private final BalanceMapper balanceMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final PurchasedLicenceRepository purchasedLicenceRepository;

    /**
     * FR22 - Vrací seznam top producentů podle hodnocení.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TopProducerRequestDto> getTopProducers(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rating"));
        return producerRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(p -> TopProducerRequestDto.builder()
                        .id(p.getId())
                        .username(p.getUsername())
                        .rating(p.getRating())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * FR09 (přesunuto do AuthController) - Odstraněno
     */
    @Override
    @Transactional
    public UserDto register(UserCreationRequestDto requestDto) {
        userValidator.validateUserCreationRequest(userRepository, requestDto.getUsername());
        Producer producer = buildProducer(requestDto);
        Producer saved = producerRepository.save(producer);
        return producerMapper.toResponseDto(saved);
    }

    /**
     * FR10 - Získání zůstatku účtu producenta.
     */
    @Override
    @Transactional
    public BalanceResponseDto getBalance() {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();
        return balanceMapper.toResponseDto(producer);
    }

    /**
     * FR10 - Statistiky nákupů klientů pro producenta.
     */
    @Override
    @Transactional
    public List<ProducerPurchaseStatisticDto> getCustomerPurchaseStatisticsForProducer() {
        Long producerId = authenticationService.getRequestingProducerFromSecurityContext().getId();
        return purchasedLicenceRepository.findCustomerStatsByProducerId(producerId);
    }

    /**
     * FR13 - Výpis všech producentů.
     */
    @Override
    @Transactional
    public List<UserDto> getAllProducers() {
        return producerMapper.toResponseDto(producerRepository.findAll());
    }

    /**
     * FR11 - Zobrazení profilu producenta podle ID.
     */
    @Override
    @Transactional
    public UserDto getProducerById(Long id) {
        return producerRepository.findById(id)
                .map(producerMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Producer with id " + id + " not found"));
    }

    /**
     * FR12 - Úprava profilu producenta.
     */
    @Override
    @Transactional
    public UserDto updateProducer(UserUpdateRequestDto requestDto) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();
        if (requestDto.getUsername() != null) {
            producer.setUsername(requestDto.getUsername());
        }
        if (requestDto.getPassword() != null) {
            producer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        Producer updated = producerRepository.save(producer);
        return producerMapper.toResponseDto(updated);
    }

    /**
     * FR? - Smazání účtu producenta.
     */
    @Override
    @Transactional
    public void deleteProducer() {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();
        userValidator.validateProducerDeletionRequest(producer, producer.getUsername());
        producerRepository.delete(producer);
    }

    /**
     * Naplánovaný update ratingu producentů.
     */
    @Override
    @Transactional
    public void updateProducerRatings() {
        List<Producer> all = producerRepository.findAll();
        for (Producer p : all) {
            double avg = p.getTracks().stream()
//                    .map(ProducerTrackInfo::getTrack)
                    .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                    .limit(10)
                    .mapToDouble(track -> track.getRating().doubleValue())
                    .average().orElse(0.0);
            p.setRating(avg);
            producerRepository.save(p);
        }
    }

    /*-- Pomocné metody --*/

    private Producer buildProducer(UserCreationRequestDto dto) {
        return Producer.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .registrationDate(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .role(dto.getRole())
                .build();
    }
}
