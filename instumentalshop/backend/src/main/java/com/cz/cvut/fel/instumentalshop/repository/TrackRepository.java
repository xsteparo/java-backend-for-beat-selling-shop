package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
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

    Page<Track> findAll(Specification<Track> spec, Pageable pageable);

    @Query("SELECT AVG(t.rating) FROM Track t")
    Double findAverageRating();

    @Query("select t.urlNonExclusive from Track t where t.id = :id")
    String findFilePathById(@Param("id") Long id);

    @Query("select t.urlNonExclusive from Track t where t.id = :id")
    String findNonExclusivePathById(@Param("id") Long id);

    @Query("select t.urlPremium from Track t where t.id = :id")
    String findPremiumPathById(@Param("id") Long id);

    @Query("select t.urlExclusive from Track t where t.id = :id")
    String findExclusivePathById(@Param("id") Long id);

    @Query("""
    SELECT t FROM Track t
    WHERE NOT EXISTS (
        SELECT 1 FROM PurchasedLicence pl
        WHERE pl.track = t AND pl.licenceTemplate.licenceType = com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType.EXCLUSIVE
    )
""")
    Page<Track> findAllAvailable(Pageable pageable);

}
