package com.cz.cvut.fel.instumentalshop.dto.licence.out;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseDto {

    private Long purchaseId;

    private Long trackId;

    private LicenceType licenceType;

    private Long producerId;

    private String producer;

    private BigDecimal price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiredDate;

    private Integer validityPeriodDays;

    private List<Platform> availablePlatforms;

}
