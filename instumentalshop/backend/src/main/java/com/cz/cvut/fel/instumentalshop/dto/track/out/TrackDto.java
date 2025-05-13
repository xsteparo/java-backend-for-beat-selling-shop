package com.cz.cvut.fel.instumentalshop.dto.track.out;

import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TrackDto {

        private Long id;

        private String name;

        private GenreType genreType;

        private Integer bpm;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<ProducerTrackInfoDto> producerTrackInfoDtoList;

        private String urlNonExclusive;
        private String urlPremium;
        private String urlExclusive;

        // Новые поля для списка:
        private double rating;
        private String length;             // например "3:45"
        private String keyType;                // например "C#"
        private String producerUsername;   // главный продюсер
        private Boolean purchased;         // для состояний Buy / Download

}
