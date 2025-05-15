package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TrackController {

    private final TrackService trackService;

    /**
     * FR04, FR05, FR06, FR21
     */
    @GetMapping
    public ResponseEntity<Page<TrackDto>> list(
            TrackFilterDto filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TrackDto> page = trackService.findAll(filter, pageable);
        return ResponseEntity.ok(page);
    }

    /** FR04 */
    @GetMapping("/{id}")
    public ResponseEntity<TrackDto> getOne(@PathVariable Long id) {
        TrackDto dto = trackService.findById(id);
        return ResponseEntity.ok(dto);
    }

    /** FR18 */
    @GetMapping("/{id}/stream")
    public ResponseEntity<ResourceRegion> stream(
            @PathVariable Long id,
            @RequestHeader HttpHeaders headers
    ) throws IOException {
        Resource audio = trackService.loadAsResource(id);
        long length = audio.contentLength();

        ResourceRegion region = trackService.buildRegion(audio, headers.getRange(), length);
        MediaType contentType = MediaTypeFactory.getMediaType(audio)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .status(region.getPosition() == 0 ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT)
                .contentType(contentType)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }
}
