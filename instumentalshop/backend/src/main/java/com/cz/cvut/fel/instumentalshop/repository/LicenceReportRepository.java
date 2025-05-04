package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.LicenceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenceReportRepository extends JpaRepository<LicenceReport, Long> {

    @Query(name = "LicenceReport.isProducerRelatedToReport")
    boolean isProducerRelatedToReport(Long reportId, Long userId);

    @Query(name = "LicenceReport.isCustomerRelatedToReport")
    boolean isCustomerRelatedToReport(Long reportId, Long userId);

}
