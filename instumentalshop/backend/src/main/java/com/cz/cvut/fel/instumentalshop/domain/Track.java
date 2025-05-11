package com.cz.cvut.fel.instumentalshop.domain;

import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.domain.enums.KeyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.cz.cvut.fel.instumentalshop.util.query.QuerySqlDefinitions.FIND_BOUGHT_TRACKS_BY_CUSTOMER_ID;
import static com.cz.cvut.fel.instumentalshop.util.query.QuerySqlDefinitions.FIND_TRACKS_BY_PRODUCER_ID;

@Entity
@NamedQueries({
        @NamedQuery(name = "Track.findTracksByProducerId",
                query = FIND_TRACKS_BY_PRODUCER_ID
        ),
        @NamedQuery(name = "Track.findCustomerBoughtTracksForProducer",
                query = FIND_BOUGHT_TRACKS_BY_CUSTOMER_ID
        )
})
@NoArgsConstructor
@Getter
@Setter
@Table
@Builder
@AllArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenreType genre;

    @Column(nullable = false)
    private Integer bpm;

    private KeyType keyType;

    private int likes;

    private int plays;

    private double rating;

    private LocalDateTime createdAt;

    private double lastRatingDelta;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProducerTrackInfo> producerTrackInfos;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LicenceTemplate> licenceTemplates;

    @Column(nullable = false)
    private boolean allProducersAgreedForSelling = false;

    private boolean isExclusiveBought = false;

    @OneToMany(mappedBy = "track")
    private List<PurchasedLicence> purchasedLicence;

    @Column(name = "url_non_exclusive")
    private String urlNonExclusive;

    @Column(name = "url_premium")
    private String urlPremium;

    @Column(name = "url_exclusive")
    private String urlExclusive;

    public double getNormalizedScore() {
        if (plays == 0) return 0;
        return (double) likes / plays;
    }

}
