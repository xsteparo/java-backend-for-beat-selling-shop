package com.cz.cvut.fel.instumentalshop.dto.licence.out;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
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
