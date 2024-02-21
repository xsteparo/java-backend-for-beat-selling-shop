package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;

import com.cvut.cz.fel.ear.instumentalshop.domain.ProducerTrackInfo;
import com.cvut.cz.fel.ear.instumentalshop.domain.Track;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.TrackDto;
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
    TrackDto toResponseDto(Track track);
}
