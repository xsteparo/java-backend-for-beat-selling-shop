package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    private TrackRepository trackRepository;
    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        trackRepository = mock(TrackRepository.class);
        ratingService = new RatingServiceImpl(trackRepository);
    }

    @Test
    void testUpdateAllRatings() {
        // Arrange
        Track trackA = Track.builder()
                .id(1L)
                .name("Track A")
                .genre(GenreType.DNB)
                .bpm(174)
                .rating(1000)
                .plays(100)
                .likes(20)  // 0.2 score
                .createdAt(LocalDateTime.now())
                .build();

        Track trackB = Track.builder()
                .id(2L)
                .name("Track B")
                .genre(GenreType.POP)
                .bpm(120)
                .rating(1000)
                .plays(200)
                .likes(10)  // 0.05 score
                .createdAt(LocalDateTime.now())
                .build();

        Track trackC = Track.builder()
                .id(3L)
                .name("Track C")
                .genre(GenreType.HIPHOP)
                .bpm(90)
                .rating(1000)
                .plays(50)
                .likes(25)  // 0.5 score
                .createdAt(LocalDateTime.now())
                .build();

        List<Track> tracks = Arrays.asList(trackA, trackB, trackC);

        when(trackRepository.findAll()).thenReturn(tracks);

        // Act
        ratingService.updateAllRatings();

        // Capture updated tracks
        ArgumentCaptor<List<Track>> captor = ArgumentCaptor.forClass(List.class);
        verify(trackRepository, times(1)).saveAll(captor.capture());

        List<Track> updatedTracks = captor.getValue();

        // Assert
        assertEquals(3, updatedTracks.size());

        // Check updated ratings
        for (Track t : updatedTracks) {
            assertTrue(t.getRating() >= 800, "Rating should not fall below MIN_RATING (800)");
        }

        // Example: Track with best score should increase
        Track updatedC = updatedTracks.stream()
                .filter(t -> t.getId().equals(3L))
                .findFirst()
                .orElseThrow();

        assertTrue(updatedC.getRating() > 1000, "Best performing track should gain rating");

        // Example: Track with worst score should decrease
        Track updatedB = updatedTracks.stream()
                .filter(t -> t.getId().equals(2L))
                .findFirst()
                .orElseThrow();

        assertTrue(updatedB.getRating() < 1000, "Worst performing track should lose rating");
    }
}
