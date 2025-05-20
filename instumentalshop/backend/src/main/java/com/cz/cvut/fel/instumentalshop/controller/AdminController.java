package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.PurchaseUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateUserRoleRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API pro administrátorské operace jako:
 * - přehled všech nákupů
 * - úprava záznamů nákupů
 * - správa uživatelů
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")  // dostupné jen pro ADMIN role
public class AdminController {

    private final AdminService adminService;

    /**
     * FR14 - Zobrazí všechny zakoupené licence (historie nákupů).
     */
    @GetMapping("/purchases")
    public ResponseEntity<List<PurchaseDto>> getAllPurchases() {
        List<PurchaseDto> list = adminService.getAllPurchases();
        return ResponseEntity.ok(list);
    }

    /**
     * FR15 - Upraví existující záznam nákupu.
     */
    @PutMapping("/purchases/{purchaseId}")
    public ResponseEntity<PurchaseDto> updatePurchase(
            @PathVariable Long purchaseId,
            @RequestBody PurchaseUpdateRequestDto dto
    ) {
        PurchaseDto updated = adminService.updatePurchase(purchaseId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Smaže track z katalogu (např. v případě nevhodného obsahu).
     */
    @DeleteMapping("/tracks/{trackId}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long trackId) {
        adminService.deleteTrack(trackId);
        return ResponseEntity.noContent().build();
    }

    /**
     * FR13 - Zobrazí seznam všech uživatelů.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * (volitelně) Smaže uživatele podle ID.
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purchases/{purchaseId}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long purchaseId) {
        adminService.deletePurchase(purchaseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UpdateUserRoleRequestDto dto
    ) {
        UserDto updated = adminService.updateUserRole(userId, dto.getRole());
        return ResponseEntity.ok(updated);
    }
}
