package com.cz.cvut.fel.instumentalshop.service.newTests;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.UserMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.PurchaseUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.AdminServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy AdminServiceImpl: ověření chování administrační logiky.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock TrackRepository trackRepo;
    @Mock UserRepository userRepo;
    @Mock PurchasedLicenceRepository licRepo;
    @Mock UserMapper userMapper;
    @InjectMocks AdminServiceImpl service;

    private PurchasedLicence licence;
    private LicenceTemplate template;
    private Track track;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        track = new Track();
        track.setId(42L);

        template = new LicenceTemplate();
        template.setLicenceType(LicenceType.NON_EXCLUSIVE);
        template.setPrice(new BigDecimal("9.99"));
        template.setValidityPeriodDays(30);

        licence = new PurchasedLicence();
        licence.setId(100L);
        licence.setTrack(track);
        licence.setLicenceTemplate(template);
        licence.setPurchaseDate(LocalDateTime.of(2025,6,1,12,0));
        licence.setExpiredDate(LocalDateTime.of(2025,6,30,0,0));

        user = new User();
        user.setId(7L);
        user.setUsername("john");
        user.setBalance(new BigDecimal("50.00"));
        user.setRole(Role.CUSTOMER);
        user.setRegistrationDate(LocalDateTime.of(2025,1,1,0,0));
        user.setBio("Hello");

        userDto = new UserDto();
        userDto.setUserId(7L);
        userDto.setUsername("john");
        userDto.setBalance(new BigDecimal("50.00"));
        userDto.setRole("USER");
        userDto.setRegistrationDate(LocalDateTime.parse("2025-01-01T00:00:00"));
        userDto.setBio("Hello");
    }

    @Test
    void testGetAllPurchases() {
        when(licRepo.findAll()).thenReturn(List.of(licence));

        List<PurchaseDto> dtos = service.getAllPurchases();

        assertEquals(1, dtos.size());
        PurchaseDto dto = dtos.get(0);
        assertEquals(100L, dto.getPurchaseId());
        assertEquals(LicenceType.NON_EXCLUSIVE, dto.getLicenceType());
        assertEquals(new BigDecimal("9.99"), dto.getPrice());
        assertEquals(42L, dto.getTrackId());
        assertEquals(30, dto.getValidityPeriodDays());
    }

    @Test
    void testUpdatePurchase_Success() {
        when(licRepo.findById(100L)).thenReturn(Optional.of(licence));
        when(licRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseUpdateRequestDto update = new PurchaseUpdateRequestDto(
                LicenceType.EXCLUSIVE, new BigDecimal("19.99"),
                LocalDateTime.of(2025,7,1,0,0)
        );

        PurchaseDto result = service.updatePurchase(100L, update);

        assertEquals(new BigDecimal("19.99"), template.getPrice());
        assertEquals(LocalDateTime.of(2025,7,1,0,0), licence.getExpiredDate());
        assertEquals(100L, result.getPurchaseId());
        assertEquals(new BigDecimal("19.99"), result.getPrice());
    }

    @Test
    void testDeleteTrack_Success() {
        when(trackRepo.existsById(55L)).thenReturn(true);

        service.deleteTrack(55L);

        verify(trackRepo).deleteById(55L);
    }

    @Test
    void testDeleteTrack_NotFound() {
        when(trackRepo.existsById(55L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deleteTrack(55L));
    }

    @Test
    void testGetAllUsers() {
        when(userRepo.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> users = service.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(userDto, users.get(0));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepo.findById(7L)).thenReturn(Optional.of(user));

        service.deleteUser(7L);

        verify(userRepo).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepo.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteUser(7L));
    }
}

