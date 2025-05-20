package com.cz.cvut.fel.instumentalshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.domain.enums.KeyType;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.impl.licence.LicencePurchaseServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {LicencePurchaseServiceImpl.class})
@ExtendWith(SpringExtension.class)
class LicencePurchaseServiceImplTest {
    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private LicencePurchaseServiceImpl licencePurchaseServiceImpl;

    @MockBean
    private LicenceTemplateRepository licenceTemplateRepository;

    @MockBean
    private LicenceValidator licenceValidator;

    @MockBean
    private ProducerRepository producerRepository;

    @MockBean
    private PurchasedLicenceRepository purchasedLicenceRepository;

    @MockBean
    private TrackRepository trackRepository;

    /**
     * Test {@link LicencePurchaseServiceImpl#purchaseLicence(PurchaseRequestDto, Long)}.
     * <ul>
     *   <li>Given {@link LicenceTemplateRepository}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#purchaseLicence(PurchaseRequestDto, Long)}
     */
    @Test
    @DisplayName("Test purchaseLicence(PurchaseRequestDto, Long); given LicenceTemplateRepository")
    void testPurchaseLicence_givenLicenceTemplateRepository() {
        // Arrange
        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

        Producer producer = new Producer();
        producer.setAvatarUrl("https://example.org/example");
        producer.setBalance(new BigDecimal("2.3"));
        producer.setBio("Bio");
        producer.setEmail("jane.doe@example.org");
        producer.setId(1L);
        producer.setLicenceReports(new ArrayList<>());
        producer.setPassword("iloveyou");
        producer.setRating(10.0d);
        producer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer.setRole(Role.PRODUCER);
        producer.setSoldLicences(new ArrayList<>());
        producer.setTracks(new HashSet<>());
        producer.setUsername("janedoe");

        Track track = new Track();
        track.setBpm(1);
        track.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track.setGenre(GenreType.HYPERPOP);
        track.setId(1L);
        track.setKeyType(KeyType.C);
        track.setLastRatingDelta(0.5d);
        track.setLicenceTemplates(new ArrayList<>());
        track.setLikes(1);
        track.setName("Name");
        track.setPlays(1);
        track.setProducer(producer);
        track.setPurchasedLicence(new ArrayList<>());
        track.setRating(new BigDecimal("2.3"));
        track.setUrlExclusive("https://example.org/example");
        track.setUrlNonExclusive("https://example.org/example");
        track.setUrlPremium("https://example.org/example");
        Optional<Track> ofResult = Optional.of(track);
        when(trackRepository.findTrackById(Mockito.<Long>any())).thenReturn(ofResult);
        doThrow(new AccessDeniedException("Msg")).when(licenceValidator)
                .validatePurchaseCreateRequest(Mockito.<Customer>any(), Mockito.<Track>any(), Mockito.<LicenceType>any());

        // Act and Assert
        assertThrows(AccessDeniedException.class,
                () -> licencePurchaseServiceImpl.purchaseLicence(new PurchaseRequestDto(LicenceType.NON_EXCLUSIVE), 1L));
        verify(trackRepository).findTrackById(eq(1L));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(licenceValidator).validatePurchaseCreateRequest(isA(Customer.class), isA(Track.class),
                eq(LicenceType.NON_EXCLUSIVE));
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#purchaseLicence(PurchaseRequestDto, Long)}.
     * <ul>
     *   <li>Then calls {@link LicenceTemplateRepository#findByTrackAndLicenceType(Track, LicenceType)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#purchaseLicence(PurchaseRequestDto, Long)}
     */
    @Test
    @DisplayName("Test purchaseLicence(PurchaseRequestDto, Long); then calls findByTrackAndLicenceType(Track, LicenceType)")
    void testPurchaseLicence_thenCallsFindByTrackAndLicenceType() {
        // Arrange
        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

        Producer producer = new Producer();
        producer.setAvatarUrl("https://example.org/example");
        producer.setBalance(new BigDecimal("2.3"));
        producer.setBio("Bio");
        producer.setEmail("jane.doe@example.org");
        producer.setId(1L);
        producer.setLicenceReports(new ArrayList<>());
        producer.setPassword("iloveyou");
        producer.setRating(10.0d);
        producer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer.setRole(Role.PRODUCER);
        producer.setSoldLicences(new ArrayList<>());
        producer.setTracks(new HashSet<>());
        producer.setUsername("janedoe");

        Track track = new Track();
        track.setBpm(1);
        track.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track.setGenre(GenreType.HYPERPOP);
        track.setId(1L);
        track.setKeyType(KeyType.C);
        track.setLastRatingDelta(0.5d);
        track.setLicenceTemplates(new ArrayList<>());
        track.setLikes(1);
        track.setName("Name");
        track.setPlays(1);
        track.setProducer(producer);
        track.setPurchasedLicence(new ArrayList<>());
        track.setRating(new BigDecimal("2.3"));
        track.setUrlExclusive("https://example.org/example");
        track.setUrlNonExclusive("https://example.org/example");
        track.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate = new LicenceTemplate();
        licenceTemplate.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate.setId(1L);
        licenceTemplate.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate.setPrice(new BigDecimal("2.3"));
        licenceTemplate.setTrack(track);
        licenceTemplate.setValidityPeriodDays(1);
        Optional<LicenceTemplate> ofResult = Optional.of(licenceTemplate);
        when(licenceTemplateRepository.findByTrackAndLicenceType(Mockito.<Track>any(), Mockito.<LicenceType>any()))
                .thenReturn(ofResult);
        when(purchasedLicenceRepository.save(Mockito.<PurchasedLicence>any())).thenThrow(new AccessDeniedException("Msg"));

        Producer producer2 = new Producer();
        producer2.setAvatarUrl("https://example.org/example");
        producer2.setBalance(new BigDecimal("2.3"));
        producer2.setBio("Bio");
        producer2.setEmail("jane.doe@example.org");
        producer2.setId(1L);
        producer2.setLicenceReports(new ArrayList<>());
        producer2.setPassword("iloveyou");
        producer2.setRating(10.0d);
        producer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer2.setRole(Role.PRODUCER);
        producer2.setSoldLicences(new ArrayList<>());
        producer2.setTracks(new HashSet<>());
        producer2.setUsername("janedoe");

        Track track2 = new Track();
        track2.setBpm(1);
        track2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track2.setGenre(GenreType.HYPERPOP);
        track2.setId(1L);
        track2.setKeyType(KeyType.C);
        track2.setLastRatingDelta(0.5d);
        track2.setLicenceTemplates(new ArrayList<>());
        track2.setLikes(1);
        track2.setName("Name");
        track2.setPlays(1);
        track2.setProducer(producer2);
        track2.setPurchasedLicence(new ArrayList<>());
        track2.setRating(new BigDecimal("2.3"));
        track2.setUrlExclusive("https://example.org/example");
        track2.setUrlNonExclusive("https://example.org/example");
        track2.setUrlPremium("https://example.org/example");
        Optional<Track> ofResult2 = Optional.of(track2);
        when(trackRepository.findTrackById(Mockito.<Long>any())).thenReturn(ofResult2);
        doNothing().when(licenceValidator)
                .validatePurchaseCreateRequest(Mockito.<Customer>any(), Mockito.<Track>any(), Mockito.<LicenceType>any());

        // Act and Assert
        assertThrows(AccessDeniedException.class,
                () -> licencePurchaseServiceImpl.purchaseLicence(new PurchaseRequestDto(LicenceType.NON_EXCLUSIVE), 1L));
        verify(licenceTemplateRepository).findByTrackAndLicenceType(isA(Track.class), eq(LicenceType.NON_EXCLUSIVE));
        verify(trackRepository).findTrackById(eq(1L));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(licenceValidator).validatePurchaseCreateRequest(isA(Customer.class), isA(Track.class),
                eq(LicenceType.NON_EXCLUSIVE));
        verify(purchasedLicenceRepository).save(isA(PurchasedLicence.class));
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}.
     * <ul>
     *   <li>Then calls {@link PurchasedLicence#getExpiredDate()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}
     */
    @Test
    @DisplayName("Test getPurchasedLicenceById(Long); then calls getExpiredDate()")
    void testGetPurchasedLicenceById_thenCallsGetExpiredDate() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);

        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");

        Producer producer = new Producer();
        producer.setAvatarUrl("https://example.org/example");
        producer.setBio("Bio");
        producer.setEmail("jane.doe@example.org");
        producer.setId(1L);
        producer.setLicenceReports(new ArrayList<>());
        producer.setPassword("iloveyou");
        producer.setRating(10.0d);
        producer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer.setRole(Role.PRODUCER);
        producer.setSoldLicences(new ArrayList<>());
        producer.setTracks(new HashSet<>());
        producer.setUsername("janedoe");

        Track track = new Track();
        track.setBpm(1);
        track.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track.setGenre(GenreType.HYPERPOP);
        track.setId(1L);
        track.setKeyType(KeyType.C);
        track.setLastRatingDelta(0.5d);
        track.setLicenceTemplates(new ArrayList<>());
        track.setLikes(1);
        track.setName("Name");
        track.setPlays(1);
        track.setProducer(producer);
        track.setPurchasedLicence(new ArrayList<>());
        track.setRating(new BigDecimal("2.3"));
        track.setUrlExclusive("https://example.org/example");
        track.setUrlNonExclusive("https://example.org/example");
        track.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate = new LicenceTemplate();
        licenceTemplate.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate.setId(1L);
        licenceTemplate.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate.setPrice(new BigDecimal("2.3"));
        licenceTemplate.setTrack(track);
        licenceTemplate.setValidityPeriodDays(1);

        Producer producer2 = new Producer();
        producer2.setAvatarUrl("https://example.org/example");
        producer2.setBalance(new BigDecimal("2.3"));
        producer2.setBio("Bio");
        producer2.setEmail("jane.doe@example.org");
        producer2.setId(1L);
        producer2.setLicenceReports(new ArrayList<>());
        producer2.setPassword("iloveyou");
        producer2.setRating(10.0d);
        producer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer2.setRole(Role.PRODUCER);
        producer2.setSoldLicences(new ArrayList<>());
        producer2.setTracks(new HashSet<>());
        producer2.setUsername("janedoe");

        Producer producer3 = new Producer();
        producer3.setAvatarUrl("https://example.org/example");
        producer3.setBalance(new BigDecimal("2.3"));
        producer3.setBio("Bio");
        producer3.setEmail("jane.doe@example.org");
        producer3.setId(1L);
        producer3.setLicenceReports(new ArrayList<>());
        producer3.setPassword("iloveyou");
        producer3.setRating(10.0d);
        producer3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer3.setRole(Role.PRODUCER);
        producer3.setSoldLicences(new ArrayList<>());
        producer3.setTracks(new HashSet<>());
        producer3.setUsername("janedoe");

        Track track2 = new Track();
        track2.setBpm(1);
        track2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track2.setGenre(GenreType.HYPERPOP);
        track2.setId(1L);
        track2.setKeyType(KeyType.C);
        track2.setLastRatingDelta(0.5d);
        track2.setLicenceTemplates(new ArrayList<>());
        track2.setLikes(1);
        track2.setName("Name");
        track2.setPlays(1);
        track2.setProducer(producer3);
        track2.setPurchasedLicence(new ArrayList<>());
        track2.setRating(new BigDecimal("2.3"));
        track2.setUrlExclusive("https://example.org/example");
        track2.setUrlNonExclusive("https://example.org/example");
        track2.setUrlPremium("https://example.org/example");

        Producer producer4 = new Producer();
        producer4.setAvatarUrl("https://example.org/example");
        producer4.setBalance(new BigDecimal("2.3"));
        producer4.setBio("Bio");
        producer4.setEmail("jane.doe@example.org");
        producer4.setId(1L);
        producer4.setLicenceReports(new ArrayList<>());
        producer4.setPassword("iloveyou");
        producer4.setRating(10.0d);
        producer4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer4.setRole(Role.PRODUCER);
        producer4.setSoldLicences(new ArrayList<>());
        producer4.setTracks(new HashSet<>());
        producer4.setUsername("janedoe");

        Track track3 = new Track();
        track3.setBpm(1);
        track3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track3.setGenre(GenreType.HYPERPOP);
        track3.setId(1L);
        track3.setKeyType(KeyType.C);
        track3.setLastRatingDelta(0.5d);
        track3.setLicenceTemplates(new ArrayList<>());
        track3.setLikes(1);
        track3.setName("Name");
        track3.setPlays(1);
        track3.setProducer(producer4);
        track3.setPurchasedLicence(new ArrayList<>());
        track3.setRating(new BigDecimal("2.3"));
        track3.setUrlExclusive("https://example.org/example");
        track3.setUrlNonExclusive("https://example.org/example");
        track3.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate2 = new LicenceTemplate();
        licenceTemplate2.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate2.setId(1L);
        licenceTemplate2.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate2.setPrice(new BigDecimal("2.3"));
        licenceTemplate2.setTrack(track3);
        licenceTemplate2.setValidityPeriodDays(1);

        Producer producer5 = new Producer();
        producer5.setAvatarUrl("https://example.org/example");
        producer5.setBalance(new BigDecimal("2.3"));
        producer5.setBio("Bio");
        producer5.setEmail("jane.doe@example.org");
        producer5.setId(1L);
        producer5.setLicenceReports(new ArrayList<>());
        producer5.setPassword("iloveyou");
        producer5.setRating(10.0d);
        producer5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer5.setRole(Role.PRODUCER);
        producer5.setSoldLicences(new ArrayList<>());
        producer5.setTracks(new HashSet<>());
        producer5.setUsername("janedoe");
        PurchasedLicence purchasedLicence = mock(PurchasedLicence.class);
        when(purchasedLicence.getProducer()).thenReturn(producer5);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        when(purchasedLicence.getExpiredDate()).thenReturn(ofResult.atStartOfDay());
        LocalDate ofResult2 = LocalDate.of(1970, 1, 1);
        when(purchasedLicence.getPurchaseDate()).thenReturn(ofResult2.atStartOfDay());
        when(purchasedLicence.getLicenceTemplate()).thenReturn(licenceTemplate2);
        when(purchasedLicence.getId()).thenReturn(1L);
        doNothing().when(purchasedLicence).setCustomer(Mockito.<Customer>any());
        doNothing().when(purchasedLicence).setExpiredDate(Mockito.<LocalDateTime>any());
        doNothing().when(purchasedLicence).setId(Mockito.<Long>any());
        doNothing().when(purchasedLicence).setLicenceTemplate(Mockito.<LicenceTemplate>any());
        doNothing().when(purchasedLicence).setProducer(Mockito.<Producer>any());
        doNothing().when(purchasedLicence).setPurchaseDate(Mockito.<LocalDateTime>any());
        doNothing().when(purchasedLicence).setTrack(Mockito.<Track>any());
        purchasedLicence.setCustomer(customer);
        purchasedLicence.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence.setId(1L);
        purchasedLicence.setLicenceTemplate(licenceTemplate);
        purchasedLicence.setProducer(producer2);
        purchasedLicence.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence.setTrack(track2);
        Optional<PurchasedLicence> ofResult3 = Optional.of(purchasedLicence);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult3);
        doNothing().when(licenceValidator)
                .validatePurchasedLicenceGetRequest(Mockito.<User>any(), Mockito.<PurchasedLicence>any());

        // Act
        PurchaseDto actualPurchasedLicenceById = licencePurchaseServiceImpl.getPurchasedLicenceById(1L);

        // Assert
        verify(purchasedLicence).getExpiredDate();
        verify(purchasedLicence).getId();
        verify(purchasedLicence).getLicenceTemplate();
        verify(purchasedLicence, atLeast(1)).getProducer();
        verify(purchasedLicence).getPurchaseDate();
        verify(purchasedLicence).setCustomer(isA(Customer.class));
        verify(purchasedLicence).setExpiredDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setId(eq(1L));
        verify(purchasedLicence).setLicenceTemplate(isA(LicenceTemplate.class));
        verify(purchasedLicence).setProducer(isA(Producer.class));
        verify(purchasedLicence).setPurchaseDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setTrack(isA(Track.class));
        verify(authenticationService).getRequestingUserFromSecurityContext();
        verify(licenceValidator).validatePurchasedLicenceGetRequest(isA(User.class), isA(PurchasedLicence.class));
        verify(purchasedLicenceRepository).findById(eq(1L));
        assertEquals("janedoe", actualPurchasedLicenceById.getProducer());
        assertEquals(1, actualPurchasedLicenceById.getValidityPeriodDays().intValue());
        assertEquals(1L, actualPurchasedLicenceById.getProducerId().longValue());
        assertEquals(1L, actualPurchasedLicenceById.getPurchaseId().longValue());
        assertEquals(1L, actualPurchasedLicenceById.getTrackId().longValue());
        assertEquals(LicenceType.NON_EXCLUSIVE, actualPurchasedLicenceById.getLicenceType());
        assertTrue(actualPurchasedLicenceById.getAvailablePlatforms().isEmpty());
        BigDecimal expectedPrice = new BigDecimal("2.3");
        assertEquals(expectedPrice, actualPurchasedLicenceById.getPrice());
        assertSame(ofResult, actualPurchasedLicenceById.getExpiredDate().toLocalDate());
        assertSame(ofResult2, actualPurchasedLicenceById.getPurchaseDate().toLocalDate());
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}.
     * <ul>
     *   <li>Then return Producer is {@code janedoe}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}
     */
    @Test
    @DisplayName("Test getPurchasedLicenceById(Long); then return Producer is 'janedoe'")
    void testGetPurchasedLicenceById_thenReturnProducerIsJanedoe() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);

        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");

        Producer producer = new Producer();
        producer.setAvatarUrl("https://example.org/example");
        producer.setBio("Bio");
        producer.setEmail("jane.doe@example.org");
        producer.setId(1L);
        producer.setLicenceReports(new ArrayList<>());
        producer.setPassword("iloveyou");
        producer.setRating(10.0d);
        producer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer.setRole(Role.PRODUCER);
        producer.setSoldLicences(new ArrayList<>());
        producer.setTracks(new HashSet<>());
        producer.setUsername("janedoe");

        Track track = new Track();
        track.setBpm(1);
        track.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track.setGenre(GenreType.HYPERPOP);
        track.setId(1L);
        track.setKeyType(KeyType.C);
        track.setLastRatingDelta(0.5d);
        track.setLicenceTemplates(new ArrayList<>());
        track.setLikes(1);
        track.setName("Name");
        track.setPlays(1);
        track.setProducer(producer);
        track.setPurchasedLicence(new ArrayList<>());
        track.setRating(new BigDecimal("2.3"));
        track.setUrlExclusive("https://example.org/example");
        track.setUrlNonExclusive("https://example.org/example");
        track.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate = new LicenceTemplate();
        licenceTemplate.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate.setId(1L);
        licenceTemplate.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate.setPrice(new BigDecimal("2.3"));
        licenceTemplate.setTrack(track);
        licenceTemplate.setValidityPeriodDays(1);

        Producer producer2 = new Producer();
        producer2.setAvatarUrl("https://example.org/example");
        producer2.setBalance(new BigDecimal("2.3"));
        producer2.setBio("Bio");
        producer2.setEmail("jane.doe@example.org");
        producer2.setId(1L);
        producer2.setLicenceReports(new ArrayList<>());
        producer2.setPassword("iloveyou");
        producer2.setRating(10.0d);
        producer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer2.setRole(Role.PRODUCER);
        producer2.setSoldLicences(new ArrayList<>());
        producer2.setTracks(new HashSet<>());
        producer2.setUsername("janedoe");

        Producer producer3 = new Producer();
        producer3.setAvatarUrl("https://example.org/example");
        producer3.setBalance(new BigDecimal("2.3"));
        producer3.setBio("Bio");
        producer3.setEmail("jane.doe@example.org");
        producer3.setId(1L);
        producer3.setLicenceReports(new ArrayList<>());
        producer3.setPassword("iloveyou");
        producer3.setRating(10.0d);
        producer3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer3.setRole(Role.PRODUCER);
        producer3.setSoldLicences(new ArrayList<>());
        producer3.setTracks(new HashSet<>());
        producer3.setUsername("janedoe");

        Track track2 = new Track();
        track2.setBpm(1);
        track2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track2.setGenre(GenreType.HYPERPOP);
        track2.setId(1L);
        track2.setKeyType(KeyType.C);
        track2.setLastRatingDelta(0.5d);
        track2.setLicenceTemplates(new ArrayList<>());
        track2.setLikes(1);
        track2.setName("Name");
        track2.setPlays(1);
        track2.setProducer(producer3);
        track2.setPurchasedLicence(new ArrayList<>());
        track2.setRating(new BigDecimal("2.3"));
        track2.setUrlExclusive("https://example.org/example");
        track2.setUrlNonExclusive("https://example.org/example");
        track2.setUrlPremium("https://example.org/example");

        PurchasedLicence purchasedLicence = new PurchasedLicence();
        purchasedLicence.setCustomer(customer);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        purchasedLicence.setExpiredDate(ofResult.atStartOfDay());
        purchasedLicence.setId(1L);
        purchasedLicence.setLicenceTemplate(licenceTemplate);
        purchasedLicence.setProducer(producer2);
        LocalDate ofResult2 = LocalDate.of(1970, 1, 1);
        purchasedLicence.setPurchaseDate(ofResult2.atStartOfDay());
        purchasedLicence.setTrack(track2);
        Optional<PurchasedLicence> ofResult3 = Optional.of(purchasedLicence);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult3);
        doNothing().when(licenceValidator)
                .validatePurchasedLicenceGetRequest(Mockito.<User>any(), Mockito.<PurchasedLicence>any());

        // Act
        PurchaseDto actualPurchasedLicenceById = licencePurchaseServiceImpl.getPurchasedLicenceById(1L);

        // Assert
        verify(authenticationService).getRequestingUserFromSecurityContext();
        verify(licenceValidator).validatePurchasedLicenceGetRequest(isA(User.class), isA(PurchasedLicence.class));
        verify(purchasedLicenceRepository).findById(eq(1L));
        assertEquals("janedoe", actualPurchasedLicenceById.getProducer());
        assertEquals(1, actualPurchasedLicenceById.getValidityPeriodDays().intValue());
        assertEquals(1L, actualPurchasedLicenceById.getProducerId().longValue());
        assertEquals(1L, actualPurchasedLicenceById.getPurchaseId().longValue());
        assertEquals(1L, actualPurchasedLicenceById.getTrackId().longValue());
        assertEquals(LicenceType.NON_EXCLUSIVE, actualPurchasedLicenceById.getLicenceType());
        assertTrue(actualPurchasedLicenceById.getAvailablePlatforms().isEmpty());
        BigDecimal expectedPrice = new BigDecimal("2.3");
        assertEquals(expectedPrice, actualPurchasedLicenceById.getPrice());
        assertSame(ofResult, actualPurchasedLicenceById.getExpiredDate().toLocalDate());
        assertSame(ofResult2, actualPurchasedLicenceById.getPurchaseDate().toLocalDate());
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}.
     * <ul>
     *   <li>Then throw {@link AccessDeniedException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}
     */
    @Test
    @DisplayName("Test getPurchasedLicenceById(Long); then throw AccessDeniedException")
    void testGetPurchasedLicenceById_thenThrowAccessDeniedException() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);

        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");

        Producer producer = new Producer();
        producer.setAvatarUrl("https://example.org/example");
        producer.setBio("Bio");
        producer.setEmail("jane.doe@example.org");
        producer.setId(1L);
        producer.setLicenceReports(new ArrayList<>());
        producer.setPassword("iloveyou");
        producer.setRating(10.0d);
        producer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer.setRole(Role.PRODUCER);
        producer.setSoldLicences(new ArrayList<>());
        producer.setTracks(new HashSet<>());
        producer.setUsername("janedoe");

        Track track = new Track();
        track.setBpm(1);
        track.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track.setGenre(GenreType.HYPERPOP);
        track.setId(1L);
        track.setKeyType(KeyType.C);
        track.setLastRatingDelta(0.5d);
        track.setLicenceTemplates(new ArrayList<>());
        track.setLikes(1);
        track.setName("Name");
        track.setPlays(1);
        track.setProducer(producer);
        track.setPurchasedLicence(new ArrayList<>());
        track.setRating(new BigDecimal("2.3"));
        track.setUrlExclusive("https://example.org/example");
        track.setUrlNonExclusive("https://example.org/example");
        track.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate = new LicenceTemplate();
        licenceTemplate.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate.setId(1L);
        licenceTemplate.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate.setPrice(new BigDecimal("2.3"));
        licenceTemplate.setTrack(track);
        licenceTemplate.setValidityPeriodDays(1);

        Producer producer2 = new Producer();
        producer2.setAvatarUrl("https://example.org/example");
        producer2.setBalance(new BigDecimal("2.3"));
        producer2.setBio("Bio");
        producer2.setEmail("jane.doe@example.org");
        producer2.setId(1L);
        producer2.setLicenceReports(new ArrayList<>());
        producer2.setPassword("iloveyou");
        producer2.setRating(10.0d);
        producer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer2.setRole(Role.PRODUCER);
        producer2.setSoldLicences(new ArrayList<>());
        producer2.setTracks(new HashSet<>());
        producer2.setUsername("janedoe");

        Producer producer3 = new Producer();
        producer3.setAvatarUrl("https://example.org/example");
        producer3.setBalance(new BigDecimal("2.3"));
        producer3.setBio("Bio");
        producer3.setEmail("jane.doe@example.org");
        producer3.setId(1L);
        producer3.setLicenceReports(new ArrayList<>());
        producer3.setPassword("iloveyou");
        producer3.setRating(10.0d);
        producer3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer3.setRole(Role.PRODUCER);
        producer3.setSoldLicences(new ArrayList<>());
        producer3.setTracks(new HashSet<>());
        producer3.setUsername("janedoe");

        Track track2 = new Track();
        track2.setBpm(1);
        track2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track2.setGenre(GenreType.HYPERPOP);
        track2.setId(1L);
        track2.setKeyType(KeyType.C);
        track2.setLastRatingDelta(0.5d);
        track2.setLicenceTemplates(new ArrayList<>());
        track2.setLikes(1);
        track2.setName("Name");
        track2.setPlays(1);
        track2.setProducer(producer3);
        track2.setPurchasedLicence(new ArrayList<>());
        track2.setRating(new BigDecimal("2.3"));
        track2.setUrlExclusive("https://example.org/example");
        track2.setUrlNonExclusive("https://example.org/example");
        track2.setUrlPremium("https://example.org/example");

        PurchasedLicence purchasedLicence = new PurchasedLicence();
        purchasedLicence.setCustomer(customer);
        purchasedLicence.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence.setId(1L);
        purchasedLicence.setLicenceTemplate(licenceTemplate);
        purchasedLicence.setProducer(producer2);
        purchasedLicence.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence.setTrack(track2);
        Optional<PurchasedLicence> ofResult = Optional.of(purchasedLicence);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        doThrow(new AccessDeniedException("Msg")).when(licenceValidator)
                .validatePurchasedLicenceGetRequest(Mockito.<User>any(), Mockito.<PurchasedLicence>any());

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> licencePurchaseServiceImpl.getPurchasedLicenceById(1L));
        verify(authenticationService).getRequestingUserFromSecurityContext();
        verify(licenceValidator).validatePurchasedLicenceGetRequest(isA(User.class), isA(PurchasedLicence.class));
        verify(purchasedLicenceRepository).findById(eq(1L));
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getPurchasedLicenceById(Long)}
     */
    @Test
    @DisplayName("Test getPurchasedLicenceById(Long); then throw EntityNotFoundException")
    void testGetPurchasedLicenceById_thenThrowEntityNotFoundException() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);

        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");

        Producer producer = new Producer();
        producer.setAvatarUrl("https://example.org/example");
        producer.setBio("Bio");
        producer.setEmail("jane.doe@example.org");
        producer.setId(1L);
        producer.setLicenceReports(new ArrayList<>());
        producer.setPassword("iloveyou");
        producer.setRating(10.0d);
        producer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer.setRole(Role.PRODUCER);
        producer.setSoldLicences(new ArrayList<>());
        producer.setTracks(new HashSet<>());
        producer.setUsername("janedoe");

        Track track = new Track();
        track.setBpm(1);
        track.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track.setGenre(GenreType.HYPERPOP);
        track.setId(1L);
        track.setKeyType(KeyType.C);
        track.setLastRatingDelta(0.5d);
        track.setLicenceTemplates(new ArrayList<>());
        track.setLikes(1);
        track.setName("Name");
        track.setPlays(1);
        track.setProducer(producer);
        track.setPurchasedLicence(new ArrayList<>());
        track.setRating(new BigDecimal("2.3"));
        track.setUrlExclusive("https://example.org/example");
        track.setUrlNonExclusive("https://example.org/example");
        track.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate = new LicenceTemplate();
        licenceTemplate.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate.setId(1L);
        licenceTemplate.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate.setPrice(new BigDecimal("2.3"));
        licenceTemplate.setTrack(track);
        licenceTemplate.setValidityPeriodDays(1);

        Producer producer2 = new Producer();
        producer2.setAvatarUrl("https://example.org/example");
        producer2.setBalance(new BigDecimal("2.3"));
        producer2.setBio("Bio");
        producer2.setEmail("jane.doe@example.org");
        producer2.setId(1L);
        producer2.setLicenceReports(new ArrayList<>());
        producer2.setPassword("iloveyou");
        producer2.setRating(10.0d);
        producer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer2.setRole(Role.PRODUCER);
        producer2.setSoldLicences(new ArrayList<>());
        producer2.setTracks(new HashSet<>());
        producer2.setUsername("janedoe");

        Producer producer3 = new Producer();
        producer3.setAvatarUrl("https://example.org/example");
        producer3.setBalance(new BigDecimal("2.3"));
        producer3.setBio("Bio");
        producer3.setEmail("jane.doe@example.org");
        producer3.setId(1L);
        producer3.setLicenceReports(new ArrayList<>());
        producer3.setPassword("iloveyou");
        producer3.setRating(10.0d);
        producer3.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer3.setRole(Role.PRODUCER);
        producer3.setSoldLicences(new ArrayList<>());
        producer3.setTracks(new HashSet<>());
        producer3.setUsername("janedoe");

        Track track2 = new Track();
        track2.setBpm(1);
        track2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track2.setGenre(GenreType.HYPERPOP);
        track2.setId(1L);
        track2.setKeyType(KeyType.C);
        track2.setLastRatingDelta(0.5d);
        track2.setLicenceTemplates(new ArrayList<>());
        track2.setLikes(1);
        track2.setName("Name");
        track2.setPlays(1);
        track2.setProducer(producer3);
        track2.setPurchasedLicence(new ArrayList<>());
        track2.setRating(new BigDecimal("2.3"));
        track2.setUrlExclusive("https://example.org/example");
        track2.setUrlNonExclusive("https://example.org/example");
        track2.setUrlPremium("https://example.org/example");

        Producer producer4 = new Producer();
        producer4.setAvatarUrl("https://example.org/example");
        producer4.setBalance(new BigDecimal("2.3"));
        producer4.setBio("Bio");
        producer4.setEmail("jane.doe@example.org");
        producer4.setId(1L);
        producer4.setLicenceReports(new ArrayList<>());
        producer4.setPassword("iloveyou");
        producer4.setRating(10.0d);
        producer4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer4.setRole(Role.PRODUCER);
        producer4.setSoldLicences(new ArrayList<>());
        producer4.setTracks(new HashSet<>());
        producer4.setUsername("janedoe");

        Track track3 = new Track();
        track3.setBpm(1);
        track3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track3.setGenre(GenreType.HYPERPOP);
        track3.setId(1L);
        track3.setKeyType(KeyType.C);
        track3.setLastRatingDelta(0.5d);
        track3.setLicenceTemplates(new ArrayList<>());
        track3.setLikes(1);
        track3.setName("Name");
        track3.setPlays(1);
        track3.setProducer(producer4);
        track3.setPurchasedLicence(new ArrayList<>());
        track3.setRating(new BigDecimal("2.3"));
        track3.setUrlExclusive("https://example.org/example");
        track3.setUrlNonExclusive("https://example.org/example");
        track3.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate2 = new LicenceTemplate();
        licenceTemplate2.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate2.setId(1L);
        licenceTemplate2.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate2.setPrice(new BigDecimal("2.3"));
        licenceTemplate2.setTrack(track3);
        licenceTemplate2.setValidityPeriodDays(1);
        PurchasedLicence purchasedLicence = mock(PurchasedLicence.class);
        when(purchasedLicence.getPurchaseDate()).thenThrow(new EntityNotFoundException("An error occurred"));
        when(purchasedLicence.getLicenceTemplate()).thenReturn(licenceTemplate2);
        when(purchasedLicence.getId()).thenReturn(1L);
        doNothing().when(purchasedLicence).setCustomer(Mockito.<Customer>any());
        doNothing().when(purchasedLicence).setExpiredDate(Mockito.<LocalDateTime>any());
        doNothing().when(purchasedLicence).setId(Mockito.<Long>any());
        doNothing().when(purchasedLicence).setLicenceTemplate(Mockito.<LicenceTemplate>any());
        doNothing().when(purchasedLicence).setProducer(Mockito.<Producer>any());
        doNothing().when(purchasedLicence).setPurchaseDate(Mockito.<LocalDateTime>any());
        doNothing().when(purchasedLicence).setTrack(Mockito.<Track>any());
        purchasedLicence.setCustomer(customer);
        purchasedLicence.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence.setId(1L);
        purchasedLicence.setLicenceTemplate(licenceTemplate);
        purchasedLicence.setProducer(producer2);
        purchasedLicence.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence.setTrack(track2);
        Optional<PurchasedLicence> ofResult = Optional.of(purchasedLicence);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        doNothing().when(licenceValidator)
                .validatePurchasedLicenceGetRequest(Mockito.<User>any(), Mockito.<PurchasedLicence>any());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> licencePurchaseServiceImpl.getPurchasedLicenceById(1L));
        verify(purchasedLicence).getId();
        verify(purchasedLicence).getLicenceTemplate();
        verify(purchasedLicence).getPurchaseDate();
        verify(purchasedLicence).setCustomer(isA(Customer.class));
        verify(purchasedLicence).setExpiredDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setId(eq(1L));
        verify(purchasedLicence).setLicenceTemplate(isA(LicenceTemplate.class));
        verify(purchasedLicence).setProducer(isA(Producer.class));
        verify(purchasedLicence).setPurchaseDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setTrack(isA(Track.class));
        verify(authenticationService).getRequestingUserFromSecurityContext();
        verify(licenceValidator).validatePurchasedLicenceGetRequest(isA(User.class), isA(PurchasedLicence.class));
        verify(purchasedLicenceRepository).findById(eq(1L));
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getAllPurchasedLicences()}.
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getAllPurchasedLicences()}
     */
    @Test
    @DisplayName("Test getAllPurchasedLicences()")
    void testGetAllPurchasedLicences() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        when(purchasedLicenceRepository.findForProducerByProducerId(Mockito.<Long>any()))
                .thenThrow(new AccessDeniedException("Msg"));

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> licencePurchaseServiceImpl.getAllPurchasedLicences());
        verify(purchasedLicenceRepository).findForProducerByProducerId(eq(1L));
        verify(authenticationService).getRequestingUserFromSecurityContext();
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getAllPurchasedLicences()}.
     * <ul>
     *   <li>Given {@link Customer} {@link User#getRole()} return {@code ADMIN}.</li>
     *   <li>Then calls {@link User#getRole()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getAllPurchasedLicences()}
     */
    @Test
    @DisplayName("Test getAllPurchasedLicences(); given Customer getRole() return 'ADMIN'; then calls getRole()")
    void testGetAllPurchasedLicences_givenCustomerGetRoleReturnAdmin_thenCallsGetRole() {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getRole()).thenReturn(Role.ADMIN);
        doNothing().when(customer).setAvatarUrl(Mockito.<String>any());
        doNothing().when(customer).setBalance(Mockito.<BigDecimal>any());
        doNothing().when(customer).setBio(Mockito.<String>any());
        doNothing().when(customer).setEmail(Mockito.<String>any());
        doNothing().when(customer).setId(Mockito.<Long>any());
        doNothing().when(customer).setPassword(Mockito.<String>any());
        doNothing().when(customer).setRegistrationDate(Mockito.<LocalDateTime>any());
        doNothing().when(customer).setRole(Mockito.<Role>any());
        doNothing().when(customer).setUsername(Mockito.<String>any());
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(customer);

        // Act and Assert
        assertThrows(AccessDeniedException.class, () -> licencePurchaseServiceImpl.getAllPurchasedLicences());
        verify(customer, atLeast(1)).getRole();
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer).setPassword(eq("iloveyou"));
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer).setUsername(eq("janedoe"));
        verify(authenticationService).getRequestingUserFromSecurityContext();
    }

    /**
     * Test {@link LicencePurchaseServiceImpl#getAllPurchasedLicences()}.
     * <ul>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link LicencePurchaseServiceImpl#getAllPurchasedLicences()}
     */
    @Test
    @DisplayName("Test getAllPurchasedLicences(); then return Empty")
    void testGetAllPurchasedLicences_thenReturnEmpty() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        user.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        when(purchasedLicenceRepository.findForProducerByProducerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

        // Act
        List<PurchaseDto> actualAllPurchasedLicences = licencePurchaseServiceImpl.getAllPurchasedLicences();

        // Assert
        verify(purchasedLicenceRepository).findForProducerByProducerId(eq(1L));
        verify(authenticationService).getRequestingUserFromSecurityContext();
        assertTrue(actualAllPurchasedLicences.isEmpty());
    }
}
