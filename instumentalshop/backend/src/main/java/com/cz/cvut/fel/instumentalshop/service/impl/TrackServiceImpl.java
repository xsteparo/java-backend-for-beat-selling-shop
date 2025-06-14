package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType.*;

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
        // Vytvoření specifikace na základě filtračního DTO
        Specification<Track> spec = TrackSpecificationBuilder.fromFilter(filter);

        if ("trending".equalsIgnoreCase(filter.getTab())) {
            Double avgRating = trackRepository.findAverageRating();
            if (avgRating != null) {
                spec = spec.and((root, cq, cb) ->
                        cb.greaterThan(root.get("rating"), avgRating)
                );
            }
        }
        // 1) Určení řazení podle zadaného filtru
        Sort sort = determineSort(filter);

        // 2) Vytvoření nového PageRequest s aktuální stránkou, velikostí stránky a řazením
        Pageable pageReq = PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort);

        // 3) Provedení dotazu s danou specifikací a stránkováním, a mapování entit na DTO
        return trackRepository.findAll(spec, pageReq)
                .map(trackMapper::toResponseDto);
    }

    private Sort determineSort(TrackFilterDto filter) {
        String tab       = Optional.ofNullable(filter.getTab())
                .orElse("")
                .trim()
                .toLowerCase();
        String sortParam = Optional.ofNullable(filter.getSort())
                .orElse("")
                .trim();

        List<Order> orders = new ArrayList<>();

        switch (tab) {
            case "trending":
                orders.add(Order.desc("lastRatingDelta"));
                break;
            case "top":
                orders.add(Order.desc("rating"));
                break;
            case "new":
                orders.add(Order.desc("createdAt"));
                break;
            default:
                break;
        }

        if (!sortParam.isEmpty()) {
            Sort.Direction dir  = sortParam.startsWith("-")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            String        prop = sortParam.replaceFirst("^-", "");
            orders.add(new Order(dir, prop));
        }

        if (orders.isEmpty()) {
            orders.add(Order.desc("createdAt"));
        }

        return Sort.by(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackDto findById(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));
        return trackMapper.toResponseDto(track);
    }

    @Override
    public Resource loadAsResource(Long trackId, LicenceType licenceType) throws IOException {

        String relPath;
        switch (licenceType) {
            case NON_EXCLUSIVE:
                relPath = trackRepository.findNonExclusivePathById(trackId);
                break;
            case PREMIUM:
                relPath = trackRepository.findPremiumPathById(trackId);
                break;
            case EXCLUSIVE:
                relPath = trackRepository.findExclusivePathById(trackId);
                break;
            default:
                throw new IllegalArgumentException("Unknown licence type: " + licenceType);
        }

        if (relPath == null) {
            throw new EntityNotFoundException("File URL not found for track " + trackId + " and licence " + licenceType);
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

    @Override
    @Transactional
    public TrackDto updateTrack(Long trackId, TrackRequestDto dto) throws IOException {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found"));

        if (!track.getProducer().getId().equals(producer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        track.setName(dto.getName());
        track.setGenre(dto.getGenreType());
        track.setBpm(dto.getBpm());
        track.setKeyType(dto.getKey() != null ? KeyType.valueOf(dto.getKey()) : null);
        track.setCreatedAt(LocalDateTime.now());

        if (dto.getNonExclusiveFile() != null && !dto.getNonExclusiveFile().isEmpty()) {
            String urlMp3 = storeFile(dto.getNonExclusiveFile(), "mp3");
            track.setUrlNonExclusive(urlMp3);
        }

        if (dto.getPremiumFile() != null && !dto.getPremiumFile().isEmpty()) {
            String urlWav = storeFile(dto.getPremiumFile(), "wav");
            track.setUrlPremium(urlWav);
        }

        if (dto.getExclusiveFile() != null && !dto.getExclusiveFile().isEmpty()) {
            String urlZip = storeFile(dto.getExclusiveFile(), "zip");
            track.setUrlExclusive(urlZip);
        }

        for (LicenceTemplate template : track.getLicenceTemplates()) {
            LicenceType type = template.getLicenceType();
            switch (type) {
                case NON_EXCLUSIVE:
                    if (dto.getPriceNonExclusive() != null) {
                        template.setPrice(BigDecimal.valueOf(dto.getPriceNonExclusive()));
                    }
                    break;

                case PREMIUM:
                    if (dto.getPricePremium() != null) {
                        template.setPrice(BigDecimal.valueOf(dto.getPricePremium()));
                    }
                    break;

                case EXCLUSIVE:
                    if (dto.getPriceExclusive() != null) {
                        template.setPrice(BigDecimal.valueOf(dto.getPriceExclusive()));
                    }
                    break;

                default:
                    break;
            }
        }

        trackRepository.save(track);

        return trackMapper.toResponseDto(track);
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
                .rating(BigDecimal.valueOf(1000.00))
                .build();

        Track saved = trackRepository.save(track);

        // Вместо одного basePrice передаём три
        createDefaultLicenceTemplates(
                saved,
                dto.getPriceNonExclusive(),
                dto.getPricePremium(),
                dto.getPriceExclusive()
        );

        return trackMapper.toResponseDto(saved);
    }

    private String storeFile(MultipartFile file, String ext) throws IOException {
        String filename = UUID.randomUUID() + "." + ext;
        Path target = tracksUploadPath.resolve(filename);
        file.transferTo(target.toFile());
        return "/uploads/tracks/" + filename;
    }

    private void createDefaultLicenceTemplates(
            Track track,
            Integer priceNonExclusive,
            Integer pricePremium,
            Integer priceExclusive
    ) {
        tplRepo.save(LicenceTemplate.builder()
                .track(track)
                .licenceType(LicenceType.NON_EXCLUSIVE)
                .price(BigDecimal.valueOf(priceNonExclusive))
                .validityPeriodDays(30)
                .build());

        if (pricePremium != null) {
            tplRepo.save(LicenceTemplate.builder()
                    .track(track)
                    .licenceType(LicenceType.PREMIUM)
                    .price(BigDecimal.valueOf(pricePremium))
                    .validityPeriodDays(90)
                    .build());
        }

        if (priceExclusive != null) {
            tplRepo.save(LicenceTemplate.builder()
                    .track(track)
                    .licenceType(LicenceType.EXCLUSIVE)
                    .price(BigDecimal.valueOf(priceExclusive))
                    .validityPeriodDays(null)
                    .build());
        }
    }
}
