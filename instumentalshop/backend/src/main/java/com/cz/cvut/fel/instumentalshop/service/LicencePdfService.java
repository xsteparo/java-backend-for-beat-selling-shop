package com.cz.cvut.fel.instumentalshop.service;

public interface LicencePdfService {

    byte[] generateLicencePdf(Long customerId, Long purchaseId);

}
