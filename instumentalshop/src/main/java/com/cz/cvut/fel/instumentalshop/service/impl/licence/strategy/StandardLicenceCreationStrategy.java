package com.cz.cvut.fel.instumentalshop.service.impl.licence.strategy;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateCreationRequestDto;


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
