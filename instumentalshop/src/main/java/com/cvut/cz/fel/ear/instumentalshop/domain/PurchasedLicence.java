package com.cvut.cz.fel.ear.instumentalshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Purchased_Licences")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PurchasedLicence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (cascade = CascadeType.PERSIST)
    private Customer customer;

    @ManyToMany(mappedBy = "soldLicences")
    private List<Producer> producers;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime purchaseDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime expiredDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Track track;

    @ManyToOne()
    @JoinColumn(name = "licence_template_id", nullable = false)
    private LicenceTemplate licenceTemplate;

    @OneToMany(mappedBy = "purchasedLicence", cascade = CascadeType.ALL)
    private List<LicenceReport> licenceReports;

}
