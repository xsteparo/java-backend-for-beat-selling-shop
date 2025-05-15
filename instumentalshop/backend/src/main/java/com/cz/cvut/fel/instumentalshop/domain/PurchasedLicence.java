package com.cz.cvut.fel.instumentalshop.domain;

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

    @ManyToOne(optional = false)
    private Customer customer;

    @ManyToOne(optional = false)
    private Producer producer;

    @ManyToOne(optional = false)
    private Track track;

    @ManyToOne(optional = false)
    private LicenceTemplate licenceTemplate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime purchaseDate;

    private LocalDateTime expiredDate;

}
