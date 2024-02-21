package com.cvut.cz.fel.ear.instumentalshop.dto.approval.out;

import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class ProducersAgreementDto {

    private Long trackId;

    List<ProducerTrackInfoDto> trackInfoResponseDtos;

}
