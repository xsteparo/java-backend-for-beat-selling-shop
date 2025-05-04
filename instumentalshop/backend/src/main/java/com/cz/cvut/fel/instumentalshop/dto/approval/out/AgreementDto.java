package com.cz.cvut.fel.instumentalshop.dto.approval.out;

import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AgreementDto {

    private Long trackId;

    private List<ProducerTrackInfoDto> producerTrackInfosResponseDto;

}
