package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private static final int K = 32;
    private static final int BASE_RATING = 1000;
    private static final int MIN_RATING = 800;

    private final TrackRepository trackRepository;


    public void updateAllRatings() {
        List<Track> tracks = trackRepository.findAll();

        double totalScore = 0;
        for (Track track : tracks) {
            totalScore += track.getNormalizedScore();
        }
        double averageScore = tracks.isEmpty() ? 0 : totalScore / tracks.size();

        for (Track track : tracks) {
            double normalizedScore = track.getNormalizedScore();
            double delta = normalizedScore - averageScore;

            double newRating = (track.getRating() == 0) ? BASE_RATING : track.getRating() + K * delta;

            if (newRating < MIN_RATING) {
                newRating = MIN_RATING;
            }

            track.setRating(newRating);
        }

        trackRepository.saveAll(tracks);
    }
}
