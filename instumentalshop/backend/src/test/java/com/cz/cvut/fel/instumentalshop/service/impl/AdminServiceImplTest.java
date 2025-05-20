package com.cz.cvut.fel.instumentalshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapperImpl;
import com.cz.cvut.fel.instumentalshop.dto.newDto.PurchaseUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ContextConfiguration(classes = {AdminServiceImpl.class})
@ExtendWith(SpringExtension.class)
class AdminServiceImplTest {
    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @MockBean
    private PurchasedLicenceRepository purchasedLicenceRepository;

    @MockBean
    private TrackRepository trackRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserRepository userRepository;

    /**
     * Test {@link AdminServiceImpl#updateUserRole(Long, Role)}.
     * <p>
     * Method under test: {@link AdminServiceImpl#updateUserRole(Long, Role)}
     */
    @Test
    @DisplayName("Test updateUserRole(Long, Role)")
    void testUpdateUserRole() {
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
        Optional<User> ofResult = Optional.of(user);
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.save(Mockito.<User>any())).thenThrow(new ResponseStatusException(HttpStatus.OK));
        when(userRepo.findById(Mockito.<Long>any())).thenReturn(ofResult);
        TrackRepository trackRepo = mock(TrackRepository.class);
        PurchasedLicenceRepository licRepo = mock(PurchasedLicenceRepository.class);

        // Act and Assert
        assertThrows(ResponseStatusException.class,
                () -> (new AdminServiceImpl(trackRepo, userRepo, licRepo, new UserMapperImpl())).updateUserRole(1L,
                        Role.PRODUCER));
        verify(userRepo).findById(eq(1L));
        verify(userRepo).save(isA(User.class));
    }

    /**
     * Test {@link AdminServiceImpl#updateUserRole(Long, Role)}.
     * <p>
     * Method under test: {@link AdminServiceImpl#updateUserRole(Long, Role)}
     */
    @Test
    @DisplayName("Test updateUserRole(Long, Role)")
    void testUpdateUserRole2() {
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
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setAvatarUrl("https://example.org/example");
        user2.setBalance(new BigDecimal("2.3"));
        user2.setBio("Bio");
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setPassword("iloveyou");
        user2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setRole(Role.PRODUCER);
        user2.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(userMapper.toDto(Mockito.<User>any())).thenThrow(new ResponseStatusException(HttpStatus.OK));

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.updateUserRole(1L, Role.PRODUCER));
        verify(userMapper).toDto(isA(User.class));
        verify(userRepository).findById(eq(1L));
        verify(userRepository).save(isA(User.class));
    }

    /**
     * Test {@link AdminServiceImpl#updateUserRole(Long, Role)}.
     * <ul>
     *   <li>Given {@link UserMapper} {@link UserMapper#toDto(User)} return {@link UserDto#UserDto()}.</li>
     *   <li>Then return {@link UserDto#UserDto()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#updateUserRole(Long, Role)}
     */
    @Test
    @DisplayName("Test updateUserRole(Long, Role); given UserMapper toDto(User) return UserDto(); then return UserDto()")
    void testUpdateUserRole_givenUserMapperToDtoReturnUserDto_thenReturnUserDto() {
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
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setAvatarUrl("https://example.org/example");
        user2.setBalance(new BigDecimal("2.3"));
        user2.setBio("Bio");
        user2.setEmail("jane.doe@example.org");
        user2.setId(1L);
        user2.setPassword("iloveyou");
        user2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setRole(Role.PRODUCER);
        user2.setUsername("janedoe");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        UserDto userDto = new UserDto();
        when(userMapper.toDto(Mockito.<User>any())).thenReturn(userDto);

        // Act
        UserDto actualUpdateUserRoleResult = adminServiceImpl.updateUserRole(1L, Role.PRODUCER);

        // Assert
        verify(userMapper).toDto(isA(User.class));
        verify(userRepository).findById(eq(1L));
        verify(userRepository).save(isA(User.class));
        assertSame(userDto, actualUpdateUserRoleResult);
    }

    /**
     * Test {@link AdminServiceImpl#updateUserRole(Long, Role)}.
     * <ul>
     *   <li>Given {@link UserRepository} {@link CrudRepository#findById(Object)} return empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#updateUserRole(Long, Role)}
     */
    @Test
    @DisplayName("Test updateUserRole(Long, Role); given UserRepository findById(Object) return empty")
    void testUpdateUserRole_givenUserRepositoryFindByIdReturnEmpty() {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.updateUserRole(1L, Role.PRODUCER));
        verify(userRepository).findById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deletePurchase(Long)}.
     * <p>
     * Method under test: {@link AdminServiceImpl#deletePurchase(Long)}
     */
    @Test
    @DisplayName("Test deletePurchase(Long)")
    void testDeletePurchase() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(purchasedLicenceRepository)
                .deleteById(Mockito.<Long>any());
        when(purchasedLicenceRepository.existsById(Mockito.<Long>any())).thenReturn(true);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.deletePurchase(1L));
        verify(purchasedLicenceRepository).deleteById(eq(1L));
        verify(purchasedLicenceRepository).existsById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deletePurchase(Long)}.
     * <ul>
     *   <li>Given {@link PurchasedLicenceRepository} {@link CrudRepository#deleteById(Object)} does nothing.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deletePurchase(Long)}
     */
    @Test
    @DisplayName("Test deletePurchase(Long); given PurchasedLicenceRepository deleteById(Object) does nothing")
    void testDeletePurchase_givenPurchasedLicenceRepositoryDeleteByIdDoesNothing() {
        // Arrange
        doNothing().when(purchasedLicenceRepository).deleteById(Mockito.<Long>any());
        when(purchasedLicenceRepository.existsById(Mockito.<Long>any())).thenReturn(true);

        // Act
        adminServiceImpl.deletePurchase(1L);

        // Assert
        verify(purchasedLicenceRepository).deleteById(eq(1L));
        verify(purchasedLicenceRepository).existsById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deletePurchase(Long)}.
     * <ul>
     *   <li>Given {@link PurchasedLicenceRepository} {@link CrudRepository#existsById(Object)} return {@code false}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deletePurchase(Long)}
     */
    @Test
    @DisplayName("Test deletePurchase(Long); given PurchasedLicenceRepository existsById(Object) return 'false'")
    void testDeletePurchase_givenPurchasedLicenceRepositoryExistsByIdReturnFalse() {
        // Arrange
        when(purchasedLicenceRepository.existsById(Mockito.<Long>any())).thenReturn(false);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.deletePurchase(1L));
        verify(purchasedLicenceRepository).existsById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#getAllPurchases()}.
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllPurchases()}
     */
    @Test
    @DisplayName("Test getAllPurchases()")
    void testGetAllPurchases() {
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
        PurchasedLicence purchasedLicence = mock(PurchasedLicence.class);
        when(purchasedLicence.getLicenceTemplate()).thenThrow(new ResponseStatusException(HttpStatus.OK));
        when(purchasedLicence.getTrack()).thenReturn(track3);
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

        ArrayList<PurchasedLicence> purchasedLicenceList = new ArrayList<>();
        purchasedLicenceList.add(purchasedLicence);
        PurchasedLicenceRepository licRepo = mock(PurchasedLicenceRepository.class);
        when(licRepo.findAll()).thenReturn(purchasedLicenceList);
        TrackRepository trackRepo = mock(TrackRepository.class);
        UserRepository userRepo = mock(UserRepository.class);

        // Act and Assert
        assertThrows(ResponseStatusException.class,
                () -> (new AdminServiceImpl(trackRepo, userRepo, licRepo, new UserMapperImpl())).getAllPurchases());
        verify(purchasedLicence).getId();
        verify(purchasedLicence).getLicenceTemplate();
        verify(purchasedLicence).getTrack();
        verify(purchasedLicence).setCustomer(isA(Customer.class));
        verify(purchasedLicence).setExpiredDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setId(eq(1L));
        verify(purchasedLicence).setLicenceTemplate(isA(LicenceTemplate.class));
        verify(purchasedLicence).setProducer(isA(Producer.class));
        verify(purchasedLicence).setPurchaseDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setTrack(isA(Track.class));
        verify(licRepo).findAll();
    }

    /**
     * Test {@link AdminServiceImpl#getAllPurchases()}.
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllPurchases()}
     */
    @Test
    @DisplayName("Test getAllPurchases()")
    void testGetAllPurchases2() {
        // Arrange
        when(purchasedLicenceRepository.findAll()).thenThrow(new ResponseStatusException(HttpStatus.OK));

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.getAllPurchases());
        verify(purchasedLicenceRepository).findAll();
    }

    /**
     * Test {@link AdminServiceImpl#getAllPurchases()}.
     * <ul>
     *   <li>Given {@link Customer#Customer()} AvatarUrl is {@code Avatar Url}.</li>
     *   <li>Then return size is two.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllPurchases()}
     */
    @Test
    @DisplayName("Test getAllPurchases(); given Customer() AvatarUrl is 'Avatar Url'; then return size is two")
    void testGetAllPurchases_givenCustomerAvatarUrlIsAvatarUrl_thenReturnSizeIsTwo() {
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

        Customer customer2 = new Customer();
        customer2.setAvatarUrl("Avatar Url");
        customer2.setBalance(new BigDecimal("2.3"));
        customer2.setBio("42");
        customer2.setEmail("john.smith@example.org");
        customer2.setId(2L);
        customer2.setOrders(new ArrayList<>());
        customer2.setPassword("Password");
        customer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer2.setRole(Role.CUSTOMER);
        customer2.setUsername("Username");

        Producer producer4 = new Producer();
        producer4.setAvatarUrl("Avatar Url");
        producer4.setBio("42");
        producer4.setEmail("john.smith@example.org");
        producer4.setId(2L);
        producer4.setLicenceReports(new ArrayList<>());
        producer4.setPassword("Password");
        producer4.setRating(1.0d);
        producer4.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer4.setRole(Role.CUSTOMER);
        producer4.setSoldLicences(new ArrayList<>());
        producer4.setTracks(new HashSet<>());
        producer4.setUsername("Username");

        Track track3 = new Track();
        track3.setBpm(0);
        track3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track3.setGenre(GenreType.ROCK);
        track3.setId(2L);
        track3.setKeyType(KeyType.C_SHARP);
        track3.setLastRatingDelta(10.0d);
        track3.setLicenceTemplates(new ArrayList<>());
        track3.setLikes(0);
        track3.setName("42");
        track3.setPlays(0);
        track3.setProducer(producer4);
        track3.setPurchasedLicence(new ArrayList<>());
        track3.setRating(new BigDecimal("2.3"));
        track3.setUrlExclusive("Url Exclusive");
        track3.setUrlNonExclusive("Url Non Exclusive");
        track3.setUrlPremium("Url Premium");

        LicenceTemplate licenceTemplate2 = new LicenceTemplate();
        licenceTemplate2.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate2.setId(2L);
        licenceTemplate2.setLicenceType(LicenceType.PREMIUM);
        licenceTemplate2.setPrice(new BigDecimal("2.3"));
        licenceTemplate2.setTrack(track3);
        licenceTemplate2.setValidityPeriodDays(2);

        Producer producer5 = new Producer();
        producer5.setAvatarUrl("Avatar Url");
        producer5.setBalance(new BigDecimal("2.3"));
        producer5.setBio("42");
        producer5.setEmail("john.smith@example.org");
        producer5.setId(2L);
        producer5.setLicenceReports(new ArrayList<>());
        producer5.setPassword("Password");
        producer5.setRating(1.0d);
        producer5.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer5.setRole(Role.CUSTOMER);
        producer5.setSoldLicences(new ArrayList<>());
        producer5.setTracks(new HashSet<>());
        producer5.setUsername("Username");

        Producer producer6 = new Producer();
        producer6.setAvatarUrl("Avatar Url");
        producer6.setBalance(new BigDecimal("2.3"));
        producer6.setBio("42");
        producer6.setEmail("john.smith@example.org");
        producer6.setId(2L);
        producer6.setLicenceReports(new ArrayList<>());
        producer6.setPassword("Password");
        producer6.setRating(1.0d);
        producer6.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer6.setRole(Role.CUSTOMER);
        producer6.setSoldLicences(new ArrayList<>());
        producer6.setTracks(new HashSet<>());
        producer6.setUsername("Username");

        Track track4 = new Track();
        track4.setBpm(0);
        track4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track4.setGenre(GenreType.ROCK);
        track4.setId(2L);
        track4.setKeyType(KeyType.C_SHARP);
        track4.setLastRatingDelta(10.0d);
        track4.setLicenceTemplates(new ArrayList<>());
        track4.setLikes(0);
        track4.setName("42");
        track4.setPlays(0);
        track4.setProducer(producer6);
        track4.setPurchasedLicence(new ArrayList<>());
        track4.setRating(new BigDecimal("2.3"));
        track4.setUrlExclusive("Url Exclusive");
        track4.setUrlNonExclusive("Url Non Exclusive");
        track4.setUrlPremium("Url Premium");

        PurchasedLicence purchasedLicence2 = new PurchasedLicence();
        purchasedLicence2.setCustomer(customer2);
        purchasedLicence2.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setId(2L);
        purchasedLicence2.setLicenceTemplate(licenceTemplate2);
        purchasedLicence2.setProducer(producer5);
        purchasedLicence2.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setTrack(track4);

        ArrayList<PurchasedLicence> purchasedLicenceList = new ArrayList<>();
        purchasedLicenceList.add(purchasedLicence2);
        purchasedLicenceList.add(purchasedLicence);
        when(purchasedLicenceRepository.findAll()).thenReturn(purchasedLicenceList);

        // Act
        List<PurchaseDto> actualAllPurchases = adminServiceImpl.getAllPurchases();

        // Assert
        verify(purchasedLicenceRepository).findAll();
        assertEquals(2, actualAllPurchases.size());
        PurchaseDto getResult = actualAllPurchases.get(1);
        assertNull(getResult.getProducerId());
        assertNull(getResult.getProducer());
        assertEquals(1, getResult.getValidityPeriodDays().intValue());
        assertEquals(1L, getResult.getPurchaseId().longValue());
        assertEquals(1L, getResult.getTrackId().longValue());
        PurchaseDto getResult2 = actualAllPurchases.get(0);
        assertEquals(2, getResult2.getValidityPeriodDays().intValue());
        assertEquals(2L, getResult2.getPurchaseId().longValue());
        assertEquals(2L, getResult2.getTrackId().longValue());
        assertEquals(LicenceType.NON_EXCLUSIVE, getResult.getLicenceType());
        assertEquals(LicenceType.PREMIUM, getResult2.getLicenceType());
        BigDecimal expectedPrice = new BigDecimal("2.3");
        assertEquals(expectedPrice, getResult.getPrice());
    }

    /**
     * Test {@link AdminServiceImpl#getAllPurchases()}.
     * <ul>
     *   <li>Given {@link PurchasedLicence#PurchasedLicence()} Customer is {@link Customer#Customer()}.</li>
     *   <li>Then return size is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllPurchases()}
     */
    @Test
    @DisplayName("Test getAllPurchases(); given PurchasedLicence() Customer is Customer(); then return size is one")
    void testGetAllPurchases_givenPurchasedLicenceCustomerIsCustomer_thenReturnSizeIsOne() {
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

        ArrayList<PurchasedLicence> purchasedLicenceList = new ArrayList<>();
        purchasedLicenceList.add(purchasedLicence);
        when(purchasedLicenceRepository.findAll()).thenReturn(purchasedLicenceList);

        // Act
        List<PurchaseDto> actualAllPurchases = adminServiceImpl.getAllPurchases();

        // Assert
        verify(purchasedLicenceRepository).findAll();
        assertEquals(1, actualAllPurchases.size());
        PurchaseDto getResult = actualAllPurchases.get(0);
        assertEquals(1, getResult.getValidityPeriodDays().intValue());
        assertEquals(1L, getResult.getPurchaseId().longValue());
        assertEquals(1L, getResult.getTrackId().longValue());
        assertEquals(LicenceType.NON_EXCLUSIVE, getResult.getLicenceType());
    }

    /**
     * Test {@link AdminServiceImpl#getAllPurchases()}.
     * <ul>
     *   <li>Given {@link TrackRepository}.</li>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllPurchases()}
     */
    @Test
    @DisplayName("Test getAllPurchases(); given TrackRepository; then return Empty")
    void testGetAllPurchases_givenTrackRepository_thenReturnEmpty() {
        // Arrange
        when(purchasedLicenceRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<PurchaseDto> actualAllPurchases = adminServiceImpl.getAllPurchases();

        // Assert
        verify(purchasedLicenceRepository).findAll();
        assertTrue(actualAllPurchases.isEmpty());
    }

    /**
     * Test {@link AdminServiceImpl#getAllPurchases()}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllPurchases()}
     */
    @Test
    @DisplayName("Test getAllPurchases(); then throw EntityNotFoundException")
    void testGetAllPurchases_thenThrowEntityNotFoundException() {
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

        Track track4 = new Track();
        track4.setBpm(1);
        track4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track4.setGenre(GenreType.HYPERPOP);
        track4.setId(1L);
        track4.setKeyType(KeyType.C);
        track4.setLastRatingDelta(0.5d);
        track4.setLicenceTemplates(new ArrayList<>());
        track4.setLikes(1);
        track4.setName("Name");
        track4.setPlays(1);
        track4.setProducer(producer5);
        track4.setPurchasedLicence(new ArrayList<>());
        track4.setRating(new BigDecimal("2.3"));
        track4.setUrlExclusive("https://example.org/example");
        track4.setUrlNonExclusive("https://example.org/example");
        track4.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate2 = new LicenceTemplate();
        licenceTemplate2.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate2.setId(1L);
        licenceTemplate2.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate2.setPrice(new BigDecimal("2.3"));
        licenceTemplate2.setTrack(track4);
        licenceTemplate2.setValidityPeriodDays(1);
        PurchasedLicence purchasedLicence = mock(PurchasedLicence.class);
        when(purchasedLicence.getPurchaseDate()).thenThrow(new EntityNotFoundException("An error occurred"));
        when(purchasedLicence.getLicenceTemplate()).thenReturn(licenceTemplate2);
        when(purchasedLicence.getTrack()).thenReturn(track3);
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

        ArrayList<PurchasedLicence> purchasedLicenceList = new ArrayList<>();
        purchasedLicenceList.add(purchasedLicence);
        when(purchasedLicenceRepository.findAll()).thenReturn(purchasedLicenceList);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> adminServiceImpl.getAllPurchases());
        verify(purchasedLicence).getId();
        verify(purchasedLicence, atLeast(1)).getLicenceTemplate();
        verify(purchasedLicence).getPurchaseDate();
        verify(purchasedLicence).getTrack();
        verify(purchasedLicence).setCustomer(isA(Customer.class));
        verify(purchasedLicence).setExpiredDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setId(eq(1L));
        verify(purchasedLicence).setLicenceTemplate(isA(LicenceTemplate.class));
        verify(purchasedLicence).setProducer(isA(Producer.class));
        verify(purchasedLicence).setPurchaseDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setTrack(isA(Track.class));
        verify(purchasedLicenceRepository).findAll();
    }

    /**
     * Test {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}.
     * <p>
     * Method under test: {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updatePurchase(Long, PurchaseUpdateRequestDto)")
    void testUpdatePurchase() {
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

        Customer customer2 = new Customer();
        customer2.setAvatarUrl("https://example.org/example");
        customer2.setBalance(new BigDecimal("2.3"));
        customer2.setBio("Bio");
        customer2.setEmail("jane.doe@example.org");
        customer2.setId(1L);
        customer2.setOrders(new ArrayList<>());
        customer2.setPassword("iloveyou");
        customer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer2.setRole(Role.PRODUCER);
        customer2.setUsername("janedoe");

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

        Producer producer6 = new Producer();
        producer6.setAvatarUrl("https://example.org/example");
        producer6.setBalance(new BigDecimal("2.3"));
        producer6.setBio("Bio");
        producer6.setEmail("jane.doe@example.org");
        producer6.setId(1L);
        producer6.setLicenceReports(new ArrayList<>());
        producer6.setPassword("iloveyou");
        producer6.setRating(10.0d);
        producer6.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer6.setRole(Role.PRODUCER);
        producer6.setSoldLicences(new ArrayList<>());
        producer6.setTracks(new HashSet<>());
        producer6.setUsername("janedoe");

        Track track4 = new Track();
        track4.setBpm(1);
        track4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track4.setGenre(GenreType.HYPERPOP);
        track4.setId(1L);
        track4.setKeyType(KeyType.C);
        track4.setLastRatingDelta(0.5d);
        track4.setLicenceTemplates(new ArrayList<>());
        track4.setLikes(1);
        track4.setName("Name");
        track4.setPlays(1);
        track4.setProducer(producer6);
        track4.setPurchasedLicence(new ArrayList<>());
        track4.setRating(new BigDecimal("2.3"));
        track4.setUrlExclusive("https://example.org/example");
        track4.setUrlNonExclusive("https://example.org/example");
        track4.setUrlPremium("https://example.org/example");

        PurchasedLicence purchasedLicence2 = new PurchasedLicence();
        purchasedLicence2.setCustomer(customer2);
        purchasedLicence2.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setId(1L);
        purchasedLicence2.setLicenceTemplate(licenceTemplate2);
        purchasedLicence2.setProducer(producer5);
        purchasedLicence2.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setTrack(track4);
        when(purchasedLicenceRepository.save(Mockito.<PurchasedLicence>any())).thenReturn(purchasedLicence2);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult3);

        // Act
        PurchaseDto actualUpdatePurchaseResult = adminServiceImpl.updatePurchase(1L,
                new PurchaseUpdateRequestDto(null, null, null));

        // Assert
        verify(purchasedLicenceRepository).findById(eq(1L));
        verify(purchasedLicenceRepository).save(isA(PurchasedLicence.class));
        assertNull(actualUpdatePurchaseResult.getProducerId());
        assertNull(actualUpdatePurchaseResult.getProducer());
        assertEquals(1, actualUpdatePurchaseResult.getValidityPeriodDays().intValue());
        assertEquals(1L, actualUpdatePurchaseResult.getPurchaseId().longValue());
        assertEquals(1L, actualUpdatePurchaseResult.getTrackId().longValue());
        assertEquals(LicenceType.NON_EXCLUSIVE, actualUpdatePurchaseResult.getLicenceType());
        assertTrue(actualUpdatePurchaseResult.getAvailablePlatforms().isEmpty());
        BigDecimal expectedPrice = new BigDecimal("2.3");
        assertEquals(expectedPrice, actualUpdatePurchaseResult.getPrice());
        assertSame(ofResult, actualUpdatePurchaseResult.getExpiredDate().toLocalDate());
        assertSame(ofResult2, actualUpdatePurchaseResult.getPurchaseDate().toLocalDate());
    }

    /**
     * Test {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}.
     * <ul>
     *   <li>Then return ProducerId is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updatePurchase(Long, PurchaseUpdateRequestDto); then return ProducerId is 'null'")
    void testUpdatePurchase_thenReturnProducerIdIsNull() {
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
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        purchasedLicence.setPurchaseDate(ofResult.atStartOfDay());
        purchasedLicence.setTrack(track2);
        Optional<PurchasedLicence> ofResult2 = Optional.of(purchasedLicence);

        Customer customer2 = new Customer();
        customer2.setAvatarUrl("https://example.org/example");
        customer2.setBalance(new BigDecimal("2.3"));
        customer2.setBio("Bio");
        customer2.setEmail("jane.doe@example.org");
        customer2.setId(1L);
        customer2.setOrders(new ArrayList<>());
        customer2.setPassword("iloveyou");
        customer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer2.setRole(Role.PRODUCER);
        customer2.setUsername("janedoe");

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

        Producer producer6 = new Producer();
        producer6.setAvatarUrl("https://example.org/example");
        producer6.setBalance(new BigDecimal("2.3"));
        producer6.setBio("Bio");
        producer6.setEmail("jane.doe@example.org");
        producer6.setId(1L);
        producer6.setLicenceReports(new ArrayList<>());
        producer6.setPassword("iloveyou");
        producer6.setRating(10.0d);
        producer6.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer6.setRole(Role.PRODUCER);
        producer6.setSoldLicences(new ArrayList<>());
        producer6.setTracks(new HashSet<>());
        producer6.setUsername("janedoe");

        Track track4 = new Track();
        track4.setBpm(1);
        track4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track4.setGenre(GenreType.HYPERPOP);
        track4.setId(1L);
        track4.setKeyType(KeyType.C);
        track4.setLastRatingDelta(0.5d);
        track4.setLicenceTemplates(new ArrayList<>());
        track4.setLikes(1);
        track4.setName("Name");
        track4.setPlays(1);
        track4.setProducer(producer6);
        track4.setPurchasedLicence(new ArrayList<>());
        track4.setRating(new BigDecimal("2.3"));
        track4.setUrlExclusive("https://example.org/example");
        track4.setUrlNonExclusive("https://example.org/example");
        track4.setUrlPremium("https://example.org/example");

        PurchasedLicence purchasedLicence2 = new PurchasedLicence();
        purchasedLicence2.setCustomer(customer2);
        purchasedLicence2.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setId(1L);
        purchasedLicence2.setLicenceTemplate(licenceTemplate2);
        purchasedLicence2.setProducer(producer5);
        purchasedLicence2.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setTrack(track4);
        when(purchasedLicenceRepository.save(Mockito.<PurchasedLicence>any())).thenReturn(purchasedLicence2);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        BigDecimal price = new BigDecimal("2.3");
        LocalDate ofResult3 = LocalDate.of(1970, 1, 1);

        // Act
        PurchaseDto actualUpdatePurchaseResult = adminServiceImpl.updatePurchase(1L,
                new PurchaseUpdateRequestDto(LicenceType.NON_EXCLUSIVE, price, ofResult3.atStartOfDay()));

        // Assert
        verify(purchasedLicenceRepository).findById(eq(1L));
        verify(purchasedLicenceRepository).save(isA(PurchasedLicence.class));
        assertNull(actualUpdatePurchaseResult.getProducerId());
        assertNull(actualUpdatePurchaseResult.getProducer());
        assertEquals(1, actualUpdatePurchaseResult.getValidityPeriodDays().intValue());
        assertEquals(1L, actualUpdatePurchaseResult.getPurchaseId().longValue());
        assertEquals(1L, actualUpdatePurchaseResult.getTrackId().longValue());
        assertEquals(LicenceType.NON_EXCLUSIVE, actualUpdatePurchaseResult.getLicenceType());
        assertTrue(actualUpdatePurchaseResult.getAvailablePlatforms().isEmpty());
        BigDecimal expectedPrice = new BigDecimal("2.3");
        assertEquals(expectedPrice, actualUpdatePurchaseResult.getPrice());
        assertSame(ofResult3, actualUpdatePurchaseResult.getExpiredDate().toLocalDate());
        assertSame(ofResult, actualUpdatePurchaseResult.getPurchaseDate().toLocalDate());
    }

    /**
     * Test {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updatePurchase(Long, PurchaseUpdateRequestDto); then throw EntityNotFoundException")
    void testUpdatePurchase_thenThrowEntityNotFoundException() {
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
        when(purchasedLicence.getId()).thenThrow(new EntityNotFoundException("An error occurred"));
        when(purchasedLicence.getLicenceTemplate()).thenReturn(licenceTemplate2);
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

        Customer customer2 = new Customer();
        customer2.setAvatarUrl("https://example.org/example");
        customer2.setBalance(new BigDecimal("2.3"));
        customer2.setBio("Bio");
        customer2.setEmail("jane.doe@example.org");
        customer2.setId(1L);
        customer2.setOrders(new ArrayList<>());
        customer2.setPassword("iloveyou");
        customer2.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer2.setRole(Role.PRODUCER);
        customer2.setUsername("janedoe");

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

        Track track4 = new Track();
        track4.setBpm(1);
        track4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track4.setGenre(GenreType.HYPERPOP);
        track4.setId(1L);
        track4.setKeyType(KeyType.C);
        track4.setLastRatingDelta(0.5d);
        track4.setLicenceTemplates(new ArrayList<>());
        track4.setLikes(1);
        track4.setName("Name");
        track4.setPlays(1);
        track4.setProducer(producer5);
        track4.setPurchasedLicence(new ArrayList<>());
        track4.setRating(new BigDecimal("2.3"));
        track4.setUrlExclusive("https://example.org/example");
        track4.setUrlNonExclusive("https://example.org/example");
        track4.setUrlPremium("https://example.org/example");

        LicenceTemplate licenceTemplate3 = new LicenceTemplate();
        licenceTemplate3.setAvailablePlatforms(new ArrayList<>());
        licenceTemplate3.setId(1L);
        licenceTemplate3.setLicenceType(LicenceType.NON_EXCLUSIVE);
        licenceTemplate3.setPrice(new BigDecimal("2.3"));
        licenceTemplate3.setTrack(track4);
        licenceTemplate3.setValidityPeriodDays(1);

        Producer producer6 = new Producer();
        producer6.setAvatarUrl("https://example.org/example");
        producer6.setBalance(new BigDecimal("2.3"));
        producer6.setBio("Bio");
        producer6.setEmail("jane.doe@example.org");
        producer6.setId(1L);
        producer6.setLicenceReports(new ArrayList<>());
        producer6.setPassword("iloveyou");
        producer6.setRating(10.0d);
        producer6.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer6.setRole(Role.PRODUCER);
        producer6.setSoldLicences(new ArrayList<>());
        producer6.setTracks(new HashSet<>());
        producer6.setUsername("janedoe");

        Producer producer7 = new Producer();
        producer7.setAvatarUrl("https://example.org/example");
        producer7.setBalance(new BigDecimal("2.3"));
        producer7.setBio("Bio");
        producer7.setEmail("jane.doe@example.org");
        producer7.setId(1L);
        producer7.setLicenceReports(new ArrayList<>());
        producer7.setPassword("iloveyou");
        producer7.setRating(10.0d);
        producer7.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        producer7.setRole(Role.PRODUCER);
        producer7.setSoldLicences(new ArrayList<>());
        producer7.setTracks(new HashSet<>());
        producer7.setUsername("janedoe");

        Track track5 = new Track();
        track5.setBpm(1);
        track5.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        track5.setGenre(GenreType.HYPERPOP);
        track5.setId(1L);
        track5.setKeyType(KeyType.C);
        track5.setLastRatingDelta(0.5d);
        track5.setLicenceTemplates(new ArrayList<>());
        track5.setLikes(1);
        track5.setName("Name");
        track5.setPlays(1);
        track5.setProducer(producer7);
        track5.setPurchasedLicence(new ArrayList<>());
        track5.setRating(new BigDecimal("2.3"));
        track5.setUrlExclusive("https://example.org/example");
        track5.setUrlNonExclusive("https://example.org/example");
        track5.setUrlPremium("https://example.org/example");

        PurchasedLicence purchasedLicence2 = new PurchasedLicence();
        purchasedLicence2.setCustomer(customer2);
        purchasedLicence2.setExpiredDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setId(1L);
        purchasedLicence2.setLicenceTemplate(licenceTemplate3);
        purchasedLicence2.setProducer(producer6);
        purchasedLicence2.setPurchaseDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        purchasedLicence2.setTrack(track5);
        when(purchasedLicenceRepository.save(Mockito.<PurchasedLicence>any())).thenReturn(purchasedLicence2);
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class,
                () -> adminServiceImpl.updatePurchase(1L, new PurchaseUpdateRequestDto(null, new BigDecimal("2.3"), null)));
        verify(purchasedLicence).getId();
        verify(purchasedLicence).getLicenceTemplate();
        verify(purchasedLicence).setCustomer(isA(Customer.class));
        verify(purchasedLicence).setExpiredDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setId(eq(1L));
        verify(purchasedLicence).setLicenceTemplate(isA(LicenceTemplate.class));
        verify(purchasedLicence).setProducer(isA(Producer.class));
        verify(purchasedLicence).setPurchaseDate(isA(LocalDateTime.class));
        verify(purchasedLicence).setTrack(isA(Track.class));
        verify(purchasedLicenceRepository).findById(eq(1L));
        verify(purchasedLicenceRepository).save(isA(PurchasedLicence.class));
    }

    /**
     * Test {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}.
     * <ul>
     *   <li>Then throw {@link ResponseStatusException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#updatePurchase(Long, PurchaseUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updatePurchase(Long, PurchaseUpdateRequestDto); then throw ResponseStatusException")
    void testUpdatePurchase_thenThrowResponseStatusException() {
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
        when(purchasedLicenceRepository.save(Mockito.<PurchasedLicence>any()))
                .thenThrow(new ResponseStatusException(HttpStatus.OK));
        when(purchasedLicenceRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        BigDecimal price = new BigDecimal("2.3");

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.updatePurchase(1L,
                new PurchaseUpdateRequestDto(LicenceType.NON_EXCLUSIVE, price, LocalDate.of(1970, 1, 1).atStartOfDay())));
        verify(purchasedLicenceRepository).findById(eq(1L));
        verify(purchasedLicenceRepository).save(isA(PurchasedLicence.class));
    }

    /**
     * Test {@link AdminServiceImpl#deleteTrack(Long)}.
     * <ul>
     *   <li>Given {@link TrackRepository} {@link CrudRepository#deleteById(Object)} does nothing.</li>
     *   <li>Then calls {@link CrudRepository#deleteById(Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deleteTrack(Long)}
     */
    @Test
    @DisplayName("Test deleteTrack(Long); given TrackRepository deleteById(Object) does nothing; then calls deleteById(Object)")
    void testDeleteTrack_givenTrackRepositoryDeleteByIdDoesNothing_thenCallsDeleteById() {
        // Arrange
        doNothing().when(trackRepository).deleteById(Mockito.<Long>any());
        when(trackRepository.existsById(Mockito.<Long>any())).thenReturn(true);

        // Act
        adminServiceImpl.deleteTrack(1L);

        // Assert
        verify(trackRepository).deleteById(eq(1L));
        verify(trackRepository).existsById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deleteTrack(Long)}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deleteTrack(Long)}
     */
    @Test
    @DisplayName("Test deleteTrack(Long); then throw EntityNotFoundException")
    void testDeleteTrack_thenThrowEntityNotFoundException() {
        // Arrange
        when(trackRepository.existsById(Mockito.<Long>any())).thenReturn(false);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> adminServiceImpl.deleteTrack(1L));
        verify(trackRepository).existsById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deleteTrack(Long)}.
     * <ul>
     *   <li>Then throw {@link ResponseStatusException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deleteTrack(Long)}
     */
    @Test
    @DisplayName("Test deleteTrack(Long); then throw ResponseStatusException")
    void testDeleteTrack_thenThrowResponseStatusException() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(trackRepository).deleteById(Mockito.<Long>any());
        when(trackRepository.existsById(Mockito.<Long>any())).thenReturn(true);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.deleteTrack(1L));
        verify(trackRepository).deleteById(eq(1L));
        verify(trackRepository).existsById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#getAllUsers()}.
     * <ul>
     *   <li>Given {@link UserMapper}.</li>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllUsers()}
     */
    @Test
    @DisplayName("Test getAllUsers(); given UserMapper; then return Empty")
    void testGetAllUsers_givenUserMapper_thenReturnEmpty() {
        // Arrange
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<UserDto> actualAllUsers = adminServiceImpl.getAllUsers();

        // Assert
        verify(userRepository).findAll();
        assertTrue(actualAllUsers.isEmpty());
    }

    /**
     * Test {@link AdminServiceImpl#getAllUsers()}.
     * <ul>
     *   <li>Then throw {@link ResponseStatusException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#getAllUsers()}
     */
    @Test
    @DisplayName("Test getAllUsers(); then throw ResponseStatusException")
    void testGetAllUsers_thenThrowResponseStatusException() {
        // Arrange
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.findAll()).thenThrow(new ResponseStatusException(HttpStatus.OK));
        TrackRepository trackRepo = mock(TrackRepository.class);
        PurchasedLicenceRepository licRepo = mock(PurchasedLicenceRepository.class);

        // Act and Assert
        assertThrows(ResponseStatusException.class,
                () -> (new AdminServiceImpl(trackRepo, userRepo, licRepo, new UserMapperImpl())).getAllUsers());
        verify(userRepo).findAll();
    }

    /**
     * Test {@link AdminServiceImpl#deleteUser(Long)}.
     * <ul>
     *   <li>Given {@link UserRepository} {@link CrudRepository#delete(Object)} does nothing.</li>
     *   <li>Then calls {@link CrudRepository#delete(Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deleteUser(Long)}
     */
    @Test
    @DisplayName("Test deleteUser(Long); given UserRepository delete(Object) does nothing; then calls delete(Object)")
    void testDeleteUser_givenUserRepositoryDeleteDoesNothing_thenCallsDelete() {
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
        Optional<User> ofResult = Optional.of(user);
        doNothing().when(userRepository).delete(Mockito.<User>any());
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        adminServiceImpl.deleteUser(1L);

        // Assert
        verify(userRepository).delete(isA(User.class));
        verify(userRepository).findById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deleteUser(Long)}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deleteUser(Long)}
     */
    @Test
    @DisplayName("Test deleteUser(Long); then throw EntityNotFoundException")
    void testDeleteUser_thenThrowEntityNotFoundException() {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> adminServiceImpl.deleteUser(1L));
        verify(userRepository).findById(eq(1L));
    }

    /**
     * Test {@link AdminServiceImpl#deleteUser(Long)}.
     * <ul>
     *   <li>Then throw {@link ResponseStatusException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminServiceImpl#deleteUser(Long)}
     */
    @Test
    @DisplayName("Test deleteUser(Long); then throw ResponseStatusException")
    void testDeleteUser_thenThrowResponseStatusException() {
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
        Optional<User> ofResult = Optional.of(user);
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(userRepository).delete(Mockito.<User>any());
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> adminServiceImpl.deleteUser(1L));
        verify(userRepository).delete(isA(User.class));
        verify(userRepository).findById(eq(1L));
    }
}
