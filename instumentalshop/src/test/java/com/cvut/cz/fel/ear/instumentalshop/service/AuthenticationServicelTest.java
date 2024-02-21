package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.User;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.out.LoginDto;
import com.cvut.cz.fel.ear.instumentalshop.repository.CustomerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.ProducerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.UserRepository;
import com.cvut.cz.fel.ear.instumentalshop.service.impl.AuthenticationServiceImpl;
import com.cvut.cz.fel.ear.instumentalshop.service.security.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServicelTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProducerRepository producerRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void login_Successful() {
        String username = "testUser";
        String password = "password";
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        User user = new User();
        String expectedJwt = "jwtToken";
        String expectedRefreshToken = "refreshToken";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(expectedJwt);
        when(jwtService.generateRefreshToken(any(), eq(user))).thenReturn(expectedRefreshToken);

        LoginDto result = authenticationService.login(loginRequestDto);

        assertEquals(expectedJwt, result.getToken());
        assertEquals(expectedRefreshToken, result.getRefreshToken());
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Test
    void getRequestingProducerFromSecurityContext_Successful() {
        String username = "producerUser";
        Producer expectedProducer = new Producer();
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "password");

        when(producerRepository.findProducerByUsername(username)).thenReturn(Optional.of(expectedProducer));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Producer result = authenticationService.getRequestingProducerFromSecurityContext();

        assertEquals(expectedProducer, result);
    }

    @Test
    void getRequestingCustomerFromSecurityContext_Successful() {
        String username = "producerUser";
        Customer expectedProducer = new Customer();
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "password");

        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(expectedProducer));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Customer result = authenticationService.getRequestingCustomerFromSecurityContext();

        assertEquals(expectedProducer, result);
    }

    @Test
    void getRequestingUserFromSecurityContext_Successful() {
        String username = "testUser";
        User expectedUser = new User();
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "password");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User result = authenticationService.getRequestingUserFromSecurityContext();

        assertEquals(expectedUser, result);
    }
}
