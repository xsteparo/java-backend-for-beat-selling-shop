package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/top-tracks")
    public List<Track> getTopTracks(@RequestParam(defaultValue = "10") int count) {
        return recommendationService.getTopTracks(count);
    }

    @GetMapping("/new-tracks")
    public List<Track> getNewTracks(@RequestParam(defaultValue = "10") int count) {
        return recommendationService.getNewTracks(count);
    }

    @GetMapping("/trending-tracks")
    public List<Track> getTrendingTracks(@RequestParam(defaultValue = "10") int count) {
        return recommendationService.getTrendingTracks(count);
    }
}
