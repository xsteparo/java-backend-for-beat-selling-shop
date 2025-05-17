package com.cz.cvut.fel.instumentalshop.service.newTests;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.impl.licence.LicencePurchaseServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicencePurchaseServiceImplTest {

    @Mock
    private EntityManager em;
    @Mock
    private AuthenticationService auth;
    @Mock
    private LicenceTemplateRepository tplRepo;
    @Mock
    private PurchasedLicenceRepository licRepo;
    @Mock
    private TrackRepository trackRepo;
    @Mock
    private ProducerTrackInfoRepository ptiRepo;
    @Mock
    private LicenceValidator validator;

    @InjectMocks
    private LicencePurchaseServiceImpl service;

    private Customer customer;
    private Producer producer;
    private Track track;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(10L);
        customer.setBalance(BigDecimal.valueOf(150));
        customer.setRole(Role.CUSTOMER);

        producer = new Producer();
        producer.setId(20L);
        producer.setUsername("prodName");
        producer.setRole(Role.PRODUCER);

        track = new Track();
        track.setId(1L);
        ProducerTrackInfo leadInfo = new ProducerTrackInfo();
        leadInfo.setTrack(track);
        leadInfo.setProducer(producer);
        leadInfo.setOwnsPublishingTrack(true);
    }

    @Test
    void testPurchaseLicence_Success() {
        PurchaseRequestDto dto = new PurchaseRequestDto();
        dto.setLicenceType(LicenceType.NON_EXCLUSIVE);

        when(auth.getRequestingCustomerFromSecurityContext()).thenReturn(customer);
        when(trackRepo.findTrackById(1L)).thenReturn(Optional.of(track));
        doNothing().when(validator).validatePurchaseCreateRequest(customer, track, LicenceType.NON_EXCLUSIVE);

        var template = new LicenceTemplate();
        template.setLicenceType(LicenceType.NON_EXCLUSIVE);
        template.setPrice(BigDecimal.valueOf(100));
        template.setValidityPeriodDays(7);
        template.setTrack(track);
        template.setAvailablePlatforms(List.of(Platform.SPOTIFY));
        when(tplRepo.findByTrackAndLicenceType(track, LicenceType.NON_EXCLUSIVE))
                .thenReturn(Optional.of(template));

        doAnswer(inv -> {
            PurchasedLicence arg = inv.getArgument(0);
            arg.setId(42L);
            return arg;
        }).when(licRepo).save(any(PurchasedLicence.class));

        when(ptiRepo.findByTrackId(1L)).thenReturn(List.of());

        PurchaseDto result = service.purchaseLicence(dto, 1L);

        assertNotNull(result);
        assertEquals(42L, result.getPurchaseId());
        assertEquals(LicenceType.NON_EXCLUSIVE, result.getLicenceType());
        assertEquals(BigDecimal.valueOf(50), customer.getBalance());
        verify(ptiRepo).findByTrackId(1L);
    }

    @Test
    void testGetPurchasedLicenceById_Success() {
        var lic = new PurchasedLicence();
        lic.setId(5L);
        var tpl = new LicenceTemplate();
        tpl.setLicenceType(LicenceType.EXCLUSIVE);
        tpl.setPrice(BigDecimal.valueOf(200));
        tpl.setValidityPeriodDays(14);
        var t = new Track();
        t.setId(10L);
        tpl.setTrack(t);
        tpl.setAvailablePlatforms(List.of(Platform.YOUTUBE));
        lic.setLicenceTemplate(tpl);
        lic.setProducer(producer);

        when(licRepo.findById(5L)).thenReturn(Optional.of(lic));
        when(auth.getRequestingUserFromSecurityContext()).thenReturn(customer);
        doNothing().when(validator).validatePurchasedLicenceGetRequest(customer, lic);

        PurchaseDto dto = service.getPurchasedLicenceById(5L);

        assertEquals(5L, dto.getPurchaseId());
        assertEquals(LicenceType.EXCLUSIVE, dto.getLicenceType());
        assertEquals(BigDecimal.valueOf(200), dto.getPrice());
        assertEquals(14, dto.getValidityPeriodDays());
        assertEquals(10L, dto.getTrackId());
        assertEquals("prodName", dto.getProducer().getUsername());
    }

    @Test
    void testGetAllPurchasedLicences_AsCustomer() {
        var p1 = new PurchasedLicence();
        p1.setId(1L);
        var tpl = new LicenceTemplate();
        tpl.setLicenceType(LicenceType.NON_EXCLUSIVE);
        tpl.setPrice(BigDecimal.valueOf(100));
        tpl.setValidityPeriodDays(7);
        var t = new Track();
        t.setId(10L);
        tpl.setTrack(t);
        tpl.setAvailablePlatforms(List.of(Platform.TIKTOK));
        p1.setLicenceTemplate(tpl);
        p1.setProducer(producer);

        when(auth.getRequestingUserFromSecurityContext()).thenReturn(customer);
        when(licRepo.findByCustomerId(10L)).thenReturn(List.of(p1));

        var list = service.getAllPurchasedLicences();

        assertEquals(1, list.size());
        var dto = list.get(0);
        assertEquals(1L, dto.getPurchaseId());
        assertEquals(LicenceType.NON_EXCLUSIVE, dto.getLicenceType());
        assertEquals(7, dto.getValidityPeriodDays());
        assertEquals(10L, dto.getTrackId());
        assertEquals("prodName", dto.getProducer().getUsername());
    }

    @Test
    void testGetAllPurchasedLicences_AsProducer() {
        var p2 = new PurchasedLicence();
        p2.setId(2L);
        var tpl = new LicenceTemplate();
        tpl.setLicenceType(LicenceType.EXCLUSIVE);
        tpl.setPrice(BigDecimal.valueOf(150));
        tpl.setValidityPeriodDays(null);
        var t2 = new Track();
        t2.setId(20L);
        tpl.setTrack(t2);
        tpl.setAvailablePlatforms(List.of(Platform.APPLE_MUSIC));
        p2.setLicenceTemplate(tpl);
        p2.setProducer(producer);

        when(auth.getRequestingUserFromSecurityContext()).thenReturn(producer);
        when(licRepo.findForProducerByProducerId(20L)).thenReturn(List.of(p2));

        var list = service.getAllPurchasedLicences();

        assertEquals(1, list.size());
        var dto = list.get(0);
        assertEquals(2L, dto.getPurchaseId());
        assertEquals(LicenceType.EXCLUSIVE, dto.getLicenceType());
        assertNull(dto.getValidityPeriodDays());
        assertEquals(20L, dto.getTrackId());
        assertEquals("prodName", dto.getProducer().getUsername());
    }

    @Test
    void testGetProducerIncomesByTracks() {
        var incomeDto = new ProducerIncomeDto(1L, "Track1", BigDecimal.valueOf(300));
        @SuppressWarnings("unchecked")
        var query = mock(TypedQuery.class);
        when(auth.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(em.createNamedQuery(eq("ProducerTrackInfo.findProducerIncomeByTracks"), eq(ProducerIncomeDto.class)))
                .thenReturn(query);
        when(query.setParameter(eq("producerId"), eq(20L))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(incomeDto));

        var result = service.getProducerIncomesByTracks();

        assertEquals(1, result.size());
        assertSame(incomeDto, result.get(0));
    }
}


