package com.cz.cvut.fel.instumentalshop.config;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.domain.enums.KeyType;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.repository.CustomerRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.LicencePurchaseService;
import com.cz.cvut.fel.instumentalshop.service.TrackService;
import com.cz.cvut.fel.instumentalshop.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;


@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthenticationService authService;
    private final CustomUserDetailsService userDetailsService;
    private final TrackService trackService;
    private final LicencePurchaseService purchaseService;
    private final TrackRepository trackRepo;
    private final CustomerRepository customerRepo;

    @Override
    public void run(String... args) throws Exception {
        // 1) Зарегистрировать трёх пользователей
        authService.register(new UserCreationRequestDto(
                "customer1", "c1@example.com", "Test1234", null, Role.CUSTOMER));
        authService.register(new UserCreationRequestDto(
                "producer1", "p1@example.com", "Test1234", null, Role.PRODUCER));
        authService.register(new UserCreationRequestDto(
                "admin1", "a1@example.com", "Test1234", null, Role.ADMIN));


        Customer customer = customerRepo.findByUsername("customer1")
                .orElseThrow(() -> new IllegalStateException("Customer не найден"));
        customer.setBalance(new BigDecimal("1000.00"));
        customerRepo.save(customer);

        // --- эмулируем login producer1 ---
        UserDetails producerDetails =
                userDetailsService.loadUserByUsername("producer1");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        producerDetails,
                        null,
                        producerDetails.getAuthorities()
                )
        );

        // 2) Dummy-файл для всех лицензий
        MultipartFile dummyFile = new MockMultipartFile(
                "file", "beat.mp3", "audio/mpeg", new byte[0]
        );

        // 3) Создать 5 треков (single-owner: mainProducerPercentage=100, producerShares=empty)
        for (int i = 1; i <= 5; i++) {
            TrackRequestDto dto = TrackRequestDto.builder()
                    .name("Beat #" + i)
                    .genreType(GenreType.HIPHOP)
                    .bpm(120)
                    .key(KeyType.A.name())
                    .price(100)

                    .nonExclusiveFile(dummyFile)
                    .premiumFile(dummyFile)
                    .exclusiveFile(dummyFile)
                    .build();
            trackService.createTrack(dto);
        }

        // убрать producer1 из контекста
        SecurityContextHolder.clearContext();

        // --- эмулируем login customer1 ---
        UserDetails customerDetails =
                userDetailsService.loadUserByUsername("customer1");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        customerDetails,
                        null,
                        customerDetails.getAuthorities()
                )
        );

        // 4) Покупки: первые два — Non-Exclusive, третий — Premium
        var allTracks = trackRepo.findAll();
        for (int idx = 0; idx < 2 && idx < allTracks.size(); idx++) {
            purchaseService.purchaseLicence(
                    new PurchaseRequestDto(LicenceType.NON_EXCLUSIVE),
                    allTracks.get(idx).getId()
            );
        }
        if (allTracks.size() >= 3) {
            purchaseService.purchaseLicence(
                    new PurchaseRequestDto(LicenceType.PREMIUM),
                    allTracks.get(2).getId()
            );
        }

        // очистить контекст
        SecurityContextHolder.clearContext();
    }
}