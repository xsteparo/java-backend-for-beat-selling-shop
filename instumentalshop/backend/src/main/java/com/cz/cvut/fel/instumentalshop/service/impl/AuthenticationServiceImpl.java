package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.RefreshTokenRequest;
import com.cz.cvut.fel.instumentalshop.dto.authentication.out.LoginDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.exception.InvalidTokenException;
import com.cz.cvut.fel.instumentalshop.exception.UserNotFoundException;
import com.cz.cvut.fel.instumentalshop.repository.CustomerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.security.JWTService;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final CustomerRepository customerRepository;

    private final ProducerRepository producerRepository;

    private final PasswordEncoder passwordEncoder;


    private final JWTService jwtService;

    @Value("${app.upload.path}")
    private String uploadPath;

    @Override
    public UserDto getProfileFromToken(String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;

        User user = userRepository.findByUsername(jwtService.extractUsername(token))
                .orElseThrow(() -> new EntityNotFoundException("User not found with token subject"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new JwtException("Invalid or expired JWT token");
        }

        return UserDto.fromEntity(user);
    }

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

    @Override
    public UserDto register(UserCreationRequestDto requestDto) throws IOException {
        // 1) validate uniqueness
        userValidator.validateUserCreationRequest(userRepository, requestDto.getUsername());

        // 2) instantiate correct subclass
        User user;
        if (requestDto.getRole() == Role.CUSTOMER) {
            user = Customer.builder().build();
        } else if (requestDto.getRole() == Role.PRODUCER) {
            user = Producer.builder().build();
        } else {
            throw new IllegalArgumentException("Unsupported role: " + requestDto.getRole());
        }

        // 3) set common fields
        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(requestDto.getRole());
        user.setRegistrationDate(LocalDateTime.now());
        user.setBalance(BigDecimal.ZERO);

        // 4) handle avatar upload
        MultipartFile avatar = requestDto.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            // а) формируем уникальное имя файла с расширением
            String ext = StringUtils.getFilenameExtension(avatar.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + (ext != null ? "." + ext : "");

            // б) находим рабочую директорию приложения
            String userDir = System.getProperty("user.dir");
            // добавляем к ней путь из настроек
            Path avatarsDir = Paths.get(userDir, uploadPath);

            // в) создаём каталоги, если их ещё нет
            Files.createDirectories(avatarsDir);

            // г) сохраняем файл
            Path destinationFile = avatarsDir.resolve(filename);
            avatar.transferTo(destinationFile.toFile());

            // д) сохраняем в БД относительный URL для отдачи статики
            user.setAvatarUrl("/uploads/avatars/" + filename);
        }
        // 5) save to DB (JPA handles JOINED inheritance)
        User saved = userRepository.save(user);

        // 6) map to DTO
        return new UserDto(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getRegistrationDate(),
                saved.getBalance(),
                saved.getAvatarUrl()
        );
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
