package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TrackService {

    Page<TrackDto> listTracks(String tab,
                              String search,
                              String genre,
                              String tempoRange,
                              String key,
                              String sort,
                              int page,
                              int size);

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
