package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.TestDataGenerator;
import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.exception.NotEnoughBalanceException;
import com.cz.cvut.fel.instumentalshop.repository.*;
import com.cz.cvut.fel.instumentalshop.service.impl.licence.LicencePurchaseServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicencePurchaseServiceTest {

    private static final BigDecimal INITIAL_CUSTOMER_BALANCE = new BigDecimal("100.00");

    private static final BigDecimal LICENCE_PRICE = new BigDecimal("50.00");

    private static final Long TRACK_ID = 1L;

    private static final Long LICENCE_TEMPLATE_ID = 1L;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LicenceTemplateRepository licenceTemplateRepository;

    @Mock
    private PurchasedLicenceRepository purchasedLicenceRepository;

    @Mock
    private ProducerRepository producerRepository;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ProducerTrackInfoRepository producerTrackInfoRepository;

    @Mock
    private LicenceValidator licenceValidator;

    @InjectMocks
    private LicencePurchaseServiceImpl licencePurchaseService;

    private Customer customer;

    private Track track;

    private LicenceTemplate licenceTemplate;

    private PurchaseRequestDto requestDto;

    private Producer producer1, producer2;

    private List<ProducerTrackInfo> producerTrackInfos;

    @BeforeEach
    void setUp() {
        customer = TestDataGenerator.createCustomer(1L, INITIAL_CUSTOMER_BALANCE);
        track = TestDataGenerator.createTrack(TRACK_ID, "Sample Track");
        licenceTemplate = TestDataGenerator.createLicenceTemplate(LICENCE_TEMPLATE_ID, track, LICENCE_PRICE, LicenceType.STANDARD, 180);
        requestDto = new PurchaseRequestDto();
        requestDto.setLicenceType(LicenceType.STANDARD);
        setupProducers();
    }

    private void setupProducers() {
        producer1 = TestDataGenerator.createProducer(1L, "Producer1");
        producer2 = TestDataGenerator.createProducer(2L, "Producer2");
        producerTrackInfos = Arrays.asList(
                TestDataGenerator.createProducerTrackInfo(producer1, track, new BigDecimal("40")),
                TestDataGenerator.createProducerTrackInfo(producer2, track, new BigDecimal("60"))
        );
    }

    @Test
    void testPurchaseLicenceCreationRequest() {
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);
        when(trackRepository.findTrackById(TRACK_ID)).thenReturn(Optional.of(track));
        when(licenceTemplateRepository.findByTrackAndLicenceType(track, LicenceType.STANDARD)).thenReturn(Optional.of(licenceTemplate));
        when(producerTrackInfoRepository.findByTrackId(TRACK_ID)).thenReturn(producerTrackInfos);

        PurchasedLicence purchasedLicence = new PurchasedLicence();
        purchasedLicence.setId(1L);
        purchasedLicence.setCustomer(customer);
        purchasedLicence.setTrack(track);
        purchasedLicence.setLicenceTemplate(licenceTemplate);
        purchasedLicence.setPurchaseDate(LocalDateTime.now());
        purchasedLicence.setProducers(Arrays.asList(producer1, producer2));
        when(purchasedLicenceRepository.save(any(PurchasedLicence.class))).thenReturn(purchasedLicence);

        PurchaseDto result = licencePurchaseService.purchaseLicence(requestDto, TRACK_ID);

        assertNotNull(result);
        assertEquals(TRACK_ID, result.getTrackId());
        assertEquals(LICENCE_PRICE, result.getPrice());
        assertEquals(INITIAL_CUSTOMER_BALANCE.subtract(LICENCE_PRICE), customer.getBalance());

        verify(licenceValidator).validatePurchaseCreateRequest(customer, track, requestDto.getLicenceType());
        verify(producerRepository, times(4)).save(any(Producer.class));
        verify(purchasedLicenceRepository).save(any(PurchasedLicence.class));
        verify(producerTrackInfoRepository, atLeastOnce()).findByTrackId(TRACK_ID);
        verify(trackRepository).findTrackById(TRACK_ID);
        verify(licenceTemplateRepository).findByTrackAndLicenceType(track, LicenceType.STANDARD);
    }

    @Test
    void testPurchaseLicenceCreationRequest_ThrowsNotEnoughBalanceException() {
        customer.setBalance(new BigDecimal("10.00"));
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);
        when(trackRepository.findTrackById(TRACK_ID)).thenReturn(Optional.of(track));

        doThrow(new NotEnoughBalanceException("Licence already exists"))
                .when(licenceValidator).validatePurchaseCreateRequest(customer, track, requestDto.getLicenceType());

        assertThrows(NotEnoughBalanceException.class, () -> licencePurchaseService.purchaseLicence(requestDto, TRACK_ID));

        verify(licenceValidator).validatePurchaseCreateRequest(customer, track, requestDto.getLicenceType());
        verify(trackRepository).findTrackById(TRACK_ID);
    }
}