package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerTrackInfoMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.TrackMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private static final long CHUNK_SIZE = 1024 * 1024; // 1 MB

    private final AuthenticationService authenticationService;
    private final ProducerTrackInfoRepository producerTrackInfoRepository;
    private final TrackRepository trackRepository;
    private final ProducerRepository producerRepository;
    private final ProducerTrackInfoMapper producerTrackInfoMapper;
    private final TrackMapper trackMapper;
    private final TrackValidator trackValidator;

    @PostConstruct
    public void init() {
        tracksUploadPath = Paths.get(tracksUploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(tracksUploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create folder for upload tracks: " + tracksUploadPath, e);
        }
    }

    //=====================================
    //    Public API
    //=====================================

    @Override
    @Transactional(readOnly = true)
    public Page<TrackDto> findAll(TrackFilterDto filter, Pageable pageable) {
        Specification<Track> spec = TrackSpecificationBuilder.fromFilter(filter);
        // Специальная логика для "trending" вкладки, если нужна
        if ("trending".equalsIgnoreCase(filter.getTab())) {
            Double avgRating = trackRepository.findAverageRating();
            if (avgRating != null) {
                spec = spec.and((root, cq, cb) ->
                        cb.greaterThan(root.get("rating"), avgRating)
                );
            }
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "lastRatingDelta"));
        }
        return trackRepository.findAll(spec, pageable)
                .map(trackMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackDto findById(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));
        return trackMapper.toResponseDto(track);
    }

    @Override
    public Resource loadAsResource(Long trackId) throws IOException {
        String relPath = trackRepository.findFilePathById(trackId);
        if (relPath == null) {
            throw new EntityNotFoundException("Track not found: " + trackId);
        }
        if (relPath.startsWith("/")) relPath = relPath.substring(1);
        String prefix = "uploads/tracks/";
        if (relPath.startsWith(prefix)) {
            relPath = relPath.substring(prefix.length());
        }
        Path file = tracksUploadPath.resolve(relPath);
        Resource res = new UrlResource(file.toUri());
        if (!res.exists()) {
            throw new EntityNotFoundException("File not found on disk: " + file);
        }
        return res;
    }

    @Override
    public ResourceRegion buildRegion(Resource resource, List<HttpRange> ranges, long contentLength) throws IOException {
        if (ranges == null || ranges.isEmpty()) {
            long len = Math.min(CHUNK_SIZE, contentLength);
            return new ResourceRegion(resource, 0, len);
        }
        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(contentLength);
        long end   = range.getRangeEnd(contentLength);
        long len   = Math.min(CHUNK_SIZE, end - start + 1);
        return new ResourceRegion(resource, start, len);
    }


    //=====================================
    //    Producer API
    //=====================================

    @Override
    @Transactional
    public TrackDto createTrack(TrackRequestDto dto) throws IOException {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();
        boolean singleOwner = dto.getMainProducerPercentage() == null
                && (dto.getProducerShares() == null || dto.getProducerShares().isEmpty());
        if (singleOwner) {
            trackValidator.validateTrackCreationRequestWithSingleOwner(dto, lead);
        } else {
            trackValidator.validateTrackCreationRequestWithMultiOwners(dto, lead);
        }
        return createWithFiles(dto, lead, singleOwner);
    }

    @Override
    @Transactional
    public TrackDto updateTrack(Long trackId, TrackRequestDto dto) {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));
        trackValidator.validateUpdateRequest(dto, trackId, lead.getId());
        track.setName(dto.getName());
        track.setBpm(dto.getBpm());
        track.setGenre(dto.getGenreType());
        Track updated = trackRepository.save(track);
        return trackMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteTrack(Long trackId) {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();
        trackValidator.validateTrackDeletionRequest(trackId, lead.getId());
        trackRepository.deleteById(trackId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProducerTrackInfoDto> getTrackApprovalsList() {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();
        return producerTrackInfoRepository
                .findByProducerIdAndAgreedForSelling(lead.getId(), false)
                .stream()
                .map(producerTrackInfoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProducerTrackInfoDto> confirmProducerAgreement(Long trackId) {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();
        ProducerTrackInfo info = producerTrackInfoRepository
                .findProducerTrackInfoByTrackIdAndProducerIdAndAgreedStatus(trackId, lead.getId(), false)
                .orElseThrow(() -> new ProducerTrackInfoNotFoundException(
                        "No pending agreement for track " + trackId + " and producer " + lead.getId()));
        info.setAgreedForSelling(true);
        producerTrackInfoRepository.save(info);

        boolean allAgreed = producerTrackInfoRepository
                .findByTrackId(trackId)
                .stream().allMatch(ProducerTrackInfo::getAgreedForSelling);

        Track track = info.getTrack();
        track.setAllProducersAgreedForSelling(allAgreed);
        trackRepository.save(track);

        return producerTrackInfoRepository.findByTrackId(trackId)
                .stream()
                .map(producerTrackInfoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    //=====================================
    //    Producer’s View on Purchases
    //=====================================

    @Override
    @Transactional(readOnly = true)
    public List<TrackDto> findAllByProducer(Long producerId) {
        return trackRepository.findTracksByProducerId(producerId).stream()
                .map(trackMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackDto> findCustomerPurchasedTracksForProducer(Long customerId) {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();
        return trackRepository
                .findCustomerBoughtTracksForProducer(customerId, lead.getId())
                .stream()
                .map(trackMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    //=====================================
    //    Private helpers
    //=====================================

    private TrackDto createWithFiles(TrackRequestDto dto, Producer lead, boolean allAgreed) throws IOException {
        String urlMp3 = storeFile(dto.getNonExclusiveFile(), "mp3");
        String urlWav = dto.getPremiumFile() != null
                ? storeFile(dto.getPremiumFile(), "wav")
                : null;
        String urlZip = dto.getExclusiveFile() != null
                ? storeFile(dto.getExclusiveFile(), "zip")
                : null;

        Track track = Track.builder()
                .name(dto.getName())
                .genre(dto.getGenreType())
                .bpm(dto.getBpm())
                .allProducersAgreedForSelling(allAgreed)
                .urlNonExclusive(urlMp3)
                .urlPremium(urlWav)
                .urlExclusive(urlZip)
                .build();

        List<ProducerTrackInfo> infos = buildProducerTrackInfos(track, dto, lead, allAgreed);
        track.setProducerTrackInfos(infos);

        Track saved = trackRepository.save(track);
        return trackMapper.toResponseDto(saved);
    }

    private String storeFile(MultipartFile file, String ext) throws IOException {
        String filename = UUID.randomUUID() + "." + ext;
        Path target = tracksUploadPath.resolve(filename);
        file.transferTo(target.toFile());
        return "/uploads/tracks/" + filename;
    }

    private List<ProducerTrackInfo> buildProducerTrackInfos(Track track, TrackRequestDto dto,
                                                            Producer lead, boolean allAgreed) {
        List<ProducerTrackInfo> infos = new ArrayList<>();
        BigDecimal leadShare = allAgreed
                ? BigDecimal.valueOf(100)
                : BigDecimal.valueOf(dto.getMainProducerPercentage());
        infos.add(buildProducerTrackInfo(track, lead, leadShare, true));

        if (dto.getProducerShares() != null) {
            dto.getProducerShares().forEach(ps -> {
                Producer co = producerRepository
                        .findProducerByUsername(ps.getProducerName())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Producer not found: " + ps.getProducerName()));
                infos.add(buildProducerTrackInfo(track, co,
                        BigDecimal.valueOf(ps.getProfitPercentage()), false));
            });
        }
        return infos;
    }

    private ProducerTrackInfo buildProducerTrackInfo(Track track, Producer producer,
                                                     BigDecimal profitPercentage, boolean isLead) {
        return ProducerTrackInfo.builder()
                .track(track)
                .producer(producer)
                .profitPercentage(profitPercentage)
                .ownsPublishingTrack(isLead)
                .agreedForSelling(isLead)
                .build();
    }
}
