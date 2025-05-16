package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.LicencePdfServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class LicencePdfServiceTest {


    private PurchasedLicenceRepository purchasedLicenceRepository;
    private LicensePdfService licencePdfService;

    @BeforeEach
    void setUp() {
        purchasedLicenceRepository = Mockito.mock(PurchasedLicenceRepository.class);
        licencePdfService = new LicencePdfServiceImpl(purchasedLicenceRepository);
    }

    @Test
    void testGenerateLicencePdf() {
        // Arrange
        Long customerId = 1L;
        Long purchaseId = 10L;

        Customer customer = Customer.builder()
                .id(customerId)
                .username("testUser")
                .build();

        Track track = Track.builder()
                .id(2L)
                .name("Test Track")
                .build();

        LicenceTemplate template = LicenceTemplate.builder()
                .id(3L)
                .licenceType(LicenceType.STANDARD)
                .price(BigDecimal.TEN)
                .build();

        PurchasedLicence licence = PurchasedLicence.builder()
                .id(purchaseId)
                .customer(customer)
                .track(track)
                .licenceTemplate(template)
                .purchaseDate(LocalDateTime.now())
                .build();

        when(purchasedLicenceRepository.findById(purchaseId))
                .thenReturn(Optional.of(licence));

        // Act
        byte[] pdfBytes = licencePdfService.generateLicencePdf(customerId, purchaseId);

        // Assert
        assertNotNull(pdfBytes, "PDF should not be null");
        assertTrue(pdfBytes.length > 0, "PDF content should not be empty");
    }

    @Test
    void testGenerateLicencePdf_AccessDenied() {
        Long customerId = 1L;
        Long purchaseId = 10L;

        Customer customer = Customer.builder()
                .id(99L)
                .username("anotherUser")
                .build();

        PurchasedLicence licence = PurchasedLicence.builder()
                .id(purchaseId)
                .customer(customer)
                .build();

        when(purchasedLicenceRepository.findById(purchaseId))
                .thenReturn(Optional.of(licence));

        assertThrows(AccessDeniedException.class, () -> {
            licencePdfService.generateLicencePdf(customerId, purchaseId);
        });
    }

    @Test
    void testGenerateLicencePdf_LicenceNotFound() {
        Long customerId = 1L;
        Long purchaseId = 10L;

        when(purchasedLicenceRepository.findById(purchaseId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            licencePdfService.generateLicencePdf(customerId, purchaseId);
        });
    }
}
