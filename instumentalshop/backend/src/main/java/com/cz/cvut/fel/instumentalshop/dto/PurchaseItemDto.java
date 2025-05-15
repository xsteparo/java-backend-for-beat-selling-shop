package com.cz.cvut.fel.instumentalshop.dto;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;

public record PurchaseItemDto(Long trackId, LicenceType licenceType) {}
