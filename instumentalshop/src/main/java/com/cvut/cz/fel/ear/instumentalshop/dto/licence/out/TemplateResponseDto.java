package com.cvut.cz.fel.ear.instumentalshop.dto.licence.out;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Platform;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
@Data
public class TemplateResponseDto {

    private Long licenceTemplateId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long trackId;

    private LicenceType licenceType;

    private Integer validityPeriodDays;

    private Integer price;

    private List<Platform> availablePlatforms;

}
