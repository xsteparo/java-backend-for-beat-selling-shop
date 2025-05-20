package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.PurchaseUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;

import java.util.List;

/**
 * Service vrstva pro administrátorské operace.
 */
public interface AdminService {

    void deletePurchase(Long purchaseId);

    /** Vrátí všechny záznamy o zakoupených licencích. */
    List<PurchaseDto> getAllPurchases();

    /** Upravení existujícího záznamu licence. */
    PurchaseDto updatePurchase(Long purchaseId, PurchaseUpdateRequestDto dto);

    /** Smazání tracku z katalogu (administrátorské právo). */
    void deleteTrack(Long trackId);

    /** Vrátí všechny registrované uživatele. */
    List<UserDto> getAllUsers();

    /** Smazání uživatele podle ID. */
    void deleteUser(Long userId);
}
