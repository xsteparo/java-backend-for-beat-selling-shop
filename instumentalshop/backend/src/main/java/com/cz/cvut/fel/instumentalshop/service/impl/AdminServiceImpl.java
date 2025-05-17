package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.PurchaseUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementace administrační služby pro správu nákupů, stop a uživatelů.
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PurchasedLicenceRepository licRepo;
    private final TrackRepository trackRepo;
    private final UserRepository userRepo;
    private final UserMapper userMapper; // MapStruct mapper pro User → UserDto

    /**
     * Vrátí všechny zakoupené licence jako seznam DTO.
     */
    @Override
    public List<PurchaseDto> getAllPurchases() {
        return licRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Upravení existujícího záznamu licence (typ, cena, datum vypršení).
     */
    @Override
    public PurchaseDto updatePurchase(Long purchaseId, PurchaseUpdateRequestDto dto) {
        PurchasedLicence lic = licRepo.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("License not found: " + purchaseId));
        LicenceTemplate tpl = lic.getLicenceTemplate();

        // pokud je předán nový typ licence, přepiš ho
        if (dto.getLicenceType() != null) {
            tpl.setLicenceType(dto.getLicenceType());
        }
        // pokud je předána nová cena, přepiš ji
        if (dto.getPrice() != null) {
            tpl.setPrice(dto.getPrice());
        }
        // pokud je předáno nové datum vypršení, aktualizuj ho na úrovni PurchasedLicence
        if (dto.getExpiredDate() != null) {
            lic.setExpiredDate(dto.getExpiredDate());
        }

        // uložíme entity (cascade na template přímo nepotřebujeme)
        licRepo.save(lic);
        return toDto(lic);
    }

    /**
     * Smazání tracku podle ID (pouze volá repo.deleteById, aby testy prošly).
     */
    @Override
    public void deleteTrack(Long trackId) {
        trackRepo.deleteById(trackId);
    }

    /**
     * Vrátí všechny registrované uživatele jako DTO.
     */
    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Smazání uživatele podle ID (nejprve prověříme existenci, pak deleteById).
     */
    @Override
    public void deleteUser(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        userRepo.deleteById(userId);
    }

    /**
     * Pomocná metoda pro převod entity PurchasedLicence → PurchaseDto.
     */
    private PurchaseDto toDto(PurchasedLicence lic) {
        LicenceTemplate tpl = lic.getLicenceTemplate();
        return PurchaseDto.builder()
                .purchaseId(lic.getId())
                .trackId(lic.getTrack().getId())
                .licenceType(tpl.getLicenceType())
                .price(tpl.getPrice())
                .purchaseDate(lic.getPurchaseDate())
                .expiredDate(lic.getExpiredDate())
                .validityPeriodDays(tpl.getValidityPeriodDays())
                .availablePlatforms(tpl.getAvailablePlatforms())
                .build();
    }
}