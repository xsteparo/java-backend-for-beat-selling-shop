package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;

import java.util.List;

public interface LicencePurchaseService {

    PurchaseDto purchaseLicence(PurchaseRequestDto requestDto, Long trackId);

    PurchaseDto getPurchasedLicenceById(Long purchaseId);

    List<PurchaseDto> getAllPurchasedLicences();

}
