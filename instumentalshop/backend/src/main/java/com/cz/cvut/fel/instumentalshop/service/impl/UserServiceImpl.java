package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UserProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementace {@link UserService} pro práci s profilem uživatele.
 *
 * Uloží a načte data z databáze přes {@code UserRepository} a
 * spravuje uložení avatarů na disk.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${app.upload.path}")
    private String avatarsDir;
    private Path avatarsPath;

    /**
     * Inicializace složky pro ukládání avatarů.
     *
     */
    @Override
    @PostConstruct
    public void init() throws IOException {
        avatarsPath = Paths.get(avatarsDir).toAbsolutePath().normalize();
        Files.createDirectories(avatarsPath);
    }

    @Override
    public UserDto depositToBalance(String username, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        return userMapper.toDto(user); 
    }

    @Override
    public UserProfileDto getMyProfile() {
        User user = getCurrentUser();
        return userMapper.toProfileDto(user);
    }

    @Override
    public UserProfileDto updateProfile(UpdateProfileDto dto) {
        User user = getCurrentUser();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setBio(dto.getBio());
        User updated = userRepository.save(user);
        return userMapper.toProfileDto(updated);
    }

    @Override
    public void updateAvatar(MultipartFile file) throws IOException {
        User user = getCurrentUser();
        String ext = getFileExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Path target = avatarsPath.resolve(filename);
        file.transferTo(target.toFile());
        user.setAvatarUrl("/uploads/avatars/" + filename);
        userRepository.save(user);
    }

    /**
     * Získá aktuálně přihlášeného uživatele z kontextu.
     * Používá metodu AuthenticationService.
     *
     * @return entita {@link User}
     * @throws EntityNotFoundException pokud uživatel neexistuje
     */
    private User getCurrentUser() {
        User user = authenticationService.getRequestingUserFromSecurityContext();
        if (user == null) {
            throw new EntityNotFoundException("Authenticated user not found in context");
        }
        return user;
    }

    /**
     * Pomocná metoda pro extrakci přípony souboru z názvu.
     *
     * @param name název souboru
     * @return řetězec s příponou (bez tečky)
     */
    private String getFileExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
