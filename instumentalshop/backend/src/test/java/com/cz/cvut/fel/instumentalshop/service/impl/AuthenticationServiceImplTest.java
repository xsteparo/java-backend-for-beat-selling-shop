package com.cz.cvut.fel.instumentalshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.RefreshTokenRequest;
import com.cz.cvut.fel.instumentalshop.dto.authentication.out.LoginDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.exception.InvalidTokenException;
import com.cz.cvut.fel.instumentalshop.repository.CustomerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.security.JWTService;
import com.cz.cvut.fel.instumentalshop.service.security.JWTServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import com.cz.cvut.fel.instumentalshop.util.validator.impl.UserValidatorImpl;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AuthenticationServiceImpl.class})
@ExtendWith(SpringExtension.class)
class AuthenticationServiceImplTest {
    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationServiceImpl authenticationServiceImpl;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private JWTService jWTService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ProducerRepository producerRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserValidator userValidator;

    /**
     * Test {@link AuthenticationServiceImpl#getProfileFromToken(String)}.
     * <ul>
     *   <li>Given {@link Customer} {@link User#getRole()} return {@code PRODUCER}.</li>
     *   <li>Then calls {@link User#getAvatarUrl()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#getProfileFromToken(String)}
     */
    @Test
    @DisplayName("Test getProfileFromToken(String); given Customer getRole() return 'PRODUCER'; then calls getAvatarUrl()")
    void testGetProfileFromToken_givenCustomerGetRoleReturnProducer_thenCallsGetAvatarUrl() {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getRole()).thenReturn(Role.PRODUCER);
        when(customer.getId()).thenReturn(1L);
        when(customer.getAvatarUrl()).thenReturn("https://example.org/example");
        when(customer.getBio()).thenReturn("Bio");
        when(customer.getEmail()).thenReturn("jane.doe@example.org");
        when(customer.getUsername()).thenReturn("janedoe");
        when(customer.getBalance()).thenReturn(new BigDecimal("2.3"));
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        when(customer.getRegistrationDate()).thenReturn(ofResult.atStartOfDay());
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
        Optional<User> ofResult2 = Optional.of(customer);
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult2);
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(true);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        // Act
        UserDto actualProfileFromToken = authenticationServiceImpl.getProfileFromToken("JaneDoe");

        // Assert
        verify(customer).getAvatarUrl();
        verify(customer).getBalance();
        verify(customer).getBio();
        verify(customer).getEmail();
        verify(customer).getId();
        verify(customer).getRegistrationDate();
        verify(customer, atLeast(1)).getRole();
        verify(customer).getUsername();
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer).setPassword(eq("iloveyou"));
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer).setUsername(eq("janedoe"));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("JaneDoe"));
        verify(jWTService).isTokenValid(eq("JaneDoe"), isA(UserDetails.class));
        LocalDateTime registrationDate = actualProfileFromToken.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualProfileFromToken.getBio());
        assertEquals("https://example.org/example", actualProfileFromToken.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualProfileFromToken.getEmail());
        assertEquals("janedoe", actualProfileFromToken.getUsername());
        assertEquals("producer", actualProfileFromToken.getRole());
        assertEquals(1L, actualProfileFromToken.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualProfileFromToken.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link AuthenticationServiceImpl#getProfileFromToken(String)}.
     * <ul>
     *   <li>Given {@link User#User()} AvatarUrl is {@code https://example.org/example}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#getProfileFromToken(String)}
     */
    @Test
    @DisplayName("Test getProfileFromToken(String); given User() AvatarUrl is 'https://example.org/example'")
    void testGetProfileFromToken_givenUserAvatarUrlIsHttpsExampleOrgExample() {
        // Arrange
        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        user.setRegistrationDate(ofResult.atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult2);
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(true);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        // Act
        UserDto actualProfileFromToken = authenticationServiceImpl.getProfileFromToken("JaneDoe");

        // Assert
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("JaneDoe"));
        verify(jWTService).isTokenValid(eq("JaneDoe"), isA(UserDetails.class));
        LocalDateTime registrationDate = actualProfileFromToken.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualProfileFromToken.getBio());
        assertEquals("https://example.org/example", actualProfileFromToken.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualProfileFromToken.getEmail());
        assertEquals("janedoe", actualProfileFromToken.getUsername());
        assertEquals("producer", actualProfileFromToken.getRole());
        assertEquals(1L, actualProfileFromToken.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualProfileFromToken.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link AuthenticationServiceImpl#getProfileFromToken(String)}.
     * <ul>
     *   <li>Given {@link UserRepository} {@link UserRepository#findByUsername(String)} return empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#getProfileFromToken(String)}
     */
    @Test
    @DisplayName("Test getProfileFromToken(String); given UserRepository findByUsername(String) return empty")
    void testGetProfileFromToken_givenUserRepositoryFindByUsernameReturnEmpty() {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(emptyResult);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> authenticationServiceImpl.getProfileFromToken("JaneDoe"));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("JaneDoe"));
    }

    /**
     * Test {@link AuthenticationServiceImpl#getProfileFromToken(String)}.
     * <ul>
     *   <li>Given {@link UserRepository}.</li>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#getProfileFromToken(String)}
     */
    @Test
    @DisplayName("Test getProfileFromToken(String); given UserRepository; then throw EntityNotFoundException")
    void testGetProfileFromToken_givenUserRepository_thenThrowEntityNotFoundException() {
        // Arrange
        when(jWTService.extractUsername(Mockito.<String>any())).thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> authenticationServiceImpl.getProfileFromToken("JaneDoe"));
        verify(jWTService).extractUsername(eq("JaneDoe"));
    }

    /**
     * Test {@link AuthenticationServiceImpl#getProfileFromToken(String)}.
     * <ul>
     *   <li>Then throw {@link JwtException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#getProfileFromToken(String)}
     */
    @Test
    @DisplayName("Test getProfileFromToken(String); then throw JwtException")
    void testGetProfileFromToken_thenThrowJwtException() {
        // Arrange
        Customer customer = mock(Customer.class);
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
        Optional<User> ofResult = Optional.of(customer);
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(false);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        // Act and Assert
        assertThrows(JwtException.class, () -> authenticationServiceImpl.getProfileFromToken("JaneDoe"));
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer).setPassword(eq("iloveyou"));
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer).setUsername(eq("janedoe"));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("JaneDoe"));
        verify(jWTService).isTokenValid(eq("JaneDoe"), isA(UserDetails.class));
    }

    /**
     * Test {@link AuthenticationServiceImpl#getProfileFromToken(String)}.
     * <ul>
     *   <li>When {@code Bearer}.</li>
     *   <li>Then calls {@link User#getAvatarUrl()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#getProfileFromToken(String)}
     */
    @Test
    @DisplayName("Test getProfileFromToken(String); when 'Bearer'; then calls getAvatarUrl()")
    void testGetProfileFromToken_whenBearer_thenCallsGetAvatarUrl() {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getRole()).thenReturn(Role.PRODUCER);
        when(customer.getId()).thenReturn(1L);
        when(customer.getAvatarUrl()).thenReturn("https://example.org/example");
        when(customer.getBio()).thenReturn("Bio");
        when(customer.getEmail()).thenReturn("jane.doe@example.org");
        when(customer.getUsername()).thenReturn("janedoe");
        when(customer.getBalance()).thenReturn(new BigDecimal("2.3"));
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        when(customer.getRegistrationDate()).thenReturn(ofResult.atStartOfDay());
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
        Optional<User> ofResult2 = Optional.of(customer);
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult2);
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(true);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        // Act
        UserDto actualProfileFromToken = authenticationServiceImpl.getProfileFromToken("Bearer ");

        // Assert
        verify(customer).getAvatarUrl();
        verify(customer).getBalance();
        verify(customer).getBio();
        verify(customer).getEmail();
        verify(customer).getId();
        verify(customer).getRegistrationDate();
        verify(customer, atLeast(1)).getRole();
        verify(customer).getUsername();
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer).setPassword(eq("iloveyou"));
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer).setUsername(eq("janedoe"));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq(""));
        verify(jWTService).isTokenValid(eq(""), isA(UserDetails.class));
        LocalDateTime registrationDate = actualProfileFromToken.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualProfileFromToken.getBio());
        assertEquals("https://example.org/example", actualProfileFromToken.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualProfileFromToken.getEmail());
        assertEquals("janedoe", actualProfileFromToken.getUsername());
        assertEquals("producer", actualProfileFromToken.getRole());
        assertEquals(1L, actualProfileFromToken.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualProfileFromToken.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link AuthenticationServiceImpl#login(LoginRequestDto)}.
     * <ul>
     *   <li>Then return RefreshToken is {@code ABC123}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#login(LoginRequestDto)}
     */
    @Test
    @DisplayName("Test login(LoginRequestDto); then return RefreshToken is 'ABC123'")
    void testLogin_thenReturnRefreshTokenIsAbc123() {
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
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(jWTService.generateRefreshToken(Mockito.<Map<String, Object>>any(), Mockito.<UserDetails>any()))
                .thenReturn("ABC123");
        when(jWTService.generateToken(Mockito.<UserDetails>any())).thenReturn("ABC123");

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPassword("iloveyou");
        loginRequestDto.setUsername("janedoe");

        // Act
        LoginDto actualLoginResult = authenticationServiceImpl.login(loginRequestDto);

        // Assert
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).generateRefreshToken(isA(Map.class), isA(UserDetails.class));
        verify(jWTService).generateToken(isA(UserDetails.class));
        assertEquals("ABC123", actualLoginResult.getRefreshToken());
        assertEquals("ABC123", actualLoginResult.getToken());
    }

    /**
     * Test {@link AuthenticationServiceImpl#login(LoginRequestDto)}.
     * <ul>
     *   <li>Then throw {@link EntityNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#login(LoginRequestDto)}
     */
    @Test
    @DisplayName("Test login(LoginRequestDto); then throw EntityNotFoundException")
    void testLogin_thenThrowEntityNotFoundException() {
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
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(jWTService.generateToken(Mockito.<UserDetails>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPassword("iloveyou");
        loginRequestDto.setUsername("janedoe");

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> authenticationServiceImpl.login(loginRequestDto));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).generateToken(isA(UserDetails.class));
    }

    /**
     * Test {@link AuthenticationServiceImpl#login(LoginRequestDto)}.
     * <ul>
     *   <li>Then throw {@link UsernameNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#login(LoginRequestDto)}
     */
    @Test
    @DisplayName("Test login(LoginRequestDto); then throw UsernameNotFoundException")
    void testLogin_thenThrowUsernameNotFoundException() {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(emptyResult);

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPassword("iloveyou");
        loginRequestDto.setUsername("janedoe");

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> authenticationServiceImpl.login(loginRequestDto));
        verify(userRepository).findByUsername(eq("janedoe"));
    }

    /**
     * Test {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Given {@link UserRepository}.</li>
     *   <li>Then throw {@link JwtException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); given UserRepository; then throw JwtException")
    void testRegister_givenUserRepository_thenThrowJwtException() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            doThrow(new JwtException("An error occurred")).when(userValidator)
                    .validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act and Assert
            assertThrows(JwtException.class,
                    () -> authenticationServiceImpl.register(new UserCreationRequestDto("janedoe", "jane.doe@example.org",
                            "iloveyou", new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))),
                            Role.PRODUCER)));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
        }
    }

    /**
     * Test {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then calls {@link Files#createDirectories(Path, FileAttribute[])}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then calls createDirectories(Path, FileAttribute[])")
    void testRegister_thenCallsCreateDirectories() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

            User user = new User();
            user.setAvatarUrl("https://example.org/example");
            user.setBalance(new BigDecimal("2.3"));
            user.setBio("Bio");
            user.setEmail("jane.doe@example.org");
            user.setId(1L);
            user.setPassword("iloveyou");
            LocalDate ofResult = LocalDate.of(1970, 1, 1);
            user.setRegistrationDate(ofResult.atStartOfDay());
            user.setRole(Role.PRODUCER);
            user.setUsername("janedoe");
            when(userRepository.save(Mockito.<User>any())).thenReturn(user);
            doNothing().when(userValidator).validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act
            UserDto actualRegisterResult = authenticationServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou",
                            new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), Role.PRODUCER));

            // Assert
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
            mockFiles.verify(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)));
            mockFiles.verify(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)));
            verify(userRepository).save(isA(User.class));
            LocalDateTime registrationDate = actualRegisterResult.getRegistrationDate();
            assertEquals("00:00", registrationDate.toLocalTime().toString());
            LocalDate toLocalDateResult = registrationDate.toLocalDate();
            assertEquals("1970-01-01", toLocalDateResult.toString());
            assertEquals("Bio", actualRegisterResult.getBio());
            assertEquals("PRODUCER", actualRegisterResult.getRole());
            assertEquals("https://example.org/example", actualRegisterResult.getAvatarUrl());
            assertEquals("jane.doe@example.org", actualRegisterResult.getEmail());
            assertEquals("janedoe", actualRegisterResult.getUsername());
            assertEquals(1L, actualRegisterResult.getUserId().longValue());
            BigDecimal expectedBalance = new BigDecimal("2.3");
            assertEquals(expectedBalance, actualRegisterResult.getBalance());
            assertSame(ofResult, toLocalDateResult);
        }
    }

    /**
     * Test {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then calls {@link UserRepository#existsByUsername(String)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then calls existsByUsername(String)")
    void testRegister_thenCallsExistsByUsername() throws IOException {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);

        User user = new User();
        user.setAvatarUrl("https://example.org/example");
        user.setBalance(new BigDecimal("2.3"));
        user.setBio("Bio");
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setPassword("iloveyou");
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        user.setRegistrationDate(ofResult.atStartOfDay());
        user.setRole(Role.PRODUCER);
        user.setUsername("janedoe");
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsByUsername(Mockito.<String>any())).thenReturn(false);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);
        UserValidatorImpl userValidator = new UserValidatorImpl();
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthenticationServiceImpl authenticationServiceImpl = new AuthenticationServiceImpl(authenticationManager,
                userRepository, userValidator, customerRepository, producerRepository, passwordEncoder, new JWTServiceImpl());

        // Act
        UserDto actualRegisterResult = authenticationServiceImpl
                .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou", null, Role.CUSTOMER));

        // Assert
        verify(userRepository).existsByUsername(eq("janedoe"));
        verify(userRepository).save(isA(User.class));
        LocalDateTime registrationDate = actualRegisterResult.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualRegisterResult.getBio());
        assertEquals("PRODUCER", actualRegisterResult.getRole());
        assertEquals("https://example.org/example", actualRegisterResult.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualRegisterResult.getEmail());
        assertEquals("janedoe", actualRegisterResult.getUsername());
        assertEquals(1L, actualRegisterResult.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualRegisterResult.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then calls {@link User#getAvatarUrl()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then calls getAvatarUrl()")
    void testRegister_thenCallsGetAvatarUrl() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));
            mockFiles.when(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)))
                    .thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
            Customer customer = mock(Customer.class);
            when(customer.getAvatarUrl()).thenReturn("https://example.org/example");
            when(customer.getBio()).thenReturn("Bio");
            when(customer.getBalance()).thenReturn(new BigDecimal("2.3"));
            LocalDate ofResult = LocalDate.of(1970, 1, 1);
            when(customer.getRegistrationDate()).thenReturn(ofResult.atStartOfDay());
            when(customer.getRole()).thenReturn(Role.PRODUCER);
            when(customer.getId()).thenReturn(1L);
            when(customer.getEmail()).thenReturn("jane.doe@example.org");
            when(customer.getUsername()).thenReturn("janedoe");
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
            when(userRepository.save(Mockito.<User>any())).thenReturn(customer);
            doNothing().when(userValidator).validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act
            UserDto actualRegisterResult = authenticationServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou",
                            new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), Role.PRODUCER));

            // Assert
            verify(customer).getAvatarUrl();
            verify(customer).getBalance();
            verify(customer).getBio();
            verify(customer).getEmail();
            verify(customer).getId();
            verify(customer).getRegistrationDate();
            verify(customer).getRole();
            verify(customer).getUsername();
            verify(customer).setAvatarUrl(eq("https://example.org/example"));
            verify(customer).setBalance(isA(BigDecimal.class));
            verify(customer).setBio(eq("Bio"));
            verify(customer).setEmail(eq("jane.doe@example.org"));
            verify(customer).setId(eq(1L));
            verify(customer).setPassword(eq("iloveyou"));
            verify(customer).setRegistrationDate(isA(LocalDateTime.class));
            verify(customer).setRole(eq(Role.PRODUCER));
            verify(customer).setUsername(eq("janedoe"));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
            mockFiles.verify(() -> Files.createDirectories(Mockito.<Path>any(), isA(FileAttribute[].class)));
            mockFiles.verify(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)));
            verify(userRepository).save(isA(User.class));
            LocalDateTime registrationDate = actualRegisterResult.getRegistrationDate();
            assertEquals("00:00", registrationDate.toLocalTime().toString());
            LocalDate toLocalDateResult = registrationDate.toLocalDate();
            assertEquals("1970-01-01", toLocalDateResult.toString());
            assertEquals("Bio", actualRegisterResult.getBio());
            assertEquals("PRODUCER", actualRegisterResult.getRole());
            assertEquals("https://example.org/example", actualRegisterResult.getAvatarUrl());
            assertEquals("jane.doe@example.org", actualRegisterResult.getEmail());
            assertEquals("janedoe", actualRegisterResult.getUsername());
            assertEquals(1L, actualRegisterResult.getUserId().longValue());
            BigDecimal expectedBalance = new BigDecimal("2.3");
            assertEquals(expectedBalance, actualRegisterResult.getBalance());
            assertSame(ofResult, toLocalDateResult);
        }
    }

    /**
     * Test {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}.
     * <ul>
     *   <li>Then return RefreshToken is {@code ABC123}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}
     */
    @Test
    @DisplayName("Test refreshToken(RefreshTokenRequest); then return RefreshToken is 'ABC123'")
    void testRefreshToken_thenReturnRefreshTokenIsAbc123() {
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
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(jWTService.generateToken(Mockito.<UserDetails>any())).thenReturn("ABC123");
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(true);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("ABC123");

        // Act
        LoginDto actualRefreshTokenResult = authenticationServiceImpl.refreshToken(refreshTokenRequest);

        // Assert
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("ABC123"));
        verify(jWTService).generateToken(isA(UserDetails.class));
        verify(jWTService).isTokenValid(eq("ABC123"), isA(UserDetails.class));
        assertEquals("ABC123", actualRefreshTokenResult.getRefreshToken());
        assertEquals("ABC123", actualRefreshTokenResult.getToken());
    }

    /**
     * Test {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}.
     * <ul>
     *   <li>Then throw {@link InvalidTokenException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}
     */
    @Test
    @DisplayName("Test refreshToken(RefreshTokenRequest); then throw InvalidTokenException")
    void testRefreshToken_thenThrowInvalidTokenException() {
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
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(false);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("ABC123");

        // Act and Assert
        assertThrows(InvalidTokenException.class, () -> authenticationServiceImpl.refreshToken(refreshTokenRequest));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("ABC123"));
        verify(jWTService).isTokenValid(eq("ABC123"), isA(UserDetails.class));
    }

    /**
     * Test {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}.
     * <ul>
     *   <li>Then throw {@link JwtException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}
     */
    @Test
    @DisplayName("Test refreshToken(RefreshTokenRequest); then throw JwtException")
    void testRefreshToken_thenThrowJwtException() {
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
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(ofResult);
        when(jWTService.generateToken(Mockito.<UserDetails>any())).thenThrow(new JwtException("An error occurred"));
        when(jWTService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any())).thenReturn(true);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("ABC123");

        // Act and Assert
        assertThrows(JwtException.class, () -> authenticationServiceImpl.refreshToken(refreshTokenRequest));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("ABC123"));
        verify(jWTService).generateToken(isA(UserDetails.class));
        verify(jWTService).isTokenValid(eq("ABC123"), isA(UserDetails.class));
    }

    /**
     * Test {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}.
     * <ul>
     *   <li>Then throw {@link UsernameNotFoundException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AuthenticationServiceImpl#refreshToken(RefreshTokenRequest)}
     */
    @Test
    @DisplayName("Test refreshToken(RefreshTokenRequest); then throw UsernameNotFoundException")
    void testRefreshToken_thenThrowUsernameNotFoundException() {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByUsername(Mockito.<String>any())).thenReturn(emptyResult);
        when(jWTService.extractUsername(Mockito.<String>any())).thenReturn("janedoe");

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("ABC123");

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> authenticationServiceImpl.refreshToken(refreshTokenRequest));
        verify(userRepository).findByUsername(eq("janedoe"));
        verify(jWTService).extractUsername(eq("ABC123"));
    }

}
