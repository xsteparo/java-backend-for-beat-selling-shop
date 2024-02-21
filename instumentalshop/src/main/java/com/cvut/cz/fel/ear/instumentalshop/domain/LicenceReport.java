package com.cvut.cz.fel.ear.instumentalshop.domain;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.cvut.cz.fel.ear.instumentalshop.util.query.QuerySqlDefinitions.IS_CUSTOMER_RELATED_TO_REPORT;
import static com.cvut.cz.fel.ear.instumentalshop.util.query.QuerySqlDefinitions.IS_PRODUCER_RELATED_TO_REPORT;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "LicenceReport.isProducerRelatedToReport",
                query = IS_PRODUCER_RELATED_TO_REPORT
        ),
        @NamedQuery(
                name = "LicenceReport.isCustomerRelatedToReport",
                query = IS_CUSTOMER_RELATED_TO_REPORT
        )
})
@NoArgsConstructor
@Getter
@Setter
public class LicenceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime reportDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @ManyToOne
    private PurchasedLicence purchasedLicence;
}
