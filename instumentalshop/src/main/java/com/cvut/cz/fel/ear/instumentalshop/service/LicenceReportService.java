package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceReport;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.ReportStatus;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.LicenceReportRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.LicenceReportDto;

public interface LicenceReportService {

    LicenceReportDto createReport(Long purchasedLicenceId, LicenceReportRequestDto reportRequest);

    LicenceReportDto getReportById(Long reportId);

    LicenceReportDto updateReport(Long reportId, ReportStatus reportStatus);

    void deleteReportById(Long reportId);

}
