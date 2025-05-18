package com.cz.cvut.fel.instumentalshop.service;


import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.TrackLike;
import com.cz.cvut.fel.instumentalshop.repository.TrackLikeRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LikeServiceImplTest {

    @Mock
    private TrackLikeRepository likeRepo;
    @Mock
    private TrackRepository trackRepo;

    @InjectMocks
    private LikeServiceImpl likeService;

    private final Long userId = 100L;
    private final Long trackId = 200L;
    private Track track;

    @BeforeEach
    void setUp() {
        track = new Track();
        track.setId(trackId);
        track.setLikes(0);
        when(trackRepo.findById(trackId)).thenReturn(Optional.of(track));
    }

    @Test
    void testLikeTrack_FirstTime() {
        // user has not liked yet
        when(likeRepo.existsByUserIdAndTrackId(userId, trackId)).thenReturn(false);
        when(likeRepo.save(any(TrackLike.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        likeService.likeTrack(userId, trackId);

        // Assert repo.save called and trackRepo.save called with incremented likes
        assertEquals(1, track.getLikes());
        verify(likeRepo).existsByUserIdAndTrackId(userId, trackId);
        verify(likeRepo).save(any(TrackLike.class));
        verify(trackRepo).save(track);
    }

    @Test
    void testLikeTrack_AlreadyLiked() {
        when(likeRepo.existsByUserIdAndTrackId(userId, trackId)).thenReturn(true);

        // Act
        likeService.likeTrack(userId, trackId);

        // Assert nothing changes
        assertEquals(0, track.getLikes());
        verify(likeRepo).existsByUserIdAndTrackId(userId, trackId);
        verify(likeRepo, never()).save(any());
        verify(trackRepo, never()).save(any());
    }

    @Test
    void testUnlikeTrack_NotLiked() {
        when(likeRepo.existsByUserIdAndTrackId(userId, trackId)).thenReturn(false);
        track.setLikes(5);

        // Act
        likeService.unlikeTrack(userId, trackId);

        // Assert
        assertEquals(5, track.getLikes());
        verify(likeRepo).existsByUserIdAndTrackId(userId, trackId);
        verify(likeRepo, never()).deleteByUserIdAndTrackId(anyLong(), anyLong());
        verify(trackRepo, never()).save(any());
    }

    @Test
    void testUnlikeTrack_OriginallyLiked() {
        when(likeRepo.existsByUserIdAndTrackId(userId, trackId)).thenReturn(true);
        track.setLikes(2);

        // Act
        likeService.unlikeTrack(userId, trackId);

        // Assert likes decremented
        assertEquals(1, track.getLikes());
        verify(likeRepo).deleteByUserIdAndTrackId(userId, trackId);
        verify(trackRepo).save(track);
    }

    @Test
    void testCountLikes() {
        when(likeRepo.countByTrackId(trackId)).thenReturn(42L);

        long count = likeService.countLikes(trackId);

        assertEquals(42, count);
        verify(likeRepo).countByTrackId(trackId);
    }

    @Test
    void testIsLikedByUser() {
        when(likeRepo.existsByUserIdAndTrackId(userId, trackId)).thenReturn(true);

        boolean liked = likeService.isLikedByUser(userId, trackId);

        assertTrue(liked);
        verify(likeRepo).existsByUserIdAndTrackId(userId, trackId);
    }
}

