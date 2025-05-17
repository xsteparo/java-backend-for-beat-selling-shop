package com.cz.cvut.fel.instumentalshop.service.newTests;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
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

    @Mock
    private PurchasedLicenceRepository licRepo;

    @Mock
    private TrackRepository trackRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminServiceImpl service;

    private PurchasedLicence licence;
    private LicenceTemplate template;
    private User user;

    @BeforeEach
    void setUp() {
        // společné entity pro testy
        template = new LicenceTemplate();
        template.setId(42L);
        template.setLicenceType(LicenceType.NON_EXCLUSIVE);
        template.setPrice(new BigDecimal("9.99"));
        template.setValidityPeriodDays(30);

        licence = new PurchasedLicence();
        licence.setId(100L);
        licence.setLicenceTemplate(template);
        licence.setPurchaseDate(LocalDateTime.of(2025, 5, 1, 12, 0));
        licence.setExpiredDate(LocalDateTime.of(2025, 6, 1, 12, 0));
        // producer a customer jsou nastaveny ve vnitřním builderu toDto

        user = new User();
        user.setId(7L);
        user.setUsername("testuser");
    }

    @Test
    void testGetAllPurchases() {
        // Arrange: repository vrací jednu licenci
        when(licRepo.findAll()).thenReturn(List.of(licence));

        // Act
        List<PurchaseDto> dtos = service.getAllPurchases();

        // Assert
        assertEquals(1, dtos.size());
        PurchaseDto dto = dtos.get(0);
        assertEquals(100L, dto.getPurchaseId());
        assertEquals(LicenceType.NON_EXCLUSIVE, dto.getLicenceType());
        assertEquals(new BigDecimal("9.99"), dto.getPrice());
        assertEquals(42L, dto.getTrackId());
        assertEquals(30, dto.getValidityPeriodDays());
        // další ověření podle potřeb...
    }

    @Test
    void testUpdatePurchase_Success() {
        // Arrange: existující licence
        when(licRepo.findById(100L)).thenReturn(Optional.of(licence));
        when(licRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PurchaseUpdateRequestDto update = new PurchaseUpdateRequestDto(
                LicenceType.EXCLUSIVE,
                new BigDecimal("19.99"),
                LocalDateTime.of(2025, 7, 1, 0, 0)
        );

        // Act
        PurchaseDto result = service.updatePurchase(100L, update);

        // Assert
        assertEquals(new BigDecimal("19.99"), template.getPrice(), "Cena šablony musí být aktualizována");
        assertEquals(LocalDateTime.of(2025, 7, 1, 0, 0), licence.getExpiredDate(), "Datum vypršení musí být aktualizováno");
        assertEquals(100L, result.getPurchaseId());
        assertEquals(new BigDecimal("19.99"), result.getPrice());
    }

    @Test
    void testUpdatePurchase_NotFound() {
        // Arrange: žádná licence
        when(licRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(EntityNotFoundException.class,
                () -> service.updatePurchase(123L, new PurchaseUpdateRequestDto(LicenceType.EXCLUSIVE, BigDecimal.ONE, null)));
    }

    @Test
    void testDeleteTrack() {
        // Act
        service.deleteTrack(55L);

        // Assert
        verify(trackRepo).deleteById(55L);
    }

    @Test
    void testGetAllUsers() {
        // Arrange: dva uživatelé a jejich DTO
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);
        UserDto dto1 = new UserDto();
        UserDto dto2 = new UserDto();

        when(userRepo.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toDto(u1)).thenReturn(dto1);
        when(userMapper.toDto(u2)).thenReturn(dto2);

        // Act
        List<UserDto> users = service.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        assertSame(dto1, users.get(0));
        assertSame(dto2, users.get(1));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepo.findById(7L)).thenReturn(Optional.of(user));

        // Act
        service.deleteUser(7L);

        // Assert
        verify(userRepo).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(EntityNotFoundException.class, () -> service.deleteUser(999L));
    }
}

