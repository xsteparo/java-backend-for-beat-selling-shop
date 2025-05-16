package com.cz.cvut.fel.instumentalshop.service;

public interface LicensePdfService {

    byte[] generateLicencePdf(Long customerId, Long purchaseId);

}
