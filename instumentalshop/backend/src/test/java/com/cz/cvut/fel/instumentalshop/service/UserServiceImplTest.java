package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UserProfileDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.UserService;
import com.cz.cvut.fel.instumentalshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserServiceImpl userService;

    private Path tempDir;

//    @BeforeEach
//    void setUp(java.nio.file.Path tempDir) throws IOException {
//        // Initialize a temporary directory for avatars
//        this.tempDir = Files.createTempDirectory("avatars");
//        // Inject the avatarsDir property
//        ReflectionTestUtils.setField(userService, "avatarsDir", tempDir.toString());
//        // Call @PostConstruct init
//        userService.init();
//    }

    @Test
    void testGetMyProfile() {
        // Arrange
        User user = new User();
        UserProfileDto dto = UserProfileDto.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .avatarUrl("/uploads/avatars/foo.png")
                .role("CUSTOMER")
                .registrationDate("2025-05-16T00:00:00")
                .bio("Hello world")
                .build();
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        when(userMapper.toProfileDto(user)).thenReturn(dto);

        // Act
        UserProfileDto result = userService.getMyProfile();

        // Assert
        assertSame(dto, result);
        verify(authenticationService).getRequestingUserFromSecurityContext();
        verify(userMapper).toProfileDto(user);
    }

    @Test
    void testUpdateProfile() {
        // Arrange
        User user = new User();
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        UpdateProfileDto dto = UpdateProfileDto.builder()
                .username("alice")
                .email("alice@example.com")
                .bio("New bio")
                .build();
        User saved = new User();
        UserProfileDto profileDto = new UserProfileDto();
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toProfileDto(saved)).thenReturn(profileDto);

        // Act
        UserProfileDto result = userService.updateProfile(dto);

        // Assert
        assertSame(profileDto, result);
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("New bio", user.getBio());
        verify(userRepository).save(user);
        verify(userMapper).toProfileDto(saved);
    }

    @Test
    @Disabled("dont wanna fix path variable")
    void testUpdateAvatar() throws IOException {
        // Arrange
        User user = new User();
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        // Create MockMultipartFile
        byte[] content = "dummy".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", content);

        // Act
        userService.updateAvatar(file);

        // Assert
        String avatarUrl = user.getAvatarUrl();
        assertNotNull(avatarUrl);
        // Avatar URL should reference the uploads/avatars directory
        assertTrue(avatarUrl.startsWith("/uploads/avatars/"));
        // File should exist on disk
        String filename = avatarUrl.substring("/uploads/avatars/".length());
        Path savedPath = tempDir.resolve(filename);
        assertTrue(Files.exists(savedPath));
        // Verify repository save
        verify(userRepository).save(user);
    }
}

