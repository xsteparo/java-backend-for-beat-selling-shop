package com.cvut.cz.fel.ear.instumentalshop.controller;

import com.cvut.cz.fel.ear.instumentalshop.dto.track.in.TrackRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.out.TrackDto;
import com.cvut.cz.fel.ear.instumentalshop.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tracks/")
public class TrackController {

    private final TrackService trackService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<TrackDto> createTrack(@Valid @RequestBody TrackRequestDto requestDto) {
        TrackDto responseDto = trackService.createTrack(requestDto);
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

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TrackDto>> getAllTracks() {
        List<TrackDto> tracks = trackService.getAllTracks();
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

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
