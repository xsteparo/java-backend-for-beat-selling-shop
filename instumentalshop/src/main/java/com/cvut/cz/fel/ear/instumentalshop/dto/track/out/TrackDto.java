package com.cvut.cz.fel.ear.instumentalshop.dto.track.out;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.GenreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TrackDto {

        private Long trackId;

        private String name;

        private GenreType genreType;

        private Integer bpm;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<ProducerTrackInfoDto> producerTrackInfoDtoList;

}
