package com.cz.cvut.fel.instumentalshop.dto.newDto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pro zobrazení profilu uživatele.
 *
 * Obsahuje základní informace o uživateli: ID, uživatelské jméno, e-mail,
 * URL avataru, roli a datum registrace.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    /** Unikátní identifikátor uživatele */
    private Long id;

    /** Uživatelské jméno (username) */
    private String username;

    /** E-mailová adresa uživatele */
    private String email;

    /** URL k avataru uživatele */
    private String avatarUrl;

    /** Role uživatele (např. CUSTOMER, PRODUCER, ADMIN) */
    private String role;

    /** Datum registrace ve formátu ISO */
    private String registrationDate;

    /** Krátký popis nebo bio uživatele */
    private String bio;
}
