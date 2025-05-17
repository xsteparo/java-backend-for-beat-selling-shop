package com.cz.cvut.fel.instumentalshop.dto.newDto;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenceTemplateDto {
    private Long id;
    private LicenceType licenceType;
    private BigDecimal price;
    private Integer validityPeriodDays;
}
