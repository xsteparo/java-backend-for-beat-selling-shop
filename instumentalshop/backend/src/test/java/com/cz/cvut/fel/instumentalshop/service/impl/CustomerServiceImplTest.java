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
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.balance.in.IncreaseBalanceRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.BalanceMapperImpl;
import com.cz.cvut.fel.instumentalshop.dto.mapper.CustomerMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.CustomerMapperImpl;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.CustomerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.security.JWTServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import com.cz.cvut.fel.instumentalshop.util.validator.impl.UserValidatorImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {CustomerServiceImpl.class})
@ExtendWith(SpringExtension.class)
class CustomerServiceImplTest {
    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private BalanceMapper balanceMapper;

    @MockBean
    private CustomerMapper customerMapper;

    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserValidator userValidator;

    /**
     * Test {@link CustomerServiceImpl#register(UserCreationRequestDto)}.
     * <p>
     * Method under test: {@link CustomerServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto)")
    void testRegister() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));

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
            when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer);
            UserDto userDto = new UserDto();
            when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);
            doNothing().when(userValidator).validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act
            UserDto actualRegisterResult = customerServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou", null, Role.PRODUCER));

            // Assert
            verify(customerMapper).toResponseDto(isA(Customer.class));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
            verify(customerRepository).save(isA(Customer.class));
            assertSame(userDto, actualRegisterResult);
        }
    }

    /**
     * Test {@link CustomerServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then calls {@link Files#newOutputStream(Path, OpenOption[])}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then calls newOutputStream(Path, OpenOption[])")
    void testRegister_thenCallsNewOutputStream() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));

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
            when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer);
            UserDto userDto = new UserDto();
            when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);
            doNothing().when(userValidator).validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act
            UserDto actualRegisterResult = customerServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou",
                            new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), Role.PRODUCER));

            // Assert
            verify(customerMapper).toResponseDto(isA(Customer.class));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
            mockFiles.verify(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)));
            verify(customerRepository).save(isA(Customer.class));
            assertSame(userDto, actualRegisterResult);
        }
    }

    /**
     * Test {@link CustomerServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then return RegistrationDate toLocalTime toString is {@code 00:00}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then return RegistrationDate toLocalTime toString is '00:00'")
    void testRegister_thenReturnRegistrationDateToLocalTimeToStringIs0000() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));

            ArrayList<AuthenticationProvider> providers = new ArrayList<>();
            providers.add(new RunAsImplAuthenticationProvider());
            ProviderManager authenticationManager = new ProviderManager(providers);
            UserRepository userRepository = mock(UserRepository.class);
            UserValidatorImpl userValidator = new UserValidatorImpl();
            CustomerRepository customerRepository = mock(CustomerRepository.class);
            ProducerRepository producerRepository = mock(ProducerRepository.class);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(authenticationManager,
                    userRepository, userValidator, customerRepository, producerRepository, passwordEncoder, new JWTServiceImpl());

            Customer customer = new Customer();
            customer.setAvatarUrl("https://example.org/example");
            customer.setBalance(new BigDecimal("2.3"));
            customer.setBio("Bio");
            customer.setEmail("jane.doe@example.org");
            customer.setId(1L);
            customer.setOrders(new ArrayList<>());
            customer.setPassword("iloveyou");
            LocalDate ofResult = LocalDate.of(1970, 1, 1);
            customer.setRegistrationDate(ofResult.atStartOfDay());
            customer.setRole(Role.PRODUCER);
            customer.setUsername("janedoe");
            CustomerRepository customerRepository2 = mock(CustomerRepository.class);
            when(customerRepository2.save(Mockito.<Customer>any())).thenReturn(customer);
            UserRepository userRepository2 = mock(UserRepository.class);
            when(userRepository2.existsByUsername(Mockito.<String>any())).thenReturn(false);
            CustomerMapperImpl customerMapper = new CustomerMapperImpl();
            BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
            BCryptPasswordEncoder passwordEncoder2 = new BCryptPasswordEncoder();
            CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository2,
                    userRepository2, customerMapper, balanceMapper, passwordEncoder2, new UserValidatorImpl());

            // Act
            UserDto actualRegisterResult = customerServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou",
                            new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), Role.PRODUCER));

            // Assert
            verify(userRepository2).existsByUsername(eq("janedoe"));
            mockFiles.verify(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)));
            verify(customerRepository2).save(isA(Customer.class));
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
     * Test {@link CustomerServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then throw IllegalArgumentException")
    void testRegister_thenThrowIllegalArgumentException() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));
            doNothing().when(userValidator).validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());
            MultipartFile avatar = mock(MultipartFile.class);
            when(avatar.isEmpty()).thenThrow(new IllegalArgumentException("foo"));

            // Act and Assert
            assertThrows(IllegalArgumentException.class, () -> customerServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou", avatar, Role.PRODUCER)));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
            verify(avatar).isEmpty();
        }
    }

    /**
     * Test {@link CustomerServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>Then throw {@link RuntimeException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); then throw RuntimeException")
    void testRegister_thenThrowRuntimeException() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));
            doThrow(new RuntimeException("foo")).when(userValidator)
                    .validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act and Assert
            assertThrows(RuntimeException.class,
                    () -> customerServiceImpl.register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou",
                            new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), Role.PRODUCER)));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
        }
    }

    /**
     * Test {@link CustomerServiceImpl#register(UserCreationRequestDto)}.
     * <ul>
     *   <li>When {@link ByteArrayInputStream#ByteArrayInputStream(byte[])} with empty array of {@code byte}.</li>
     *   <li>Then return {@link UserDto#UserDto()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#register(UserCreationRequestDto)}
     */
    @Test
    @DisplayName("Test register(UserCreationRequestDto); when ByteArrayInputStream(byte[]) with empty array of byte; then return UserDto()")
    void testRegister_whenByteArrayInputStreamWithEmptyArrayOfByte_thenReturnUserDto() throws IOException {
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {

            // Arrange
            mockFiles.when(() -> Files.newOutputStream(Mockito.<Path>any(), isA(OpenOption[].class)))
                    .thenReturn(new ByteArrayOutputStream(1));

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
            when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer);
            UserDto userDto = new UserDto();
            when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);
            doNothing().when(userValidator).validateUserCreationRequest(Mockito.<UserRepository>any(), Mockito.<String>any());

            // Act
            UserDto actualRegisterResult = customerServiceImpl
                    .register(new UserCreationRequestDto("janedoe", "jane.doe@example.org", "iloveyou",
                            new MockMultipartFile("Name", new ByteArrayInputStream(new byte[]{})), Role.PRODUCER));

            // Assert
            verify(customerMapper).toResponseDto(isA(Customer.class));
            verify(userValidator).validateUserCreationRequest(isA(UserRepository.class), eq("janedoe"));
            verify(customerRepository).save(isA(Customer.class));
            assertSame(userDto, actualRegisterResult);
        }
    }

    /**
     * Test {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}.
     * <p>
     * Method under test: {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}
     */
    @Test
    @DisplayName("Test increaseBalance(IncreaseBalanceRequestDto)")
    void testIncreaseBalance() {
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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        BalanceResponseDto balanceResponseDto = new BalanceResponseDto(1L, new BigDecimal("2.3"));

        when(balanceMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(balanceResponseDto);

        IncreaseBalanceRequestDto requestDto = new IncreaseBalanceRequestDto();
        requestDto.setBalanceRecharge(new BigDecimal("2.3"));

        // Act
        BalanceResponseDto actualIncreaseBalanceResult = customerServiceImpl.increaseBalance(requestDto);

        // Assert
        verify(balanceMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertSame(balanceResponseDto, actualIncreaseBalanceResult);
    }

    /**
     * Test {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}.
     * <ul>
     *   <li>Given {@link BalanceMapper} {@link BalanceMapper#toResponseDto(Customer)} throw {@link RuntimeException#RuntimeException(String)} with {@code foo}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}
     */
    @Test
    @DisplayName("Test increaseBalance(IncreaseBalanceRequestDto); given BalanceMapper toResponseDto(Customer) throw RuntimeException(String) with 'foo'")
    void testIncreaseBalance_givenBalanceMapperToResponseDtoThrowRuntimeExceptionWithFoo() {
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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        when(balanceMapper.toResponseDto(Mockito.<Customer>any())).thenThrow(new RuntimeException("foo"));

        IncreaseBalanceRequestDto requestDto = new IncreaseBalanceRequestDto();
        requestDto.setBalanceRecharge(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> customerServiceImpl.increaseBalance(requestDto));
        verify(balanceMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
    }

    /**
     * Test {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}.
     * <ul>
     *   <li>Given {@link CustomerRepository} {@link CrudRepository#save(Object)} throw {@link RuntimeException#RuntimeException(String)} with {@code foo}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}
     */
    @Test
    @DisplayName("Test increaseBalance(IncreaseBalanceRequestDto); given CustomerRepository save(Object) throw RuntimeException(String) with 'foo'")
    void testIncreaseBalance_givenCustomerRepositorySaveThrowRuntimeExceptionWithFoo() {
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
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.save(Mockito.<Customer>any())).thenThrow(new RuntimeException("foo"));
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl());

        IncreaseBalanceRequestDto requestDto = new IncreaseBalanceRequestDto();
        requestDto.setBalanceRecharge(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> customerServiceImpl.increaseBalance(requestDto));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
    }

    /**
     * Test {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}.
     * <ul>
     *   <li>Then calls {@link Customer#setOrders(List)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}
     */
    @Test
    @DisplayName("Test increaseBalance(IncreaseBalanceRequestDto); then calls setOrders(List)")
    void testIncreaseBalance_thenCallsSetOrders() {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getBalance()).thenReturn(new BigDecimal("2.3"));
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        BalanceResponseDto balanceResponseDto = new BalanceResponseDto(1L, new BigDecimal("2.3"));

        when(balanceMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(balanceResponseDto);

        IncreaseBalanceRequestDto requestDto = new IncreaseBalanceRequestDto();
        requestDto.setBalanceRecharge(new BigDecimal("2.3"));

        // Act
        BalanceResponseDto actualIncreaseBalanceResult = customerServiceImpl.increaseBalance(requestDto);

        // Assert
        verify(customer).setOrders(isA(List.class));
        verify(customer).getBalance();
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer, atLeast(1)).setBalance(Mockito.<BigDecimal>any());
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer).setPassword(eq("iloveyou"));
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer).setUsername(eq("janedoe"));
        verify(balanceMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertSame(balanceResponseDto, actualIncreaseBalanceResult);
    }

    /**
     * Test {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}.
     * <ul>
     *   <li>Then return UserId longValue is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#increaseBalance(IncreaseBalanceRequestDto)}
     */
    @Test
    @DisplayName("Test increaseBalance(IncreaseBalanceRequestDto); then return UserId longValue is one")
    void testIncreaseBalance_thenReturnUserIdLongValueIsOne() {
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
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl());

        IncreaseBalanceRequestDto requestDto = new IncreaseBalanceRequestDto();
        requestDto.setBalanceRecharge(new BigDecimal("2.3"));

        // Act
        BalanceResponseDto actualIncreaseBalanceResult = customerServiceImpl.increaseBalance(requestDto);

        // Assert
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertEquals(1L, actualIncreaseBalanceResult.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("4.6");
        assertEquals(expectedBalance, actualIncreaseBalanceResult.getBalance());
    }

    /**
     * Test {@link CustomerServiceImpl#getBalance()}.
     * <ul>
     *   <li>Then return {@link BalanceResponseDto#BalanceResponseDto(Long, BigDecimal)} with userId is one and balance is {@link BigDecimal#BigDecimal(String)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getBalance()}
     */
    @Test
    @DisplayName("Test getBalance(); then return BalanceResponseDto(Long, BigDecimal) with userId is one and balance is BigDecimal(String)")
    void testGetBalance_thenReturnBalanceResponseDtoWithUserIdIsOneAndBalanceIsBigDecimal() {
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
        BalanceResponseDto balanceResponseDto = new BalanceResponseDto(1L, new BigDecimal("2.3"));

        when(balanceMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(balanceResponseDto);

        // Act
        BalanceResponseDto actualBalance = customerServiceImpl.getBalance();

        // Assert
        verify(balanceMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        assertSame(balanceResponseDto, actualBalance);
    }

    /**
     * Test {@link CustomerServiceImpl#getBalance()}.
     * <ul>
     *   <li>Then return UserId longValue is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getBalance()}
     */
    @Test
    @DisplayName("Test getBalance(); then return UserId longValue is one")
    void testGetBalance_thenReturnUserIdLongValueIsOne() {
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
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Act
        BalanceResponseDto actualBalance = (new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl())).getBalance();

        // Assert
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        assertEquals(1L, actualBalance.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualBalance.getBalance());
    }

    /**
     * Test {@link CustomerServiceImpl#getBalance()}.
     * <ul>
     *   <li>Then throw {@link RuntimeException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getBalance()}
     */
    @Test
    @DisplayName("Test getBalance(); then throw RuntimeException")
    void testGetBalance_thenThrowRuntimeException() {
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
        when(balanceMapper.toResponseDto(Mockito.<Customer>any())).thenThrow(new RuntimeException("foo"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> customerServiceImpl.getBalance());
        verify(balanceMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
    }

    /**
     * Test {@link CustomerServiceImpl#getCustomerById(Long)}.
     * <ul>
     *   <li>Given {@link Customer} {@link User#getRole()} return {@code PRODUCER}.</li>
     *   <li>Then calls {@link Customer#setOrders(List)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getCustomerById(Long)}
     */
    @Test
    @DisplayName("Test getCustomerById(Long); given Customer getRole() return 'PRODUCER'; then calls setOrders(List)")
    void testGetCustomerById_givenCustomerGetRoleReturnProducer_thenCallsSetOrders() {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        UserRepository userRepository = mock(UserRepository.class);
        UserValidatorImpl userValidator = new UserValidatorImpl();
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(authenticationManager,
                userRepository, userValidator, customerRepository, producerRepository, passwordEncoder, new JWTServiceImpl());

        Customer customer = mock(Customer.class);
        when(customer.getRole()).thenReturn(Role.PRODUCER);
        when(customer.getId()).thenReturn(1L);
        when(customer.getAvatarUrl()).thenReturn("https://example.org/example");
        when(customer.getBio()).thenReturn("Bio");
        when(customer.getEmail()).thenReturn("jane.doe@example.org");
        when(customer.getUsername()).thenReturn("janedoe");
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        when(customer.getRegistrationDate()).thenReturn(ofResult.atStartOfDay());
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        Optional<Customer> ofResult2 = Optional.of(customer);
        CustomerRepository customerRepository2 = mock(CustomerRepository.class);
        when(customerRepository2.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        UserRepository userRepository2 = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder2 = new BCryptPasswordEncoder();

        // Act
        UserDto actualCustomerById = (new CustomerServiceImpl(authenticationService, customerRepository2, userRepository2,
                customerMapper, balanceMapper, passwordEncoder2, new UserValidatorImpl())).getCustomerById(1L);

        // Assert
        verify(customer).setOrders(isA(List.class));
        verify(customer).getAvatarUrl();
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
        verify(customerRepository2).findById(eq(1L));
        LocalDateTime registrationDate = actualCustomerById.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualCustomerById.getBio());
        assertEquals("PRODUCER", actualCustomerById.getRole());
        assertEquals("https://example.org/example", actualCustomerById.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualCustomerById.getEmail());
        assertEquals("janedoe", actualCustomerById.getUsername());
        assertEquals(1L, actualCustomerById.getUserId().longValue());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link CustomerServiceImpl#getCustomerById(Long)}.
     * <ul>
     *   <li>Then return RegistrationDate toLocalTime toString is {@code 00:00}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getCustomerById(Long)}
     */
    @Test
    @DisplayName("Test getCustomerById(Long); then return RegistrationDate toLocalTime toString is '00:00'")
    void testGetCustomerById_thenReturnRegistrationDateToLocalTimeToStringIs0000() {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        UserRepository userRepository = mock(UserRepository.class);
        UserValidatorImpl userValidator = new UserValidatorImpl();
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(authenticationManager,
                userRepository, userValidator, customerRepository, producerRepository, passwordEncoder, new JWTServiceImpl());

        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        customer.setRegistrationDate(ofResult.atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        Optional<Customer> ofResult2 = Optional.of(customer);
        CustomerRepository customerRepository2 = mock(CustomerRepository.class);
        when(customerRepository2.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        UserRepository userRepository2 = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder2 = new BCryptPasswordEncoder();

        // Act
        UserDto actualCustomerById = (new CustomerServiceImpl(authenticationService, customerRepository2, userRepository2,
                customerMapper, balanceMapper, passwordEncoder2, new UserValidatorImpl())).getCustomerById(1L);

        // Assert
        verify(customerRepository2).findById(eq(1L));
        LocalDateTime registrationDate = actualCustomerById.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualCustomerById.getBio());
        assertEquals("PRODUCER", actualCustomerById.getRole());
        assertEquals("https://example.org/example", actualCustomerById.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualCustomerById.getEmail());
        assertEquals("janedoe", actualCustomerById.getUsername());
        assertEquals(1L, actualCustomerById.getUserId().longValue());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link CustomerServiceImpl#getCustomerById(Long)}.
     * <ul>
     *   <li>Then return {@link UserDto#UserDto()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getCustomerById(Long)}
     */
    @Test
    @DisplayName("Test getCustomerById(Long); then return UserDto()")
    void testGetCustomerById_thenReturnUserDto() {
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
        Optional<Customer> ofResult = Optional.of(customer);
        when(customerRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        UserDto userDto = new UserDto();
        when(customerMapper.toGetResponseDto(Mockito.<Customer>any())).thenReturn(userDto);

        // Act
        UserDto actualCustomerById = customerServiceImpl.getCustomerById(1L);

        // Assert
        verify(customerMapper).toGetResponseDto(isA(Customer.class));
        verify(customerRepository).findById(eq(1L));
        assertSame(userDto, actualCustomerById);
    }

    /**
     * Test {@link CustomerServiceImpl#getCustomerById(Long)}.
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getCustomerById(Long)}
     */
    @Test
    @DisplayName("Test getCustomerById(Long); then throw IllegalArgumentException")
    void testGetCustomerById_thenThrowIllegalArgumentException() {
        // Arrange
        Optional<Customer> emptyResult = Optional.empty();
        when(customerRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> customerServiceImpl.getCustomerById(1L));
        verify(customerRepository).findById(eq(1L));
    }

    /**
     * Test {@link CustomerServiceImpl#getCustomerById(Long)}.
     * <ul>
     *   <li>Then throw {@link RuntimeException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#getCustomerById(Long)}
     */
    @Test
    @DisplayName("Test getCustomerById(Long); then throw RuntimeException")
    void testGetCustomerById_thenThrowRuntimeException() {
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
        Optional<Customer> ofResult = Optional.of(customer);
        when(customerRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(customerMapper.toGetResponseDto(Mockito.<Customer>any())).thenThrow(new RuntimeException("foo"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> customerServiceImpl.getCustomerById(1L));
        verify(customerMapper).toGetResponseDto(isA(Customer.class));
        verify(customerRepository).findById(eq(1L));
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto)")
    void testUpdateCustomer() {
        // Arrange
        Customer customer = mock(Customer.class);
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserDto userDto = new UserDto();
        when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl.updateCustomer(new UserUpdateRequestDto(null, "iloveyou"));

        // Assert
        verify(customer).setOrders(isA(List.class));
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer, atLeast(1)).setPassword(Mockito.<String>any());
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer).setUsername(eq("janedoe"));
        verify(customerMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertSame(userDto, actualUpdateCustomerResult);
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto)")
    void testUpdateCustomer2() {
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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserDto userDto = new UserDto();
        when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl
                .updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou"));

        // Assert
        verify(customerMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertSame(userDto, actualUpdateCustomerResult);
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto)")
    void testUpdateCustomer3() {
        // Arrange
        Customer customer = mock(Customer.class);
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserDto userDto = new UserDto();
        when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl.updateCustomer(new UserUpdateRequestDto("janedoe", null));

        // Assert
        verify(customer).setOrders(isA(List.class));
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer).setPassword(eq("iloveyou"));
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer, atLeast(1)).setUsername(eq("janedoe"));
        verify(customerMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertSame(userDto, actualUpdateCustomerResult);
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <ul>
     *   <li>Given {@link Customer} {@link User#getRole()} return {@code null}.</li>
     *   <li>Then calls {@link User#getAvatarUrl()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto); given Customer getRole() return 'null'; then calls getAvatarUrl()")
    void testUpdateCustomer_givenCustomerGetRoleReturnNull_thenCallsGetAvatarUrl() {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getRole()).thenReturn(null);
        when(customer.getId()).thenReturn(1L);
        when(customer.getAvatarUrl()).thenReturn("https://example.org/example");
        when(customer.getBio()).thenReturn("Bio");
        when(customer.getEmail()).thenReturn("jane.doe@example.org");
        when(customer.getUsername()).thenReturn("janedoe");
        when(customer.getBalance()).thenReturn(new BigDecimal("2.3"));
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        when(customer.getRegistrationDate()).thenReturn(ofResult.atStartOfDay());
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl());

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl
                .updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou"));

        // Assert
        verify(customer).setOrders(isA(List.class));
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
        verify(customer, atLeast(1)).setPassword(Mockito.<String>any());
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer, atLeast(1)).setUsername(eq("janedoe"));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        LocalDateTime registrationDate = actualUpdateCustomerResult.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualUpdateCustomerResult.getBio());
        assertEquals("https://example.org/example", actualUpdateCustomerResult.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualUpdateCustomerResult.getEmail());
        assertEquals("janedoe", actualUpdateCustomerResult.getUsername());
        assertEquals(1L, actualUpdateCustomerResult.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualUpdateCustomerResult.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <ul>
     *   <li>Given {@link Customer} {@link User#getRole()} return {@code PRODUCER}.</li>
     *   <li>Then return Role is {@code PRODUCER}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto); given Customer getRole() return 'PRODUCER'; then return Role is 'PRODUCER'")
    void testUpdateCustomer_givenCustomerGetRoleReturnProducer_thenReturnRoleIsProducer() {
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
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl());

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl
                .updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou"));

        // Assert
        verify(customer).setOrders(isA(List.class));
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
        verify(customer, atLeast(1)).setPassword(Mockito.<String>any());
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer, atLeast(1)).setUsername(eq("janedoe"));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        LocalDateTime registrationDate = actualUpdateCustomerResult.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualUpdateCustomerResult.getBio());
        assertEquals("PRODUCER", actualUpdateCustomerResult.getRole());
        assertEquals("https://example.org/example", actualUpdateCustomerResult.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualUpdateCustomerResult.getEmail());
        assertEquals("janedoe", actualUpdateCustomerResult.getUsername());
        assertEquals(1L, actualUpdateCustomerResult.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualUpdateCustomerResult.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <ul>
     *   <li>Given {@link CustomerMapper} {@link CustomerMapper#toResponseDto(Customer)} throw {@link RuntimeException#RuntimeException(String)} with {@code foo}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto); given CustomerMapper toResponseDto(Customer) throw RuntimeException(String) with 'foo'")
    void testUpdateCustomer_givenCustomerMapperToResponseDtoThrowRuntimeExceptionWithFoo() {
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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenThrow(new RuntimeException("foo"));

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> customerServiceImpl.updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou")));
        verify(customerMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <ul>
     *   <li>Given {@link CustomerRepository} {@link CrudRepository#save(Object)} throw {@link RuntimeException#RuntimeException(String)} with {@code foo}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto); given CustomerRepository save(Object) throw RuntimeException(String) with 'foo'")
    void testUpdateCustomer_givenCustomerRepositorySaveThrowRuntimeExceptionWithFoo() {
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
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.save(Mockito.<Customer>any())).thenThrow(new RuntimeException("foo"));
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl());

        // Act and Assert
        assertThrows(RuntimeException.class,
                () -> customerServiceImpl.updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou")));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <ul>
     *   <li>Then return Role is {@code PRODUCER}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto); then return Role is 'PRODUCER'")
    void testUpdateCustomer_thenReturnRoleIsProducer() {
        // Arrange
        Customer customer = new Customer();
        customer.setAvatarUrl("https://example.org/example");
        customer.setBalance(new BigDecimal("2.3"));
        customer.setBio("Bio");
        customer.setEmail("jane.doe@example.org");
        customer.setId(1L);
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        customer.setRegistrationDate(ofResult.atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        AuthenticationServiceImpl authenticationService = mock(AuthenticationServiceImpl.class);
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserRepository userRepository = mock(UserRepository.class);
        CustomerMapperImpl customerMapper = new CustomerMapperImpl();
        BalanceMapperImpl balanceMapper = new BalanceMapperImpl();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl(authenticationService, customerRepository,
                userRepository, customerMapper, balanceMapper, passwordEncoder, new UserValidatorImpl());

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl
                .updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou"));

        // Assert
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        LocalDateTime registrationDate = actualUpdateCustomerResult.getRegistrationDate();
        assertEquals("00:00", registrationDate.toLocalTime().toString());
        LocalDate toLocalDateResult = registrationDate.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Bio", actualUpdateCustomerResult.getBio());
        assertEquals("PRODUCER", actualUpdateCustomerResult.getRole());
        assertEquals("https://example.org/example", actualUpdateCustomerResult.getAvatarUrl());
        assertEquals("jane.doe@example.org", actualUpdateCustomerResult.getEmail());
        assertEquals("janedoe", actualUpdateCustomerResult.getUsername());
        assertEquals(1L, actualUpdateCustomerResult.getUserId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, actualUpdateCustomerResult.getBalance());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}.
     * <ul>
     *   <li>Then return {@link UserDto#UserDto()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#updateCustomer(UserUpdateRequestDto)}
     */
    @Test
    @DisplayName("Test updateCustomer(UserUpdateRequestDto); then return UserDto()")
    void testUpdateCustomer_thenReturnUserDto() {
        // Arrange
        Customer customer = mock(Customer.class);
        doNothing().when(customer).setOrders(Mockito.<List<PurchasedLicence>>any());
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
        customer.setOrders(new ArrayList<>());
        customer.setPassword("iloveyou");
        customer.setRegistrationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        customer.setRole(Role.PRODUCER);
        customer.setUsername("janedoe");
        when(authenticationService.getRequestingCustomerFromSecurityContext()).thenReturn(customer);

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
        when(customerRepository.save(Mockito.<Customer>any())).thenReturn(customer2);
        UserDto userDto = new UserDto();
        when(customerMapper.toResponseDto(Mockito.<Customer>any())).thenReturn(userDto);

        // Act
        UserDto actualUpdateCustomerResult = customerServiceImpl
                .updateCustomer(new UserUpdateRequestDto("janedoe", "iloveyou"));

        // Assert
        verify(customer).setOrders(isA(List.class));
        verify(customer).setAvatarUrl(eq("https://example.org/example"));
        verify(customer).setBalance(isA(BigDecimal.class));
        verify(customer).setBio(eq("Bio"));
        verify(customer).setEmail(eq("jane.doe@example.org"));
        verify(customer).setId(eq(1L));
        verify(customer, atLeast(1)).setPassword(Mockito.<String>any());
        verify(customer).setRegistrationDate(isA(LocalDateTime.class));
        verify(customer).setRole(eq(Role.PRODUCER));
        verify(customer, atLeast(1)).setUsername(eq("janedoe"));
        verify(customerMapper).toResponseDto(isA(Customer.class));
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(customerRepository).save(isA(Customer.class));
        assertSame(userDto, actualUpdateCustomerResult);
    }

    /**
     * Test {@link CustomerServiceImpl#deleteCustomer()}.
     * <ul>
     *   <li>Given {@link CustomerRepository} {@link CrudRepository#delete(Object)} does nothing.</li>
     *   <li>Then calls {@link CrudRepository#delete(Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#deleteCustomer()}
     */
    @Test
    @DisplayName("Test deleteCustomer(); given CustomerRepository delete(Object) does nothing; then calls delete(Object)")
    void testDeleteCustomer_givenCustomerRepositoryDeleteDoesNothing_thenCallsDelete() {
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
        doNothing().when(customerRepository).delete(Mockito.<Customer>any());
        doNothing().when(userValidator).validateCustomerDeletionRequest(Mockito.<Customer>any());

        // Act
        customerServiceImpl.deleteCustomer();

        // Assert
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(userValidator).validateCustomerDeletionRequest(isA(Customer.class));
        verify(customerRepository).delete(isA(Customer.class));
    }

    /**
     * Test {@link CustomerServiceImpl#deleteCustomer()}.
     * <ul>
     *   <li>Given {@link CustomerRepository}.</li>
     *   <li>Then throw {@link RuntimeException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CustomerServiceImpl#deleteCustomer()}
     */
    @Test
    @DisplayName("Test deleteCustomer(); given CustomerRepository; then throw RuntimeException")
    void testDeleteCustomer_givenCustomerRepository_thenThrowRuntimeException() {
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
        doThrow(new RuntimeException("foo")).when(userValidator).validateCustomerDeletionRequest(Mockito.<Customer>any());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> customerServiceImpl.deleteCustomer());
        verify(authenticationService).getRequestingCustomerFromSecurityContext();
        verify(userValidator).validateCustomerDeletionRequest(isA(Customer.class));
    }
}
