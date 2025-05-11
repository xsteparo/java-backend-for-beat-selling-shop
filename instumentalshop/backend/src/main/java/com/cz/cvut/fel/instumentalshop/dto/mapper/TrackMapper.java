package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrackMapper {

    @Mapping(target = "producerTrackInfoDtoList", source = "producerTrackInfos")
    @Mapping(target = "trackId", source = "id")
    @Mapping(target = "genreType" , source = "genre")
    TrackDto toResponseWithMultipleOwnersDto(Track track);

    @Mapping(target = "trackId", source = "id")
    @Mapping(target = "genreType" , source = "genre")
    @Mapping(target = "producerTrackInfoDtoList", ignore = true)
    TrackDto toResponseWithSingleOwnerDto(Track track);

    @Mapping(target = "producerId", source = "producer.id")
    @Mapping(target= "producerUsername", source = "producer.username")
    ProducerTrackInfoDto mapProducerTrackInfo(ProducerTrackInfo producerTrackInfo);

    List<ProducerTrackInfoDto> mapProducerTrackInfos(List<ProducerTrackInfo> producerTrackInfos);

    @Mapping(target = "trackId", source = "id")
    @Mapping(target = "genreType" , source = "genre")
    @Mapping(target = "producerTrackInfoDtoList", ignore = true)

    @Mapping(target = "rating",                source = "rating")
//    @Mapping(target = "length",                source = "length")
    @Mapping(target = "keyType",                   source = "keyType")
    @Mapping(
            target = "producerUsername",
            expression = "java("
                    + "track.getProducerTrackInfos().stream()"
                    +     ".filter(ProducerTrackInfo::getOwnsPublishingTrack)"
                    +     ".findFirst()"
                    +     ".map(info -> info.getProducer().getUsername())"
                    +     ".orElse(null)"
                    + ")"
    )
    @Mapping(target = "purchased",             constant = "false")
    TrackDto toResponseDto(Track track);
}
