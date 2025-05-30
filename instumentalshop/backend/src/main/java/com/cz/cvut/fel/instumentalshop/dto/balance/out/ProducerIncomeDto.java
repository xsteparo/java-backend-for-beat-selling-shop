package com.cz.cvut.fel.instumentalshop.dto.balance.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProducerIncomeDto {
    private Long trackId;
    private String trackName;
    private BigDecimal salaryFromTrack;
}
