package com.cvut.cz.fel.ear.instumentalshop.service.impl;

import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.ProducerTrackInfo;
import com.cvut.cz.fel.ear.instumentalshop.domain.Track;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.ProducerTrackInfoMapper;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.TrackMapper;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.in.ProducerShareRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.in.TrackRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.TrackDto;
import com.cvut.cz.fel.ear.instumentalshop.exception.ProducerTrackInfoNotFoundException;
import com.cvut.cz.fel.ear.instumentalshop.exception.UserNotFoundException;
import com.cvut.cz.fel.ear.instumentalshop.repository.ProducerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.TrackRepository;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import com.cvut.cz.fel.ear.instumentalshop.service.TrackService;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.TrackValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AuthenticationService authenticationService;

    private final ProducerTrackInfoRepository producerTrackInfoRepository;

    private final TrackRepository trackRepository;

    private final ProducerRepository producerRepository;

    private final ProducerTrackInfoMapper producerTrackInfoMapper;

    private final TrackMapper trackMapper;

    private final TrackValidator trackValidator;


    @Override
    @Transactional
    public TrackDto createTrack(TrackRequestDto requestDto) {
        Producer leadProducer = authenticationService.getRequestingProducerFromSecurityContext();

        if (isSingleOwner(requestDto)) {
            trackValidator.validateTrackCreationRequestWithSingleOwner(requestDto, leadProducer);
            return createTrackWithSingleOwner(requestDto, leadProducer);
        }

        trackValidator.validateTrackCreationRequestWithMultiOwners(requestDto, leadProducer);
        return createTrackWithMultipleOwners(requestDto, leadProducer);
    }

    @Override
    @Transactional
    public TrackDto getTrackById(Long trackId) {
        Track track = trackRepository.findTrackById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track is not found"));

        return getTrackResponseWithProducersIds(track);
    }

    @Override
    @Transactional
    public List<TrackDto> getAllTracks() {
        List<Track> tracks = trackRepository.findAll();

        return tracks.stream()
                .map(this::getTrackResponseWithProducersIds)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TrackDto> getAllTracksByProducer(Long producerId) {
        List<Track> tracks = trackRepository.findTracksByProducerId(producerId);

        return tracks.stream()
                .map(this::getTrackResponseWithProducersIds)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TrackDto> getCustomerBoughtTracksForProducer(Long customerId) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        List<Track> tracks = trackRepository.findCustomerBoughtTracksForProducer(customerId, producer.getId());

        return tracks.stream()
                .map(this::getTrackResponseWithProducersIds)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTrack(Long trackId) {
        Producer currentProducer = authenticationService.getRequestingProducerFromSecurityContext();

        trackValidator.validateTrackDeletionRequest(trackId, currentProducer.getId());

        trackRepository.deleteById(trackId);
    }

    @Override
    @Transactional
    public TrackDto updateTrack(Long trackId, TrackRequestDto requestDto) {
        Producer currentProducer = authenticationService.getRequestingProducerFromSecurityContext();

        Track track = trackRepository.findTrackById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track is not found"));

        trackValidator.validateUpdateRequest(requestDto, trackId, currentProducer.getId());

        track.setName(requestDto.getName());
        track.setBpm(requestDto.getBpm());
        track.setGenre(requestDto.getGenreType());

        track = trackRepository.save(track);

        return trackMapper.toResponseDto(track);

    }

    @Override
    @Transactional
    public List<ProducerTrackInfoDto> getTrackApprovalsList() {
        Producer currentProducer = authenticationService.getRequestingProducerFromSecurityContext();
        List<ProducerTrackInfo> producerTrackInfos = producerTrackInfoRepository.findByProducerIdAndAgreedForSelling(currentProducer.getId(), false);
        return producerTrackInfoMapper.toResponseDtoList(producerTrackInfos);
    }

    @Override
    @Transactional
    public List<ProducerTrackInfoDto> confirmProducerAgreement(Long trackId) {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();
        Track track = trackRepository.findTrackById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track is not found"));

        ProducerTrackInfo producerTrackInfo = getProducerTrackInfo(trackId, producer);

        updateProducerAgreement(producerTrackInfo);

        boolean isAllAgreed = isAllProducersAgreed(trackId);
        updateTrackAgreementStatus(track, isAllAgreed);

        return getProducerTrackInfoResponses(trackId);
    }

    private TrackDto createTrackWithSingleOwner(TrackRequestDto requestDto, Producer leadProducer) {
        Track track = buildTrack(requestDto, true);

        ProducerTrackInfo producerTrackInfo = buildProducerTrackInfo(track, leadProducer, BigDecimal.valueOf(100), true);

        producerTrackInfo = producerTrackInfoRepository.save(producerTrackInfo);

        return trackMapper.toResponseWithSingleOwnerDto(producerTrackInfo.getTrack());
    }

    private TrackDto createTrackWithMultipleOwners(TrackRequestDto requestDto, Producer leadProducer) {
        Track track = buildTrack(requestDto, false);

        List<ProducerTrackInfo> producerTrackInfos = buildProducerTrackInfosWithAllProducers(track, requestDto, leadProducer);

        track.setProducerTrackInfos(producerTrackInfos);
        track = trackRepository.save(track);

        return trackMapper.toResponseWithMultipleOwnersDto(track);
    }

    private Track buildTrack(TrackRequestDto requestDto, boolean isAllAgreedForSelling) {
        return Track.builder()
                .name(requestDto.getName())
                .bpm(requestDto.getBpm())
                .genre(requestDto.getGenreType())
                .allProducersAgreedForSelling(isAllAgreedForSelling)
                .build();
    }

    private List<ProducerTrackInfo> buildProducerTrackInfosWithAllProducers(Track track, TrackRequestDto requestDto, Producer leadProducer) {
        List<ProducerTrackInfo> producerTrackInfos = new ArrayList<>();

        ProducerTrackInfo leadProducerTrackInfo = buildProducerTrackInfo(track, leadProducer, BigDecimal.valueOf(requestDto.getMainProducerPercentage()), true);

        producerTrackInfos.add(leadProducerTrackInfo);

        addCoProducerTrackInfos(track, producerTrackInfos, requestDto);

        return producerTrackInfos;
    }

    private void addCoProducerTrackInfos(Track track, List<ProducerTrackInfo> producerTrackInfos, TrackRequestDto requestDto) {
        for (ProducerShareRequestDto shareRequestDto : requestDto.getProducerShares()) {
            Producer coProducer = producerRepository.findProducerByUsername(shareRequestDto.getProducerName())
                    .orElseThrow(() -> new UserNotFoundException("Producer not found with id: " + shareRequestDto.getProducerName() + "not found "));

            Integer profitPercentage = shareRequestDto.getProfitPercentage();

            ProducerTrackInfo producerTrackInfo = buildProducerTrackInfo(track, coProducer, BigDecimal.valueOf(profitPercentage), false);

            producerTrackInfos.add(producerTrackInfo);
        }
    }

    private ProducerTrackInfo buildProducerTrackInfo(Track track, Producer producer, BigDecimal profitPercentage, boolean isLeadProducer) {
        if (isLeadProducer) {
            return ProducerTrackInfo.builder()
                    .track(track)
                    .producer(producer)
                    .profitPercentage(profitPercentage)
                    .ownsPublishingTrack(true)
                    .agreedForSelling(true)
                    .build();
        }

        return ProducerTrackInfo.builder()
                .track(track)
                .producer(producer)
                .profitPercentage(profitPercentage)
                .ownsPublishingTrack(false)
                .agreedForSelling(false)
                .build();

    }

    private boolean isSingleOwner(TrackRequestDto requestDto) {
        return requestDto.getMainProducerPercentage() == null &&
                (requestDto.getProducerShares() == null || requestDto.getProducerShares().isEmpty());
    }

    private ProducerTrackInfo getProducerTrackInfo(Long trackId, Producer producer) {
        return producerTrackInfoRepository
                .findProducerTrackInfoByTrackIdAndProducerIdAndAgreedStatus(trackId, producer.getId(), false)
                .orElseThrow(() -> new ProducerTrackInfoNotFoundException(
                        "Producer track information not found or already confirmed for track ID " + trackId
                                + " and producer ID " + producer.getId()));
    }

    private void updateProducerAgreement(ProducerTrackInfo producerTrackInfo) {
        producerTrackInfo.setAgreedForSelling(true);
        producerTrackInfoRepository.save(producerTrackInfo);
    }

    private boolean isAllProducersAgreed(Long trackId) {
        return producerTrackInfoRepository.findByTrackId(trackId).stream()
                .allMatch(ProducerTrackInfo::getAgreedForSelling);
    }

    private void updateTrackAgreementStatus(Track track, boolean isAllAgreed) {
        track.setAllProducersAgreedForSelling(isAllAgreed);
    }

    private List<ProducerTrackInfoDto> getProducerTrackInfoResponses(Long trackId) {
        List<ProducerTrackInfo> producerTrackInfos = producerTrackInfoRepository.findByTrackId(trackId);
        return producerTrackInfoMapper.toResponseDtoList(producerTrackInfos);
    }

    private TrackDto getTrackResponseWithProducersIds(Track track) {
        TrackDto responseDto = trackMapper.toResponseDto(track);

        if (track.getProducerTrackInfos() != null) {
            List<ProducerTrackInfoDto> producerTrackInfosDtoList = track.getProducerTrackInfos().stream()
                    .map(producerTrackInfo -> {
                        ProducerTrackInfoDto producerTrackInfoDto = new ProducerTrackInfoDto();
                        producerTrackInfoDto.setProducerId(producerTrackInfo.getProducer().getId());
                        producerTrackInfoDto.setProducerUsername(producerTrackInfo.getProducer().getUsername());
                        producerTrackInfoDto.setOwnsPublishingTrack(producerTrackInfo.getOwnsPublishingTrack());
                        return producerTrackInfoDto;
                    })
                    .collect(Collectors.toList());

            responseDto.setProducerTrackInfoDtoList(producerTrackInfosDtoList);
        }

        return responseDto;
    }
}