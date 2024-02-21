package com.cvut.cz.fel.ear.instumentalshop.dto.authentication.out;

import lombok.Data;

@Data
public class LoginDto {

    private String token;

    private String refreshToken;
}
