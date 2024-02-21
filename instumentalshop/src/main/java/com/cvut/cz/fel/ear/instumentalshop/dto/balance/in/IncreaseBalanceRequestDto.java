package com.cvut.cz.fel.ear.instumentalshop.dto.balance.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncreaseBalanceRequestDto {
    @Min(value = 1, message = "Minimum balance recharge 1$")
    @Max(value = 2000, message = "Maximum balance recharge is 2000$")
    private BigDecimal balanceRecharge;

}
