package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class RecommendationServiceTest {

    private TrackRepository trackRepository;
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        trackRepository = Mockito.mock(TrackRepository.class);
        recommendationService = new RecommendationServiceImpl(trackRepository);
    }

    @Test
    void testGetTopTracks() {
        List<Track> mockTracks = Arrays.asList(
                Track.builder()
                        .id(1L)
                        .name("Track A")
                        .genre(GenreType.DNB)
                        .bpm(174)
                        .rating(900)
                        .plays(100)
                        .likes(10)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .lastRatingDelta(5)
                        .build(),
                Track.builder()
                        .id(2L)
                        .name("Track B")
                        .genre(GenreType.ELECTRO)
                        .bpm(128)
                        .rating(1100)
                        .plays(200)
                        .likes(20)
                        .createdAt(LocalDateTime.now().minusDays(3))
                        .lastRatingDelta(10)
                        .build(),
                Track.builder()
                        .id(3L)
                        .name("Track C")
                        .genre(GenreType.HIPHOP)
                        .bpm(95)
                        .rating(1000)
                        .plays(150)
                        .likes(15)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .lastRatingDelta(7)
                        .build()
        );

        when(trackRepository.findAll()).thenReturn(mockTracks);

        List<Track> topTracks = recommendationService.getTopTracks(2);

        assertEquals(2, topTracks.size());
        assertEquals("Track B", topTracks.get(0).getName());
        assertEquals("Track C", topTracks.get(1).getName());
    }

    @Test
    void testGetNewTracks() {
        List<Track> mockTracks = Arrays.asList(
                Track.builder()
                        .id(1L)
                        .name("Old Track")
                        .genre(GenreType.ROCK)
                        .bpm(110)
                        .rating(1000)
                        .plays(100)
                        .likes(10)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .lastRatingDelta(2)
                        .build(),
                Track.builder()
                        .id(2L)
                        .name("New Track")
                        .genre(GenreType.JAZZ)
                        .bpm(120)
                        .rating(1000)
                        .plays(200)
                        .likes(20)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .lastRatingDelta(5)
                        .build(),
                Track.builder()
                        .id(3L)
                        .name("Newest Track")
                        .genre(GenreType.POP)
                        .bpm(130)
                        .rating(1000)
                        .plays(300)
                        .likes(30)
                        .createdAt(LocalDateTime.now())
                        .lastRatingDelta(8)
                        .build()
        );

        when(trackRepository.findAll()).thenReturn(mockTracks);

        List<Track> newTracks = recommendationService.getNewTracks(2);

        assertEquals(2, newTracks.size());
        assertEquals("Newest Track", newTracks.get(0).getName());
        assertEquals("New Track", newTracks.get(1).getName());
    }

    @Test
    void testGetTrendingTracks() {
        List<Track> mockTracks = Arrays.asList(
                Track.builder()
                        .id(1L)
                        .name("Low Track")
                        .genre(GenreType.DNB)
                        .bpm(174)
                        .rating(900)
                        .plays(100)
                        .likes(10)
                        .createdAt(LocalDateTime.now())
                        .lastRatingDelta(2)
                        .build(),
                Track.builder()
                        .id(2L)
                        .name("Mid Track")
                        .genre(GenreType.HIPHOP)
                        .bpm(95)
                        .rating(1000)
                        .plays(200)
                        .likes(20)
                        .createdAt(LocalDateTime.now())
                        .lastRatingDelta(5)
                        .build(),
                Track.builder()
                        .id(3L)
                        .name("High Track")
                        .genre(GenreType.ELECTRO)
                        .bpm(128)
                        .rating(1100)
                        .plays(300)
                        .likes(30)
                        .createdAt(LocalDateTime.now())
                        .lastRatingDelta(8)
                        .build()
        );

        when(trackRepository.findAll()).thenReturn(mockTracks);

        List<Track> trendingTracks = recommendationService.getTrendingTracks(2);

        assertTrue(trendingTracks.size() <= 2);
        assertTrue(trendingTracks.stream().allMatch(t -> t.getRating() > 1000 || t.getLastRatingDelta() > 0));
    }
}
