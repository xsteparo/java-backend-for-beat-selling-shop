package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.PurchaseDto;

import java.util.List;

public interface LicencePurchaseService {

    PurchaseDto purchaseLicence(PurchaseRequestDto requestDto, Long trackId);

    PurchaseDto getPurchasedLicenceById(Long purchaseId);

    List<PurchaseDto> getAllPurchasedLicences();

    List<ProducerIncomeDto> getProducerIncomesByTracks();

}
