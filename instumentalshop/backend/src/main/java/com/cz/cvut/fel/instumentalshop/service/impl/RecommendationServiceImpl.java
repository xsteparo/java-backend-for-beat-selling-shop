package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final TrackRepository trackRepository;

    public List<Track> getTopTracks(int count) {
        return trackRepository.findAll().stream()
                .sorted(Comparator.comparing(Track::getRating).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Track> getNewTracks(int count) {
        return trackRepository.findAll().stream()
                .sorted(Comparator.comparing(Track::getCreatedAt).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Track> getTrendingTracks(int count) {
        double averageRating = trackRepository.findAll().stream()
                .mapToDouble(track -> track.getRating().doubleValue())
                .average()
                .orElse(0);

        return trackRepository.findAll().stream()
                .filter(t -> t.getRating().compareTo(BigDecimal.valueOf(averageRating)) > 0)
                .sorted(Comparator.comparingDouble(Track::getLastRatingDelta).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
