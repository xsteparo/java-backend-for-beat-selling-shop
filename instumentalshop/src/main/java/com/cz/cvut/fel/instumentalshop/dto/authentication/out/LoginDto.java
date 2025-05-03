package com.cz.cvut.fel.instumentalshop.dto.authentication.out;

import lombok.Data;

@Data
public class LoginDto {

    private String token;

    private String refreshToken;
}
