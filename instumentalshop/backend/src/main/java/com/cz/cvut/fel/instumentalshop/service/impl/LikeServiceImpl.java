package com.cz.cvut.fel.instumentalshop.service.impl;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.TrackLike;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.repository.TrackLikeRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final TrackLikeRepository likeRepo;
    private final TrackRepository trackRepo;  // pro update počtu lajků v entitě Track

    @Override
    @Transactional
    public void likeTrack(Long userId, Long trackId) {
        if (likeRepo.existsByUserIdAndTrackId(userId, trackId)) return;
        var like = new TrackLike();
        User user = new User();
        user.setId(userId);
        like.setUser(user);
        Track track = new Track();
        track.setId(trackId);
        like.setTrack(track);
        likeRepo.save(like);

        // synchronní update v Track
        Track t = trackRepo.findById(trackId).orElseThrow();
        t.setLikes(t.getLikes() + 1);
        trackRepo.save(t);
    }

    @Override
    @Transactional
    public void unlikeTrack(Long userId, Long trackId) {
        if (!likeRepo.existsByUserIdAndTrackId(userId, trackId)) return;
        likeRepo.deleteByUserIdAndTrackId(userId, trackId);

        Track t = trackRepo.findById(trackId).orElseThrow();
        t.setLikes(Math.max(0, t.getLikes() - 1));
        trackRepo.save(t);
    }

    @Override
    public boolean isLikedByUser(Long userId, Long trackId) {
        return likeRepo.existsByUserIdAndTrackId(userId, trackId);
    }

    @Override
    public long countLikes(Long trackId) {
        return likeRepo.countByTrackId(trackId);
    }
}

