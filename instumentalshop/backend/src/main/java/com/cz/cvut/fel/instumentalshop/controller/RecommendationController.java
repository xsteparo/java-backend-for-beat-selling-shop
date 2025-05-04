package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

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
