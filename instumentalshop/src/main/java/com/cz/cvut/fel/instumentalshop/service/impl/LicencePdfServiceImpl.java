package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.service.LicencePdfService;
import com.cz.cvut.fel.instumentalshop.util.pdf.PdfGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LicencePdfServiceImpl implements LicencePdfService {

    private final PurchasedLicenceRepository purchasedLicenceRepository;

    @Override
    public byte[] generateLicencePdf(Long customerId, Long purchaseId) {
        PurchasedLicence licence = purchasedLicenceRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Licence not found"));

        if (!licence.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("Access denied to licence");
        }

        String buyerName = licence.getCustomer().getUsername();
        String trackName = licence.getTrack().getName();
        LicenceType licenceType = licence.getLicenceTemplate().getLicenceType();
        LocalDateTime purchaseDate = licence.getPurchaseDate();
        LocalDateTime expiredDate = licence.getExpiredDate();

        byte[] pdfBytes = PdfGenerator.generate(
                buyerName, trackName, licenceType, purchaseDate, expiredDate
        );

        return pdfBytes;
    }
}
