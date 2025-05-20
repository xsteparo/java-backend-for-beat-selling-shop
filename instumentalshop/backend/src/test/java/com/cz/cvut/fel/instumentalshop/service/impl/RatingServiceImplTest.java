package com.cz.cvut.fel.instumentalshop.service.impl;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private Track track1;
    private Track track2;

    @BeforeEach
    void setUp() {
        track1 = mock(Track.class);
        when(track1.getNormalizedScore()).thenReturn(1.0);
        when(track1.getRating()).thenReturn(BigDecimal.ZERO);

        track2 = mock(Track.class);
        when(track2.getNormalizedScore()).thenReturn(-0.5);
        when(track2.getRating()).thenReturn(BigDecimal.valueOf(900.0));

        when(trackRepository.findAll()).thenReturn(List.of(track1, track2));
    }

    @Test
    @DisplayName("Sets base rating for unrated tracks")
    void updateAllRatings_baseRatingApplied() {
        ratingService.updateAllRatings();

        verify(track1).setRating(argThat(val -> Math.abs(val.doubleValue() - 1000.0) < 0.01));
    }

    @Test
    @DisplayName("Enforces minimum rating of 800")
    void updateAllRatings_enforceMinimumRating() {
        // Override rating to make sure it's below 800 after delta
        when(track2.getNormalizedScore()).thenReturn(-10.0);
        when(track2.getRating()).thenReturn(BigDecimal.valueOf(810.0));

        ratingService.updateAllRatings();

        verify(track2).setRating(argThat(val -> Math.abs(val.doubleValue() - 800.0) < 0.01));
    }

    @Test
    @DisplayName("Updates rating with positive delta")
    void updateAllRatings_positiveDelta() {
        when(track2.getNormalizedScore()).thenReturn(0.8);

        ratingService.updateAllRatings();
    }

    @Test
    @DisplayName("Calls saveAll on updated tracks")
    void updateAllRatings_callsSaveAll() {
        ratingService.updateAllRatings();

        verify(trackRepository).saveAll(List.of(track1, track2));
    }
}