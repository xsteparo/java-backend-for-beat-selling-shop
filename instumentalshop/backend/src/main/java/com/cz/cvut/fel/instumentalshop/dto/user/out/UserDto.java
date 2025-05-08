package com.cz.cvut.fel.instumentalshop.dto.user.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class UserDto {

    private Long userId;

    private String role;

    private String username;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime registrationDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal balance;

}
