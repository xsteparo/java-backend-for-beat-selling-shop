package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.enums.ReportStatus;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.LicenceReportRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.LicenceReportDto;

public interface LicenceReportService {

    LicenceReportDto createReport(Long purchasedLicenceId, LicenceReportRequestDto reportRequest);

    LicenceReportDto getReportById(Long reportId);

    LicenceReportDto updateReport(Long reportId, ReportStatus reportStatus);

    void deleteReportById(Long reportId);

}
