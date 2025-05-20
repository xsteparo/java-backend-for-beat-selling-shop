package com.cz.cvut.fel.instumentalshop.service.impl;


import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.TrackLike;
import com.cz.cvut.fel.instumentalshop.repository.TrackLikeRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LikeServiceImplTest {

    @Mock
    private TrackLikeRepository likeRepo;

    @Mock
    private TrackRepository trackRepo;

    @InjectMocks
    private LikeServiceImpl likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindLikedTrackIdsByUser() {
        Track track = new Track();
        track.setId(10L);
        TrackLike like = new TrackLike();
        like.setTrack(track);
        when(likeRepo.findAllByUserId(1L)).thenReturn(List.of(like));

        List<Long> result = likeService.findLikedTrackIdsByUser(1L);

        assertEquals(List.of(10L), result);
    }

    @Test
    void testLikeTrack_FirstTime() {
        when(likeRepo.existsByUserIdAndTrackId(1L, 2L)).thenReturn(false);

        Track track = new Track();
        track.setId(2L);
        track.setLikes(0);
        when(trackRepo.findById(2L)).thenReturn(Optional.of(track));

        likeService.likeTrack(1L, 2L);

        ArgumentCaptor<TrackLike> captor = ArgumentCaptor.forClass(TrackLike.class);
        verify(likeRepo).save(captor.capture());
        verify(trackRepo).save(track);

        assertEquals(1, track.getLikes());
        assertEquals(2L, captor.getValue().getTrack().getId());
        assertEquals(1L, captor.getValue().getUser().getId());
    }

    @Test
    void testLikeTrack_AlreadyLiked() {
        when(likeRepo.existsByUserIdAndTrackId(1L, 2L)).thenReturn(true);

        likeService.likeTrack(1L, 2L);

        verify(likeRepo, never()).save(any());
        verify(trackRepo, never()).save(any());
    }

    @Test
    void testUnlikeTrack() {
        when(likeRepo.existsByUserIdAndTrackId(1L, 2L)).thenReturn(true);

        Track track = new Track();
        track.setId(2L);
        track.setLikes(5);
        when(trackRepo.findById(2L)).thenReturn(Optional.of(track));

        likeService.unlikeTrack(1L, 2L);

        verify(likeRepo).deleteByUserIdAndTrackId(1L, 2L);
        verify(trackRepo).save(track);
        assertEquals(4, track.getLikes());
    }

    @Test
    void testUnlikeTrack_NotLiked() {
        when(likeRepo.existsByUserIdAndTrackId(1L, 2L)).thenReturn(false);

        likeService.unlikeTrack(1L, 2L);

        verify(likeRepo, never()).deleteByUserIdAndTrackId(anyLong(), anyLong());
        verify(trackRepo, never()).save(any());
    }

    @Test
    void testIsLikedByUser() {
        when(likeRepo.existsByUserIdAndTrackId(1L, 2L)).thenReturn(true);
        assertTrue(likeService.isLikedByUser(1L, 2L));
    }

    @Test
    void testCountLikes() {
        when(likeRepo.countByTrackId(2L)).thenReturn(123L);
        assertEquals(123L, likeService.countLikes(2L));
    }
}

