package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.KeyType;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.mapper.TrackMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final TrackRepository trackRepository;
    private final TrackMapper trackMapper;
    private final TrackValidator trackValidator;
    private final LicenceTemplateRepository tplRepo;

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

        // 1) определяем Sort
        Sort sort = determineSort(filter);

        // 2) создаём новую PageRequest
        Pageable pageReq = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                sort);

        // 3) выполняем запрос
        return trackRepository.findAll(spec, pageReq)
                .map(trackMapper::toResponseDto);
    }

    private Sort determineSort(TrackFilterDto filter) {
        String tab  = Optional.ofNullable(filter.getTab()).orElse("").toLowerCase();
        String sort = Optional.ofNullable(filter.getSort()).orElse("").trim();

        // табы имеют приоритет над ручной сортировкой
        switch (tab) {
            case "trending":
                return Sort.by(Sort.Direction.DESC, "lastRatingDelta");
            case "top":
                return Sort.by(Sort.Direction.DESC, "rating");
            case "new":
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        // если юзер передал sort (например "-rating" или "createdAt")
        if (!sort.isEmpty()) {
            Sort.Direction dir = sort.startsWith("-")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            String property = sort.replaceFirst("^-", "");
            return Sort.by(dir, property);
        }

        // fallback
        return Sort.by(Sort.Direction.DESC, "createdAt");
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
        long end = range.getRangeEnd(contentLength);
        long len = Math.min(CHUNK_SIZE, end - start + 1);
        return new ResourceRegion(resource, start, len);
    }


    //=====================================
    //    Producer API
    //=====================================

    @Override
    @Transactional
    public TrackDto createTrack(TrackRequestDto dto) throws IOException {
        Producer lead = authenticationService.getRequestingProducerFromSecurityContext();

        trackValidator.validateTrackCreationRequestWithSingleOwner(dto, lead);

        return createWithFiles(dto, lead, true);
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

    @Transactional
    public void incrementPlays(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found"));
        track.setPlays(track.getPlays() + 1);
        trackRepository.save(track);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackDto> getTopTracks(int limit) {
        // sestupné řazení podle ratingu
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rating"));
        return trackRepository.findAll(pageable)
                .stream()
                .map(trackMapper::toResponseDto)
                .toList();
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
                .keyType(KeyType.valueOf(dto.getKey()))
                .producer(lead)
                .urlNonExclusive(urlMp3)
                .urlPremium(urlWav)
                .urlExclusive(urlZip)
                .createdAt(LocalDateTime.now())
                .rating(1000.00)
                .build();

        Track saved = trackRepository.save(track);
        createDefaultLicenceTemplates(saved, dto.getPrice());
        return trackMapper.toResponseDto(saved);
    }

    private String storeFile(MultipartFile file, String ext) throws IOException {
        String filename = UUID.randomUUID() + "." + ext;
        Path target = tracksUploadPath.resolve(filename);
        file.transferTo(target.toFile());
        return "/uploads/tracks/" + filename;
    }

    private void createDefaultLicenceTemplates(Track track, int basePrice) {
        tplRepo.save(LicenceTemplate.builder()
                .track(track)
                .licenceType(LicenceType.NON_EXCLUSIVE)
                .price(BigDecimal.valueOf(basePrice))
                .validityPeriodDays(30)
                .build());

        tplRepo.save(LicenceTemplate.builder()
                .track(track)
                .licenceType(LicenceType.PREMIUM)
                .price(BigDecimal.valueOf(basePrice * 2))
                .validityPeriodDays(90)
                .build());

        tplRepo.save(LicenceTemplate.builder()
                .track(track)
                .licenceType(LicenceType.EXCLUSIVE)
                .price(BigDecimal.valueOf(basePrice * 10))
                .validityPeriodDays(null)
                .build());
    }
}
