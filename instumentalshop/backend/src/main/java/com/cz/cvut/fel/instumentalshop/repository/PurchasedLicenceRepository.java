package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import org.springframework.data.jpa.repository.Query;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasedLicenceRepository extends JpaRepository<PurchasedLicence,Long> {

    boolean existsByCustomerIdAndTrackIdAndLicenceTemplate_LicenceType(Long customerId, Long trackId, LicenceType licenceType);

    List<PurchasedLicence> findPurchasedLicenceByTrackId(Long trackId);

    boolean existsByTrackId(Long id);

    @Query("SELECT pl FROM PurchasedLicence pl JOIN pl.producer p WHERE p.id = :producerId")
    List<PurchasedLicence> findForProducerByProducerId(@Param("producerId") Long producerId);

    List<PurchasedLicence> findByCustomerId(Long customerId);

    @Query("""
        SELECT new com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto(
            c.id,
            u.username,
            COUNT(pl),
            MAX(pl.purchaseDate)
        )
        FROM PurchasedLicence pl
        JOIN pl.customer c
        JOIN User u ON c.id = u.id
        WHERE pl.producer.id = :producerId
        GROUP BY c.id, u.username
        ORDER BY MAX(pl.purchaseDate) DESC
    """)
    List<ProducerPurchaseStatisticDto> findCustomerStatsByProducerId(@Param("producerId") Long producerId);

}
