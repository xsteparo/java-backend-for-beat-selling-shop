package com.cz.cvut.fel.instumentalshop.domain;


import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LicenceTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = true)
    private Track track;

    @Column(name = "period_days")
    private Integer validityPeriodDays;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "licence_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private LicenceType licenceType;

    @ElementCollection(targetClass = Platform.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "licence_template_platforms", joinColumns = @JoinColumn(name = "licence_template_id"))
    @Column(name = "platform")
    @Enumerated(EnumType.STRING)
    private List<Platform> availablePlatforms;

}
