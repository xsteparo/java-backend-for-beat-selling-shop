package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.TrackLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {
    boolean existsByUserIdAndTrackId(Long userId, Long trackId);
    List<TrackLike> findAllByUserId(Long userId);
    long countByTrackId(Long trackId);
    void deleteByUserIdAndTrackId(Long userId, Long trackId);
}
