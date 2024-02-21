package com.cvut.cz.fel.ear.instumentalshop.repository;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import org.springframework.data.jpa.repository.Query;
import com.cvut.cz.fel.ear.instumentalshop.domain.PurchasedLicence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasedLicenceRepository extends JpaRepository<PurchasedLicence,Long> {

    boolean existsByCustomerIdAndTrackIdAndLicenceTemplate_LicenceType(Long customerId, Long trackId, LicenceType licenceType);

    List<PurchasedLicence> findPurchasedLicenceByTrackId(Long trackId);

    boolean existsByTrackId(Long id);

    @Query("SELECT pl FROM PurchasedLicence pl JOIN pl.producers p WHERE p.id = :producerId")
    List<PurchasedLicence> findForProducerByProducerId(@Param("producerId") Long producerId);

    List<PurchasedLicence> findByCustomerId(Long customerId);

}
