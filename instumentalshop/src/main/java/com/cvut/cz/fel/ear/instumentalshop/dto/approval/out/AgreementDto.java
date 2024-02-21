package com.cvut.cz.fel.ear.instumentalshop.dto.approval.out;

import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AgreementDto {

    private Long trackId;

    private List<ProducerTrackInfoDto> producerTrackInfosResponseDto;

}
