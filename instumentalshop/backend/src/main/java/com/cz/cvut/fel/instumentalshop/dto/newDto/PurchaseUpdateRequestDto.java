package com.cz.cvut.fel.instumentalshop.dto.newDto;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pro úpravu existujícího záznamu nákupu.
 * Používá se administrátorem k aktualizaci ceny nebo data vypršení licence.
 */
@Data
@AllArgsConstructor
public class PurchaseUpdateRequestDto {

    /** Nový typ licence (pokud má být změněn). */
    private LicenceType licenceType;

    /** Nová cena (pokud má být změněna). */
    private BigDecimal price;


    /**
     * Nové datum vypršení licence (volitelné).
     */
    private LocalDateTime expiredDate;

}
