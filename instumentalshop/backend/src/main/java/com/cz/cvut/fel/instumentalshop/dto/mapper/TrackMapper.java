package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrackMapper {

    @Mapping(target = "producerTrackInfoDtoList", source = "producerTrackInfos")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "genreType" , source = "genre")
    TrackDto toResponseWithMultipleOwnersDto(Track track);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "genreType" , source = "genre")
    @Mapping(target = "producerTrackInfoDtoList", ignore = true)
    TrackDto toResponseWithSingleOwnerDto(Track track);

    @Mapping(target = "producerId", source = "producer.id")
    @Mapping(target= "producerUsername", source = "producer.username")
    ProducerTrackInfoDto mapProducerTrackInfo(ProducerTrackInfo producerTrackInfo);

    List<ProducerTrackInfoDto> mapProducerTrackInfos(List<ProducerTrackInfo> producerTrackInfos);

    @Mapping(target = "id",                source = "id")
    @Mapping(target = "name",              source = "name")
    @Mapping(target = "genreType",         source = "genre")
    @Mapping(target = "bpm",               source = "bpm")

    // URL-ы
    @Mapping(target = "urlNonExclusive",   source = "urlNonExclusive")
    @Mapping(target = "urlPremium",        source = "urlPremium")
    @Mapping(target = "urlExclusive",      source = "urlExclusive")

    @Mapping(target = "rating",            source = "rating")
//    @Mapping(target = "length",            source = "length")
    @Mapping(target = "keyType",           source = "keyType")

    // главный продюсер
    @Mapping(
            target     = "producerUsername",
            expression = "java(track.getProducerTrackInfos().stream()\n" +
                    "     .filter(ProducerTrackInfo::getOwnsPublishingTrack)\n" +
                    "     .findFirst()\n" +
                    "     .map(info -> info.getProducer().getUsername())\n" +
                    "     .orElse(null))"
    )

    // пока константой, потом можно по-умному
    @Mapping(target = "purchased",         constant = "false")
    TrackDto toResponseDto(Track track);
}

