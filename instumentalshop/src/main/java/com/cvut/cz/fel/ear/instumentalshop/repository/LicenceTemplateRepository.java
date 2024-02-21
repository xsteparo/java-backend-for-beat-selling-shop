package com.cvut.cz.fel.ear.instumentalshop.repository;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceTemplate;
import com.cvut.cz.fel.ear.instumentalshop.domain.Track;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LicenceTemplateRepository extends JpaRepository<LicenceTemplate,Long> {

    boolean existsByTrackIdAndLicenceType(Long trackId, LicenceType licenceType);

    Optional<LicenceTemplate>  findByTrackAndLicenceType (Track track, LicenceType licenceType);

    Optional<LicenceTemplate> findByTrackIdAndLicenceType (Long track, LicenceType licenceType);

    List<LicenceTemplate> findByTrackId(Long trackId);
}
