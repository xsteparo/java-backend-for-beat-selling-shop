package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.User;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.out.LoginDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.authentication.in.RefreshTokenRequest;

public interface AuthenticationService {

    LoginDto login(LoginRequestDto loginRequestDto);

    LoginDto refreshToken(RefreshTokenRequest refreshTokenRequest);

    Customer getRequestingCustomerFromSecurityContext();

    Producer getRequestingProducerFromSecurityContext();

    User getRequestingUserFromSecurityContext();

}
