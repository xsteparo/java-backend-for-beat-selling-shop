package com.cvut.cz.fel.ear.instumentalshop.service.impl.licence;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceReport;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.PurchasedLicence;
import com.cvut.cz.fel.ear.instumentalshop.domain.User;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.ReportStatus;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.LicenceReportRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.LicenceReportDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.LicenceMapper;
import com.cvut.cz.fel.ear.instumentalshop.repository.LicenceReportRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.PurchasedLicenceRepository;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import com.cvut.cz.fel.ear.instumentalshop.service.LicenceReportService;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.LicenceValidator;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.impl.LicenceValidatorImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LicenceReportServiceImpl implements LicenceReportService {

    private final AuthenticationService authenticationService;

    private final PurchasedLicenceRepository purchasedLicenceRepository;

    private final LicenceReportRepository licenceReportRepository;

    private final LicenceMapper licenceMapper;

    private final LicenceValidator licenceValidator;


    @Override
    @Transactional
    public LicenceReportDto createReport(Long purchasedLicenceId, LicenceReportRequestDto requestDto) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        PurchasedLicence purchasedLicence = purchasedLicenceRepository.findById(purchasedLicenceId)
                .orElseThrow(() -> new IllegalArgumentException("Purchased licence not found"));

        licenceValidator.validateReportCreateRequest(purchasedLicence, producer);

        LicenceReport report = buildReport(purchasedLicence, requestDto);

        report = licenceReportRepository.save(report);

        return licenceMapper.toResponseDto(report);
    }

    @Override
    @Transactional
    public LicenceReportDto getReportById(Long reportId) {
        User user = authenticationService.getRequestingUserFromSecurityContext();

        licenceValidator.validateReportGetRequest(user, reportId);

        LicenceReport report = licenceReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Licence report not found with id: " + reportId));

        return licenceMapper.toResponseDto(report);
    }

    @Override
    @Transactional
    public LicenceReportDto updateReport(Long reportId, ReportStatus reportStatus) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        LicenceReport licenceReport = licenceReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Licence report not found with id: " + reportId));

        licenceValidator.validateReportUpdateStatus(producer, licenceReport);

        licenceReport.setReportStatus(reportStatus);
        licenceReport = licenceReportRepository.save(licenceReport);
        return licenceMapper.toResponseDto(licenceReport);
    }

    @Override
    @Transactional
    public void deleteReportById(Long reportId) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        licenceValidator.validateReportDeleteRequest(producer, reportId);

        LicenceReport licenceReport = licenceReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Licence report not found with id: " + reportId));

        licenceReportRepository.delete(licenceReport);
    }

    private LicenceReport buildReport(PurchasedLicence purchasedLicence, LicenceReportRequestDto requestDto) {
        LicenceReport report = new LicenceReport();
        report.setPurchasedLicence(purchasedLicence);
        report.setReportDate(LocalDateTime.now());
        report.setReportStatus(ReportStatus.PENDING);
        report.setDescription(requestDto.getDescription());
        return report;
    }

}

