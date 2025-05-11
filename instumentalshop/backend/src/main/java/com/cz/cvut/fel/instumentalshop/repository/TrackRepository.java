package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long>, JpaSpecificationExecutor<Track> {

    Optional<Track> findTrackById(Long id);

    List<Track> findTracksByProducerId(Long producerId);

    @Query(name = "Track.findCustomerBoughtTracksForProducer")
    List<Track> findCustomerBoughtTracksForProducer(@Param("customerId") Long customerId, @Param("producerId") Long producerId);

}
