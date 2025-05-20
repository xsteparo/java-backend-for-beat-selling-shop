package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UserProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * {@code UserService} poskytuje operace pro správu profilu
 * aktuálně přihlášeného uživatele.
 *
 * Metody zahrnují získání a aktualizaci profilu a změnu avataru.
 */
public interface UserService {

    void init() throws IOException;

    UserDto depositToBalance(String username, BigDecimal amount);

    /**
     * Získá profil přihlášeného uživatele.
     *
     * @return {@link UserProfileDto} s informacemi o uživateli
     */
    UserProfileDto getMyProfile();

    /**
     * Aktualizuje profil přihlášeného uživatele.
     *
     * @param dto DTO s novými hodnotami (username, email, bio)
     * @return {@link UserProfileDto} s aktualizovanými hodnotami
     */
    UserProfileDto updateProfile(UpdateProfileDto dto);

    /**
     * Aktualizuje avatar přihlášeného uživatele.
     *
     * @param file nový avatar jako multipart soubor
     * @throws IOException pokud se nepodaří uložit soubor
     */
    void updateAvatar(MultipartFile file) throws IOException;
}
