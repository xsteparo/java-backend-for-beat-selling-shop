package com.cz.cvut.fel.instumentalshop.dto.track.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ProducerTrackInfoDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long trackId;

    private Long producerId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String producerUsername;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer profitPercentage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean agreedForSelling;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean ownsPublishingTrack;

}
