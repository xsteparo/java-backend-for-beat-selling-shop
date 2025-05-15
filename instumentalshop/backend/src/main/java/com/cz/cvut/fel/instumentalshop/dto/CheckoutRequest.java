package com.cz.cvut.fel.instumentalshop.dto;

import java.util.List;

public record CheckoutRequest(List<PurchaseItemDto> items) {}

