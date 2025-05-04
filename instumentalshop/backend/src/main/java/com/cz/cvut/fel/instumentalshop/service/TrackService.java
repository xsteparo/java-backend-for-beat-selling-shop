package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;

import java.util.List;

public interface TrackService {

    List<ProducerTrackInfoDto> getTrackApprovalsList();

    List<ProducerTrackInfoDto> confirmProducerAgreement(Long trackId);

    TrackDto createTrack(TrackRequestDto requestDto);

    TrackDto getTrackById(Long trackId);

    List<TrackDto> getAllTracks();

    List<TrackDto> getAllTracksByProducer(Long producerId);

    List<TrackDto> getCustomerBoughtTracksForProducer(Long customerId);

    TrackDto updateTrack(Long trackId, TrackRequestDto requestDto);

    void deleteTrack(Long trackId);

}
