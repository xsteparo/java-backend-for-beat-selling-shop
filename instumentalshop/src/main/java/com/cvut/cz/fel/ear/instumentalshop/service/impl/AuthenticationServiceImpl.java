package com.cvut.cz.fel.ear.instumentalshop.service.impl;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.User;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.out.LoginDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.RefreshTokenRequest;
import com.cvut.cz.fel.ear.instumentalshop.exception.InvalidTokenException;
import com.cvut.cz.fel.ear.instumentalshop.exception.UserNotFoundException;
import com.cvut.cz.fel.ear.instumentalshop.repository.CustomerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.ProducerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.UserRepository;
import com.cvut.cz.fel.ear.instumentalshop.service.security.JWTService;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;

    private final ProducerRepository producerRepository;

    private final JWTService jwtService;

    public LoginDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword()));

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        LoginDto loginDto = new LoginDto();
        loginDto.setToken(jwt);
        loginDto.setRefreshToken(refreshToken);

        return loginDto;
    }

    public LoginDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtService.extractUsername(refreshTokenRequest.getToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            String jwt = jwtService.generateToken(user);

            LoginDto loginDto = new LoginDto();
            loginDto.setToken(jwt);
            loginDto.setRefreshToken(refreshTokenRequest.getToken());

            return loginDto;
        }

        throw new InvalidTokenException("Refresh token is invalid");
    }

    public Customer getRequestingCustomerFromSecurityContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Customer not found"));
    }

    public Producer getRequestingProducerFromSecurityContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return producerRepository.findProducerByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Producer not found"));
    }

    public User getRequestingUserFromSecurityContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
