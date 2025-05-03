package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.dto.authentication.out.LoginDto;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.LoginRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.authentication.in.RefreshTokenRequest;

public interface AuthenticationService {

    LoginDto login(LoginRequestDto loginRequestDto);

    LoginDto refreshToken(RefreshTokenRequest refreshTokenRequest);

    Customer getRequestingCustomerFromSecurityContext();

    Producer getRequestingProducerFromSecurityContext();

    User getRequestingUserFromSecurityContext();

}
