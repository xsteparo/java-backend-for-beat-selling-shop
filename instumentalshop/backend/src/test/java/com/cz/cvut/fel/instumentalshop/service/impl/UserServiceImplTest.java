package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UserProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.UserService;
import com.cz.cvut.fel.instumentalshop.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper userMapper;

    @TempDir
    Path tempDir;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(service, "avatarsDir", tempDir.toString());
        service.init();
    }

    @Test
    void depositToBalance_positiveAmount_updatesBalance() {
        String username = "john";
        User user = new User();
        user.setUsername(username);
        user.setBalance(BigDecimal.valueOf(50));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any())).thenReturn(new UserDto());

        UserDto dto = service.depositToBalance(username, BigDecimal.valueOf(25));

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(75));
        assertThat(dto).isNotNull();
    }

    @Test
    void depositToBalance_zeroOrNegative_throws() {
        assertThatThrownBy(() -> service.depositToBalance("john", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be positive");
    }

    @Test
    void depositToBalance_userNotFound_throws() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.depositToBalance("someone", BigDecimal.valueOf(10)))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getMyProfile_authenticatedUser_returnsProfileDto() {
        User user = new User();
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        UserProfileDto profileDto = new UserProfileDto();
        when(userMapper.toProfileDto(user)).thenReturn(profileDto);

        UserProfileDto result = service.getMyProfile();

        assertThat(result).isSameAs(profileDto);
    }

    @Test
    void getMyProfile_noAuthenticatedUser_throws() {
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(null);
        assertThatThrownBy(() -> service.getMyProfile())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Authenticated user not found in context");
    }

    @Test
    void updateProfile_changesFieldsAndReturnsDto() {
        User user = new User();
        user.setUsername("old");
        user.setEmail("old@example.com");
        user.setBio("old bio");
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(user);
        UpdateProfileDto dto = new UpdateProfileDto("newname", "new@example.com", "new bio");
        User updatedUser = new User();
        when(userRepository.save(any())).thenReturn(updatedUser);
        UserProfileDto profileDto = new UserProfileDto();
        when(userMapper.toProfileDto(updatedUser)).thenReturn(profileDto);

        UserProfileDto result = service.updateProfile(dto);

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getUsername()).isEqualTo("newname");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getBio()).isEqualTo("new bio");
        assertThat(result).isSameAs(profileDto);
    }

    @Test
    void updateAvatar_noAuthenticatedUser_throws() {
        when(authenticationService.getRequestingUserFromSecurityContext()).thenReturn(null);
        MultipartFile file = mock(MultipartFile.class);
        assertThatThrownBy(() -> service.updateAvatar(file))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Authenticated user not found in context");
    }
}

