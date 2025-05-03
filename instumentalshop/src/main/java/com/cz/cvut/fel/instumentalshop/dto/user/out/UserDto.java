package com.cz.cvut.fel.instumentalshop.dto.user.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long userId;

    private String role;

    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime registrationDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal balance;

}
