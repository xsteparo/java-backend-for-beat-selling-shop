package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tracks")
@CrossOrigin(origins = "http://localhost:5173")
public class TrackController {

    @Value("${app.upload.tracks-path}")
    private String uploadDir;

    private final TrackService trackService;

    @GetMapping("/{id}/stream")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResourceRegion> stream(
            @PathVariable Long id,
            @RequestHeader HttpHeaders headers) throws IOException {

        Resource audio = trackService.loadAsResource(id);      // <-- здесь
        long length = audio.contentLength();

        ResourceRegion region = buildRegion(audio, headers.getRange(), length);

        return ResponseEntity
                .status(region.getPosition() == 0 ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(audio)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }

    private ResourceRegion buildRegion(Resource res, List<HttpRange> ranges, long len) throws IOException {
        long chunk = 1024 * 1024;                             // 1 МБ
        if (ranges == null || ranges.isEmpty()) {
            return new ResourceRegion(res, 0, Math.min(chunk, len));
        }
        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(len);
        long end   = range.getRangeEnd(len);
        long rangeLen = Math.min(chunk, end - start + 1);
        return new ResourceRegion(res, start, rangeLen);
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<TrackDto>> listTracks(
            @RequestParam(defaultValue = "new") String tab,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String genre,
            @RequestParam(defaultValue = "") String tempoRange,
            @RequestParam(defaultValue = "") String key,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TrackDto> result = trackService.listTracks(
                tab, search, genre, tempoRange, key, sort, page, size
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping(
            path = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<TrackDto> createTrack(
            @Valid @ModelAttribute TrackRequestDto dto
    ) {
        TrackDto responseDto = trackService.createTrack(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{trackId}/confirm-agreement")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<ProducerTrackInfoDto>> confirmProducerAgreement(
            @PathVariable Long trackId) {

        List<ProducerTrackInfoDto> responseDtos = trackService.confirmProducerAgreement(trackId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/track-approvals-list")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<ProducerTrackInfoDto>> getAllTrackApprovals() {
        List<ProducerTrackInfoDto> responseDto = trackService.getTrackApprovalsList();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{trackId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TrackDto> getTrackById(@PathVariable Long trackId) {
        TrackDto track = trackService.getTrackById(trackId);
        return new ResponseEntity<>(track, HttpStatus.OK);
    }

//    @GetMapping("")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<List<TrackDto>> getAllTracks() {
//        List<TrackDto> tracks = trackService.getAllTracks();
//        return new ResponseEntity<>(tracks, HttpStatus.OK);
//    }

    @GetMapping("/by-producer/{producerId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TrackDto>> getAllTracksByProducer(@PathVariable Long producerId) {
        List<TrackDto> tracks = trackService.getAllTracksByProducer(producerId);
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @GetMapping("/bought-by-customer/{customerId}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<TrackDto>> getCustomerBoughtTracksForProducer(@PathVariable Long customerId) {
        List<TrackDto> tracks = trackService.getCustomerBoughtTracksForProducer(customerId);
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @DeleteMapping("/{trackId}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long trackId) {
        trackService.deleteTrack(trackId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{trackId}")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<TrackDto> updateTrack(@PathVariable Long trackId, @Valid @RequestBody TrackRequestDto requestDto) {
        TrackDto responseDto = trackService.updateTrack(trackId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
