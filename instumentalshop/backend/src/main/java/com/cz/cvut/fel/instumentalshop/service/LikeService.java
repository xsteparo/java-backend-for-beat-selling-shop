package com.cz.cvut.fel.instumentalshop.service;

public interface LikeService {
    void likeTrack(Long userId, Long trackId);
    void unlikeTrack(Long userId, Long trackId);
    boolean isLikedByUser(Long userId, Long trackId);
    long countLikes(Long trackId);
}
