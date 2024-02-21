package com.cvut.cz.fel.ear.instumentalshop.dto.licence.out;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Platform;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class PurchaseDto {

    private Long purchaseId;

    private Long trackId;

    private LicenceType licenceType;

    private Map<Long, String> producerOwners;

    private BigDecimal price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiredDate;

    private Integer validityPeriodDays;

    private List<Platform> availablePlatforms;

}
