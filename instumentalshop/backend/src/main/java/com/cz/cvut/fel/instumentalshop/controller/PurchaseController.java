package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.LicencePurchaseService;
import com.cz.cvut.fel.instumentalshop.service.LicensePdfService;
import com.cz.cvut.fel.instumentalshop.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


/**
 * {@code PurchaseController} poskytuje endpointy pro:
 * - nákup licence skladby
 * - výpis nákupů uživatele
 * - stažení PDF licence
 * - stažení stažené audio stopy
 */
@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@CrossOrigin(origins = "http://localhost:5173")
public class PurchaseController {

    private final LicencePurchaseService licenceService;
    private final AuthenticationService authenticationService;
    private final PurchasedLicenceRepository licRepo;
    private final TrackService trackService;
    private final LicensePdfService pdfService;

    /**
     * FR08: Zakoupí licenci a vrátí informace o koupi.
     */
    @PostMapping("/{trackId}")
    public ResponseEntity<PurchaseDto> purchase(
            @PathVariable Long trackId,
            @RequestBody PurchaseRequestDto dto
    ) {
        PurchaseDto result = licenceService.purchaseLicence(dto, trackId);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * FR08: Zobrazí seznam všech zakoupených licencí aktuálního uživatele.
     */
    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getMyPurchases() {
        List<PurchaseDto> list = licenceService.getAllPurchasedLicences();
        return ResponseEntity.ok(list);
    }

    /**
     * FR16: Stáhne PDF licence pro dané ID nákupu.
     */
    @GetMapping("/{purchaseId}/license")
    public ResponseEntity<byte[]> downloadLicense(
            @PathVariable Long purchaseId
    ) {
        Customer customer = (Customer) authenticationService.getRequestingUserFromSecurityContext();
        byte[] pdf = pdfService.generateLicencePdf(customer.getId(), purchaseId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=license.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /**
     * FR17: Stáhne audio soubor zakoupené skladby.
     */
    @GetMapping("/{purchaseId}/download")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Resource> downloadTrack(@PathVariable Long purchaseId) throws IOException {
        PurchasedLicence lic = licRepo.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found: " + purchaseId));

        Long trackId = lic.getTrack().getId();
        LicenceType licenceType = lic.getLicenceTemplate().getLicenceType();

        // Загружаем ресурс в зависимости от типа лицензии
        Resource resource = trackService.loadAsResource(trackId, licenceType);

        // Определяем расширение и media type
        String extension;
        MediaType mediaType;
        switch (licenceType) {
            case NON_EXCLUSIVE:
                extension = ".mp3";
                mediaType = MediaType.parseMediaType("audio/mpeg");
                break;
            case PREMIUM:
                extension = ".wav";
                mediaType = MediaType.parseMediaType("audio/wav");
                break;
            case EXCLUSIVE:
                extension = ".zip";
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            default:
                throw new IllegalStateException("Unknown licence type: " + licenceType);
        }

        // Собираем имя файла: можно брать track.getName() + extension, либо resource.getFilename()
        String trackName = lic.getTrack().getName().replaceAll("\\s+", "_");
        String filename = trackName + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(resource);
    }
}
