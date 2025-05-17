package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.producer.in.TopProducerRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.service.ProducerService;
import com.cz.cvut.fel.instumentalshop.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final TrackService trackService;
    private final ProducerService producerService;

    /**
     * Vrátí top skladeb podle hodnocení.
     *
     * @param limit počet skladeb, které se mají vrátit (volitelně, výchozí 10)
     * @return seznam DTO skladeb seřazených sestupně podle ratingu
     */
    @GetMapping("/tracks")
    public ResponseEntity<List<TrackDto>> getTopTracks(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        List<TrackDto> topTracks = trackService.getTopTracks(limit);
        return ResponseEntity.ok(topTracks);
    }

    /**
     * Vrátí top producentů podle průměrného hodnocení jejich skladeb.
     *
     * @param limit počet producentů, které se mají vrátit (volitelně, výchozí 10)
     * @return seznam DTO producentů seřazených sestupně podle ratingu
     */
    @GetMapping("/producers")
    public ResponseEntity<List<TopProducerRequestDto>> getTopProducers(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        List<TopProducerRequestDto> topProducers = producerService.getTopProducers(limit);
        return ResponseEntity.ok(topProducers);
    }
}
