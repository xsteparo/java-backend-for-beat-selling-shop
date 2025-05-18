package com.cz.cvut.fel.instumentalshop.service;

import java.util.List;

public interface LikeService {
    List<Long> findLikedTrackIdsByUser(Long userId);
    void likeTrack(Long userId, Long trackId);
    void unlikeTrack(Long userId, Long trackId);
    boolean isLikedByUser(Long userId, Long trackId);
    long countLikes(Long trackId);
}
