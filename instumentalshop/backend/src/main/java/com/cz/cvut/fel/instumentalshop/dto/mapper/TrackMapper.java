package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackMapper {

    // URL-ы
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "genreType", source = "genre")
    @Mapping(target = "bpm", source = "bpm")

    // URL-ы
    @Mapping(target = "urlNonExclusive", source = "urlNonExclusive")
    @Mapping(target = "urlPremium", source = "urlPremium")
    @Mapping(target = "urlExclusive", source = "urlExclusive")

    @Mapping(target = "rating", source = "rating")
    // @Mapping(target = "length",         source = "length") // если понадобится
    @Mapping(source = "keyType", target = "key")

    // главный продюсер
    @Mapping(
            target = "producerUsername",
            expression = "java(track.getProducer().getUsername())"
    )

    // пока константой, потом можно по-умному
    @Mapping(target = "purchased", constant = "false")
    @Mapping(target = "likes", source = "likes")
    @Mapping(target = "plays", source = "plays")

    // новый маппинг: список трёх шаблонов лицензий
    @Mapping(
            target = "licenceTemplates",
            expression = "java(\n" +
                    "  track.getLicenceTemplates().stream()\n" +
                    "    .map(tpl -> new com.cz.cvut.fel.instumentalshop.dto.newDto.LicenceTemplateDto(\n" +
                    "        tpl.getId(),\n" +
                    "        tpl.getLicenceType(),\n" +
                    "        tpl.getPrice(),\n" +
                    "        tpl.getValidityPeriodDays()\n" +
                    "    ))\n" +
                    "    .collect(java.util.stream.Collectors.toList())\n" +
                    ")"
    )
    TrackDto toResponseDto(Track track);
}

