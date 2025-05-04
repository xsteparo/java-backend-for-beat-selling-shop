package com.cz.cvut.fel.instumentalshop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("Producer")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class Producer extends User {

    @OneToMany(mappedBy = "producer")
    private Set<ProducerTrackInfo> tracks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "producer_purchased_licence",
            joinColumns = @JoinColumn(name = "producer_id"),
            inverseJoinColumns = @JoinColumn(name = "purchased_licence_id")
    )
    private List<PurchasedLicence> soldLicences;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal salary = BigDecimal.valueOf(0);

    @ManyToMany
    @JoinTable(
            name = "producer_licence_report",
            joinColumns = @JoinColumn(name = "producer_id"),
            inverseJoinColumns = @JoinColumn(name = "licence_report_id")
    )
    private List<LicenceReport> licenceReports;

    @Column( nullable = false)
    private double rating = 0.0;


}
