package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UserProfileDto;
import com.cz.cvut.fel.instumentalshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserService userService;

    /**
     * FR11: Zobrazení profilu přihlášeného uživatele
     */
    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile() {
        UserProfileDto profile = userService.getMyProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * FR12: Úprava profilu (jméno, email, bio apod.)
     */
    @PutMapping
    public ResponseEntity<UserProfileDto> updateProfile(
            @RequestBody UpdateProfileDto dto
    ) {
        UserProfileDto updated = userService.updateProfile(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Změna avataru
     */
    @PutMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAvatar(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        userService.updateAvatar(file);
        return ResponseEntity.noContent().build();
    }
}
