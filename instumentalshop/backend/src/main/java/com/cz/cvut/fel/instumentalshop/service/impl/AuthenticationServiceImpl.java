package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.authentication.out.LoginDto;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.RefreshTokenRequest;
import com.cz.cvut.fel.instumentalshop.exception.InvalidTokenException;
import com.cz.cvut.fel.instumentalshop.exception.UserNotFoundException;
import com.cz.cvut.fel.instumentalshop.repository.CustomerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.security.JWTService;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
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
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

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
