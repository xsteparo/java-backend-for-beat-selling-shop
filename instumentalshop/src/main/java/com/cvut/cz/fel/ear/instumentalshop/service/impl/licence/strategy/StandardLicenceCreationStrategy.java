package com.cvut.cz.fel.ear.instumentalshop.service.impl.licence.strategy;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceTemplate;
import com.cvut.cz.fel.ear.instumentalshop.domain.Track;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateCreationRequestDto;


public class StandardLicenceCreationStrategy implements LicenceCreationStrategy {

    @Override
    public LicenceTemplate createTemplate(TemplateCreationRequestDto dto, Track track) {
        return LicenceTemplate.builder()
                .track(track)
                .validityPeriodDays(dto.getValidityPeriodDays())
                .licenceType(dto.getLicenceType())
                .availablePlatforms(dto.getPlatforms())
                .price(dto.getPrice())
                .build();
    }
}
