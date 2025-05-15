package com.cz.cvut.fel.instumentalshop.dto;

import java.util.List;

public record CheckoutResponse(List<Long> licenceIds, java.math.BigDecimal total) {}

