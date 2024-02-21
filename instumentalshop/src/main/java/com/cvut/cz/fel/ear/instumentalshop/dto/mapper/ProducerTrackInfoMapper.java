package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;

import com.cvut.cz.fel.ear.instumentalshop.domain.ProducerTrackInfo;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProducerTrackInfoMapper {

    @Mapping(target = "producerId", source = "producer.id")
    @Mapping(target = "trackId", source = "track.id")
    ProducerTrackInfoDto toResponseDto(ProducerTrackInfo producerTrackInfo);

    List<ProducerTrackInfoDto> toResponseDtoList(List<ProducerTrackInfo> producerTrackInfo);

}
