package com.cz.cvut.fel.instumentalshop.dto.licence.in;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TemplateUpdateRequestDto {

    private LicenceType licenceType;

    private Integer validityPeriodDays;

    private BigDecimal price;

    private List<Platform> platforms;

}
