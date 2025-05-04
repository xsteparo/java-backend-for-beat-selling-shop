package com.cz.cvut.fel.instumentalshop.dto.approval.out;

import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class ProducersAgreementDto {

    private Long trackId;

    List<ProducerTrackInfoDto> trackInfoResponseDtos;

}
