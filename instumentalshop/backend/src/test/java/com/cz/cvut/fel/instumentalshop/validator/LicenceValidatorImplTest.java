package com.cz.cvut.fel.instumentalshop.validator;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.exception.LicenceAlreadyExistsException;
import com.cz.cvut.fel.instumentalshop.exception.NotEnoughBalanceException;
import com.cz.cvut.fel.instumentalshop.repository.LicenceReportRepository;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.util.validator.impl.LicenceValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LicenceValidatorImplTest {

    @Mock
    private ProducerTrackInfoRepository producerTrackInfoRepository;

    @Mock
    private LicenceTemplateRepository licenceTemplateRepository;

    @Mock
    private LicenceReportRepository licenceReportRepository;

    @Mock
    private PurchasedLicenceRepository purchasedLicenceRepository;

    @InjectMocks
    private LicenceValidatorImpl licenceValidator;

    @Test
    void validateTemplateCreationRequest_ValidData_NoExceptionThrown() {
        Producer producer = mock(Producer.class);
        Track track = mock(Track.class);

        LicenceType licenceType = LicenceType.STANDARD;

        ProducerTrackInfo trackInfo = mock(ProducerTrackInfo.class);
        when(trackInfo.getOwnsPublishingTrack()).thenReturn(true);
        when(producerTrackInfoRepository.findByTrackIdAndProducerId(track.getId(), producer.getId()))
                .thenReturn(Optional.of(trackInfo));

        when(licenceTemplateRepository.existsByTrackIdAndLicenceType(track.getId(), licenceType))
                .thenReturn(false);

        assertDoesNotThrow(() ->
                licenceValidator.validateTemplateCreationRequest(producer, track.getId(), licenceType));
    }

    @Test
    void validateTemplateCreationRequest_InvalidLicenceType_ExceptionThrown() {
        Producer producer = mock(Producer.class);
        Track track = mock(Track.class);
        LicenceType licenceType = LicenceType.STANDARD;

        ProducerTrackInfo trackInfo = mock(ProducerTrackInfo.class);
        when(trackInfo.getOwnsPublishingTrack()).thenReturn(true);
        when(producerTrackInfoRepository.findByTrackIdAndProducerId(track.getId(), producer.getId()))
                .thenReturn(Optional.of(trackInfo));

        when(licenceTemplateRepository.existsByTrackIdAndLicenceType(track.getId(), licenceType))
                .thenReturn(true);


        assertThrows(LicenceAlreadyExistsException.class, () ->
                licenceValidator.validateTemplateCreationRequest(producer, track.getId(), licenceType));
    }

    @Test
    void validatePurchaseCreateRequest_ValidData_NoExceptionThrown() {
        Customer customer = mock(Customer.class);
        Track track = mock(Track.class);
        LicenceType licenceType = LicenceType.STANDARD;

        when(customer.getBalance()).thenReturn(new BigDecimal("100.00"));
        LicenceTemplate licenceTemplate = mock(LicenceTemplate.class);
        when(licenceTemplate.getPrice()).thenReturn(new BigDecimal("50.00"));
        when(licenceTemplateRepository.findByTrackAndLicenceType(track, licenceType))
                .thenReturn(Optional.of(licenceTemplate));

        when(track.isExclusiveBought()).thenReturn(false);
        when(track.isAllProducersAgreedForSelling()).thenReturn(true);
        when(licenceTemplateRepository.existsByTrackIdAndLicenceType(track.getId(), licenceType))
                .thenReturn(true);
        when(purchasedLicenceRepository.existsByCustomerIdAndTrackIdAndLicenceTemplate_LicenceType(customer.getId(), track.getId(), licenceType))
                .thenReturn(false);

        assertDoesNotThrow(() -> licenceValidator.validatePurchaseCreateRequest(customer, track, licenceType));
    }

    @Test
    void validatePurchaseCreateRequest_InsufficientBalance_ExceptionThrown() {
        Customer customer = mock(Customer.class);
        Track track = mock(Track.class);
        LicenceType licenceType = LicenceType.STANDARD;

        when(customer.getBalance()).thenReturn(new BigDecimal("10.00"));
        when(track.isExclusiveBought()).thenReturn(false);
        when(track.isAllProducersAgreedForSelling()).thenReturn(true);
        when(licenceTemplateRepository.existsByTrackIdAndLicenceType(track.getId(), licenceType))
                .thenReturn(true);

        LicenceTemplate licenceTemplate = mock(LicenceTemplate.class);
        when(licenceTemplate.getPrice()).thenReturn(new BigDecimal("50.00"));
        when(licenceTemplateRepository.findByTrackAndLicenceType(track, licenceType))
                .thenReturn(Optional.of(licenceTemplate));

        assertThrows(NotEnoughBalanceException.class, () ->
                licenceValidator.validatePurchaseCreateRequest(customer, track, licenceType));
    }

}
