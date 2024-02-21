package com.cvut.cz.fel.ear.instumentalshop.dto.licence.in;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Platform;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class TemplateCreationRequestDto {

    @NotNull
    private LicenceType licenceType;

    private Integer validityPeriodDays;

    @NotNull
    private BigDecimal price;

    private List<Platform> platforms;

}
