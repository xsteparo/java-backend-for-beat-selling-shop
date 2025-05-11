package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerTrackInfoMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.TrackMapper;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.exception.ProducerTrackInfoNotFoundException;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.TrackService;
import com.cz.cvut.fel.instumentalshop.util.validator.TrackValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    @Value("${app.upload.tracks-path}")
    private String tracksUploadDir;

    private Path tracksUploadPath;

    private final AuthenticationService authenticationService;
    private final ProducerTrackInfoRepository producerTrackInfoRepository;
    private final TrackRepository trackRepository;
    private final ProducerRepository producerRepository;
    private final ProducerTrackInfoMapper producerTrackInfoMapper;
    private final TrackMapper trackMapper;
    private final TrackValidator trackValidator;

    @PostConstruct
    public void init() {
        tracksUploadPath = Paths.get(tracksUploadDir)
                .toAbsolutePath()
                .normalize();
        try {
            Files.createDirectories(tracksUploadPath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to create folder for upload tracks: " + tracksUploadPath, e
            );
        }
    }

    private String storeFile(MultipartFile file, String ext) {
        String filename = UUID.randomUUID() + "." + ext;
        Path target = tracksUploadPath.resolve(filename);
        try {
            file.transferTo(target.toFile());
            return "/uploads/tracks/" + filename;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to store file " + file.getOriginalFilename(), e
            );
        }
    }

    @Override
    @Transactional
    public TrackDto createTrack(TrackRequestDto dto) {
        Producer leadProducer = authenticationService.getRequestingProducerFromSecurityContext();
        boolean singleOwner = isSingleOwner(dto);

        if (singleOwner) {
            trackValidator.validateTrackCreationRequestWithSingleOwner(dto, leadProducer);
        } else {
            trackValidator.validateTrackCreationRequestWithMultiOwners(dto, leadProducer);
        }

        return createWithFiles(dto, leadProducer, singleOwner);
    }

    private TrackDto createWithFiles(TrackRequestDto dto,
                                     Producer leadProducer,
                                     boolean allAgreedForSelling) {
        // 1) Store files
        String urlMp3 = storeFile(dto.getNonExclusiveFile(), "mp3");
        String urlWav = dto.getPremiumFile() != null
                ? storeFile(dto.getPremiumFile(), "wav")
                : null;
        String urlZip = dto.getExclusiveFile() != null
                ? storeFile(dto.getExclusiveFile(), "zip")
                : null;

        // 2) Build Track entity
        Track track = Track.builder()
                .name(dto.getName())
                .genre(dto.getGenreType())
                .bpm(dto.getBpm())
                .allProducersAgreedForSelling(allAgreedForSelling)
                .urlNonExclusive(urlMp3)
                .urlPremium(urlWav)
                .urlExclusive(urlZip)
                .build();

        // 3) Build ProducerTrackInfo list
        List<ProducerTrackInfo> infos = buildProducerTrackInfos(track, dto, leadProducer, allAgreedForSelling);
        track.setProducerTrackInfos(infos);

        // 4) Persist track + infos
        Track saved = trackRepository.save(track);

        // 5) Map to DTO
        if (allAgreedForSelling) {
            return trackMapper.toResponseWithSingleOwnerDto(saved);
        } else {
            return trackMapper.toResponseWithMultipleOwnersDto(saved);
        }
    }


    private boolean isSingleOwner(TrackRequestDto dto) {
        return dto.getMainProducerPercentage() == null
                && (dto.getProducerShares() == null || dto.getProducerShares().isEmpty());
    }

    private List<ProducerTrackInfo> buildProducerTrackInfos(Track track,
                                                            TrackRequestDto dto,
                                                            Producer leadProducer,
                                                            boolean allAgreed) {
        List<ProducerTrackInfo> infos = new ArrayList<>();

        BigDecimal leadShare = allAgreed
                ? BigDecimal.valueOf(100)
                : BigDecimal.valueOf(dto.getMainProducerPercentage());
        infos.add(buildProducerTrackInfo(track, leadProducer, leadShare, true));

        if (dto.getProducerShares() != null) {
            dto.getProducerShares().forEach(ps -> {
                Producer co = producerRepository
                        .findProducerByUsername(ps.getProducerName())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Producer not found: " + ps.getProducerName()));
                infos.add(buildProducerTrackInfo(
                        track,
                        co,
                        BigDecimal.valueOf(ps.getProfitPercentage()),
                        false));
            });
        }

        return infos;
    }

    private ProducerTrackInfo buildProducerTrackInfo(Track track,
                                                     Producer producer,
                                                     BigDecimal profitPercentage,
                                                     boolean isLead) {
        return ProducerTrackInfo.builder()
                .track(track)
                .producer(producer)
                .profitPercentage(profitPercentage)
                .ownsPublishingTrack(isLead)
                .agreedForSelling(isLead)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TrackDto getTrackById(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));
        return getTrackResponseWithProducers(track);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackDto> getAllTracks() {
        return trackRepository.findAll().stream()
                .map(this::getTrackResponseWithProducers)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackDto> getAllTracksByProducer(Long producerId) {
        return trackRepository.findTracksByProducerId(producerId).stream()
                .map(this::getTrackResponseWithProducers)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackDto> getCustomerBoughtTracksForProducer(Long customerId) {
        Producer p = authenticationService.getRequestingProducerFromSecurityContext();
        return trackRepository.findCustomerBoughtTracksForProducer(customerId, p.getId()).stream()
                .map(this::getTrackResponseWithProducers)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTrack(Long trackId) {
        Producer p = authenticationService.getRequestingProducerFromSecurityContext();
        trackValidator.validateTrackDeletionRequest(trackId, p.getId());
        trackRepository.deleteById(trackId);
    }

    @Override
    @Transactional
    public TrackDto updateTrack(Long trackId, TrackRequestDto dto) {
        Producer p = authenticationService.getRequestingProducerFromSecurityContext();
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));
        trackValidator.validateUpdateRequest(dto, trackId, p.getId());

        track.setName(dto.getName());
        track.setBpm(dto.getBpm());
        track.setGenre(dto.getGenreType());
        // File updates can be handled similarly if needed

        Track updated = trackRepository.save(track);
        return trackMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProducerTrackInfoDto> getTrackApprovalsList() {
        Producer p = authenticationService.getRequestingProducerFromSecurityContext();
        return producerTrackInfoRepository
                .findByProducerIdAndAgreedForSelling(p.getId(), false)
                .stream()
                .map(producerTrackInfoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProducerTrackInfoDto> confirmProducerAgreement(Long trackId) {
        Producer p = authenticationService.getRequestingProducerFromSecurityContext();
        ProducerTrackInfo info = producerTrackInfoRepository
                .findProducerTrackInfoByTrackIdAndProducerIdAndAgreedStatus(trackId, p.getId(), false)
                .orElseThrow(() -> new ProducerTrackInfoNotFoundException(
                        "No pending agreement for track " + trackId + " and producer " + p.getId()));

        info.setAgreedForSelling(true);
        producerTrackInfoRepository.save(info);

        boolean allAgreed = producerTrackInfoRepository
                .findByTrackId(trackId)
                .stream()
                .allMatch(ProducerTrackInfo::getAgreedForSelling);

        Track track = info.getTrack();
        track.setAllProducersAgreedForSelling(allAgreed);
        trackRepository.save(track);

        return producerTrackInfoRepository
                .findByTrackId(trackId)
                .stream()
                .map(producerTrackInfoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private TrackDto getTrackResponseWithProducers(Track track) {
        TrackDto dto = trackMapper.toResponseDto(track);
        if (track.getProducerTrackInfos() != null) {
            dto.setProducerTrackInfoDtoList(
                    track.getProducerTrackInfos().stream()
                            .map(info -> {
                                ProducerTrackInfoDto pdto = new ProducerTrackInfoDto();
                                pdto.setProducerId(info.getProducer().getId());
                                pdto.setProducerUsername(info.getProducer().getUsername());
                                pdto.setOwnsPublishingTrack(info.getOwnsPublishingTrack());
                                pdto.setProfitPercentage(info.getProfitPercentage());
                                pdto.setAgreedForSelling(info.getAgreedForSelling());
                                return pdto;
                            })
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }
}