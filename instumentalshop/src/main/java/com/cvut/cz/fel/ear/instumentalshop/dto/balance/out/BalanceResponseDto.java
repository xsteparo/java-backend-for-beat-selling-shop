package com.cvut.cz.fel.ear.instumentalshop.dto.balance.out;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceResponseDto {

    private Long userId;

    private BigDecimal balance;

}
