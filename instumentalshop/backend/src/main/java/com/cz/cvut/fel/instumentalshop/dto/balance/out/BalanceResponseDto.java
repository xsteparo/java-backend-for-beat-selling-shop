package com.cz.cvut.fel.instumentalshop.dto.balance.out;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceResponseDto {

    private Long userId;

    private BigDecimal balance;

}
