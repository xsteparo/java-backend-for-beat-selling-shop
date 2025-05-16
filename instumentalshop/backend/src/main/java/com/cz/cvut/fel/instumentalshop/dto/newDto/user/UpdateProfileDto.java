package com.cz.cvut.fel.instumentalshop.dto.newDto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pro aktualizaci profilu přihlášeného uživatele.
 *
 * Umožňuje změnu uživatelského jména, e-mailu a bio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileDto {
    /** Nové uživatelské jméno, 3–50 znaků */
    @Size(min = 3, max = 50)
    private String username;

    /** Nový e-mail, validní formát */
    @Email
    private String email;

    /** Aktualizované bio uživatele, max. 250 znaků */
    @Size(max = 250)
    private String bio;
}
