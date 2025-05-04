package com.cz.cvut.fel.instumentalshop.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;

public interface JWTService {

    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);

}
