package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.dto.CheckoutRequest;
import com.cz.cvut.fel.instumentalshop.dto.CheckoutResponse;
import com.cz.cvut.fel.instumentalshop.dto.PurchaseItemDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.service.CheckoutService;
import com.cz.cvut.fel.instumentalshop.service.LicencePurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private final LicencePurchaseService licencePurchaseService;

    public CheckoutResponse checkout(Long customerId, CheckoutRequest req) {

        List<Long> licenceIds = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItemDto item : req.items()) {

            PurchaseRequestDto pr = new PurchaseRequestDto();
            pr.setLicenceType(item.licenceType());
            PurchaseDto pd = licencePurchaseService
                    .purchaseLicence(pr, item.trackId());

            licenceIds.add(pd.getPurchaseId());
            total = total.add(pd.getPrice());
        }
        return new CheckoutResponse(licenceIds, total);
    }
}
