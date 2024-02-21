package com.cvut.cz.fel.ear.instumentalshop.controller;

import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.out.LoginDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.RefreshTokenRequest;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authenticationService.login(loginRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginDto> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

}
