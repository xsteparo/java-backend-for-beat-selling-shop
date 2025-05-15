package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.CheckoutRequest;
import com.cz.cvut.fel.instumentalshop.dto.CheckoutResponse;

public interface CheckoutService {

    public CheckoutResponse checkout(Long customerId, CheckoutRequest req);

}
