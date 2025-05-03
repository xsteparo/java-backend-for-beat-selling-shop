package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.ProducerIncomeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProducerTrackInfoRepository extends JpaRepository<ProducerTrackInfo, Long> {

    List<ProducerTrackInfo> findByTrackId(Long id);

    Optional<ProducerTrackInfo> findByTrackIdAndProducerId(Long trackId, Long producerId);

    @Query(name = "ProducerTrackInfo.findProducerTrackInfoByTrackIdAndProducerIdAndAgreedStatus")
    Optional<ProducerTrackInfo> findProducerTrackInfoByTrackIdAndProducerIdAndAgreedStatus(
            Long trackId,
            Long producerId,
            Boolean agreedStatus);

    @Query(name = "ProducerTrackInfo.findByProducerIdAndAgreedForSelling")
    List<ProducerTrackInfo> findByProducerIdAndAgreedForSelling(
            Long producerId,
            Boolean agreedForSelling);

    @Query(name = "ProducerTrackInfo.findProducerIncomeByTracks")
    List<ProducerIncomeDto> findProducerIncomeByTracks(Long producerId);

    boolean existsProducerTrackInfoByProducerIdAndTrackId(Long producerId, Long trackId);
}
