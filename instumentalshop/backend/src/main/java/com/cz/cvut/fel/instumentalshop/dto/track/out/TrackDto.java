package com.cz.cvut.fel.instumentalshop.dto.track.out;

import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.domain.enums.KeyType;
import com.cz.cvut.fel.instumentalshop.dto.newDto.LicenceTemplateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackDto {

        private Long id;

        private String name;

        private GenreType genreType;

        private Integer bpm;

//        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<LicenceTemplateDto> licenceTemplates;

        private String urlNonExclusive;
        private String urlPremium;
        private String urlExclusive;

        // Новые поля для списка:
        private double rating;
        private int likes;
        private int plays;
        private KeyType key;                // например "C#"
        private String producerUsername;   // главный продюсер
        private Boolean purchased;         // для состояний Buy / Download

}
