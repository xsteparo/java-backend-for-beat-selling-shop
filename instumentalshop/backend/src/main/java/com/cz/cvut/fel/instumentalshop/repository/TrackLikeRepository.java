package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.TrackLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {
    boolean existsByUserIdAndTrackId(Long userId, Long trackId);
    long countByTrackId(Long trackId);
    void deleteByUserIdAndTrackId(Long userId, Long trackId);
}
