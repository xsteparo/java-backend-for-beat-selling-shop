package com.cz.cvut.fel.instumentalshop.controller;


import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.LicensePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/licences")
public class LicenceDownloadController {

    private final LicensePdfService licencePdfService;
    private final AuthenticationService authenticationService;


    @GetMapping("/{purchaseId}/download")
    public ResponseEntity<byte[]> downloadLicence(@PathVariable Long purchaseId) {
        Customer customer = (Customer) authenticationService.getRequestingUserFromSecurityContext();

        byte[] pdfBytes = licencePdfService.generateLicencePdf(customer.getId(), purchaseId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=licence_" + purchaseId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
