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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    private final TrackRepository trackRepo;
    private final UserRepository userRepo;
    private final PurchasedLicenceRepository licRepo;
    private final UserMapper userMapper;

    @Override
    public void deletePurchase(Long id) {
        if (!licRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found");
        }
        licRepo.deleteById(id);
    }

    @Override
    public List<PurchaseDto> getAllPurchases() {
        return licRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseDto updatePurchase(Long purchaseId, PurchaseUpdateRequestDto dto) {
        PurchasedLicence lic = licRepo.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("PurchasedLicence not found: " + purchaseId));
        LicenceTemplate tpl = lic.getLicenceTemplate();
        if (dto.getLicenceType() != null) {
            tpl.setLicenceType(dto.getLicenceType());
        }
        if (dto.getPrice() != null) {
            tpl.setPrice(dto.getPrice());
        }
        if (dto.getExpiredDate() != null) {
            lic.setExpiredDate(dto.getExpiredDate());
        }
        licRepo.save(lic);
        return toDto(lic);
    }

    @Override
    public void deleteTrack(Long trackId) {
        if (!trackRepo.existsById(trackId)) {
            throw new EntityNotFoundException("Track not found: " + trackId);
        }
        trackRepo.deleteById(trackId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        userRepo.delete(user);
    }

    private PurchaseDto toDto(PurchasedLicence lic) {
        return PurchaseDto.builder()
                .purchaseId(lic.getId())
                .trackId(lic.getTrack().getId())
                .licenceType(lic.getLicenceTemplate().getLicenceType())
                .price(lic.getLicenceTemplate().getPrice())
                .purchaseDate(lic.getPurchaseDate())
                .expiredDate(lic.getExpiredDate())
                .validityPeriodDays(lic.getLicenceTemplate().getValidityPeriodDays())
                .availablePlatforms(List.of())
                .build();
    }
}