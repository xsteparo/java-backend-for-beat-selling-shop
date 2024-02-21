package com.cvut.cz.fel.ear.instumentalshop.domain;


import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.ProducerIncomeDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

import static com.cvut.cz.fel.ear.instumentalshop.util.query.QuerySqlDefinitions.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@SqlResultSetMapping(
        name = "ProducerIncomeMapping",
        classes = @ConstructorResult(
                targetClass = ProducerIncomeDto.class,
                columns = {
                        @ColumnResult(name = "trackId", type = Long.class),
                        @ColumnResult(name = "trackName", type = String.class),
                        @ColumnResult(name = "salaryFromTrack", type = BigDecimal.class)
                }
        )
)
@NamedQueries({
        @NamedQuery(
                name = "ProducerTrackInfo.checkIfProducerIsLead",
                query = CHECK_IF_PRODUCER_IS_LEAD
        ),

        @NamedQuery(
                name = "ProducerTrackInfo.findProducerTrackInfoByTrackIdAndProducerIdAndAgreedStatus",
                query = FIND_PRODUCER_TRACK_INFO_BY_TRACK_ID_AND_PRODUCER_ID_AND_AGREED_STATUS
        ),

        @NamedQuery(
                name = "ProducerTrackInfo.findByProducerIdAndAgreedForSelling",
                query = FIND_PRODUCER_TRACK_INFO_BY_PRODUCER_ID_AND_AGREED_STATUS
        ),

        @NamedQuery(
                name = "ProducerTrackInfo.checkIfProducerCanDeleteTrack",
                query = CHECK_IF_PRODUCER_CAN_DELETE_TRACK
        )
})

@NamedNativeQuery(
        name = "ProducerTrackInfo.findProducerIncomeByTracks",
        query = FIND_PRODUCER_INCOMES,

        resultSetMapping = "ProducerIncomeMapping"
)
public class ProducerTrackInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producer_id", nullable = false)
    private Producer producer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal profitPercentage;

    @Column(nullable = false)
    private Boolean agreedForSelling = false;

    @Column(nullable = false)
    private Boolean ownsPublishingTrack;

}
