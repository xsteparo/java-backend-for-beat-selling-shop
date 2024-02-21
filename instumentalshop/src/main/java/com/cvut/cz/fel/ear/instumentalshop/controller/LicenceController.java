package com.cvut.cz.fel.ear.instumentalshop.controller;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceReport;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.ReportStatus;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.LicenceReportRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.LicenceReportDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.TemplateResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.PurchaseDto;
import com.cvut.cz.fel.ear.instumentalshop.service.LicencePurchaseService;
import com.cvut.cz.fel.ear.instumentalshop.service.LicenceReportService;
import com.cvut.cz.fel.ear.instumentalshop.service.LicenceTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LicenceController {

    private final LicencePurchaseService licencePurchaseService;

    private final LicenceTemplateService licenceTemplateService;

    private final LicenceReportService licenceReportService;

    @PostMapping("tracks/{trackId}/licence-templates")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<TemplateResponseDto> createLicenceTemplate(
            @PathVariable Long trackId,
            @Valid @RequestBody TemplateCreationRequestDto requestDto) {

        TemplateResponseDto responseDto = licenceTemplateService.createTemplate(trackId, requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/tracks/{trackId}/licence-templates/{licenceType}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TemplateResponseDto> getLicenceTemplateByType(
            @PathVariable Long trackId,
            @PathVariable LicenceType licenceType) {

        TemplateResponseDto responseDto = licenceTemplateService.getTemplateByTypeAndTrackId(trackId, licenceType);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/tracks/{trackId}/licence-templates")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TemplateResponseDto>> getAllLicenceTemplatesByTrack(@PathVariable Long trackId) {
        List<TemplateResponseDto> responseDto = licenceTemplateService.getAllTemplatesByTrack(trackId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/tracks/{trackId}/licence-templates/{licenceType}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<TemplateResponseDto> updateLicenceTemplate(
            @PathVariable Long trackId,
            @PathVariable LicenceType licenceType,
            @Valid @RequestBody TemplateUpdateRequestDto requestDto) {

        TemplateResponseDto responseDto = licenceTemplateService.updateTemplate(trackId, licenceType, requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/tracks/{trackId}/licence-templates/{licenceType}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<Void> deleteLicenceTemplate(
            @PathVariable Long trackId,
            @PathVariable LicenceType licenceType) {

        licenceTemplateService.deleteTemplate(trackId, licenceType);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("tracks/{trackId}/purchase")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<PurchaseDto> purchaseLicence(@Valid @RequestBody PurchaseRequestDto requestDto,
                                                       @PathVariable Long trackId) {

        PurchaseDto responseDto = licencePurchaseService.purchaseLicence(requestDto, trackId);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/licence-purchases/{purchaseId}")
    @PreAuthorize("hasAuthority('PRODUCER') or hasAuthority('CUSTOMER')")
    public ResponseEntity<PurchaseDto> getPurchasedLicenceById(@PathVariable Long purchaseId) {
        PurchaseDto responseDto = licencePurchaseService.getPurchasedLicenceById(purchaseId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/licence-purchases")
    @PreAuthorize("hasAuthority('PRODUCER') or hasAuthority('CUSTOMER')")
    public List<PurchaseDto> getAllPurchasedLicences() {
        return licencePurchaseService.getAllPurchasedLicences();
    }

    @PostMapping("/licence-purchases/{purchasedLicenceId}/create-report")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<LicenceReportDto> createReport(
            @PathVariable Long purchasedLicenceId,
            @RequestBody LicenceReportRequestDto requestDto) {

        LicenceReportDto report = licenceReportService.createReport(purchasedLicenceId, requestDto);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<LicenceReportDto> getReport(@PathVariable Long reportId) {
        LicenceReportDto report = licenceReportService.getReportById(reportId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PutMapping("/reports/{reportId}/{reportStatus}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<LicenceReportDto> updateReport(
            @PathVariable Long reportId,
            @PathVariable ReportStatus reportStatus) {

        LicenceReportDto report = licenceReportService.updateReport(reportId, reportStatus);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @DeleteMapping("/reports/{reportId}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long reportId) {

        licenceReportService.deleteReportById(reportId);
        return ResponseEntity.ok().build();
    }

}
