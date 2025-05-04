package com.cz.cvut.fel.instumentalshop.service.impl.licence.strategy;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateCreationRequestDto;

import java.util.List;

public class ExclusiveLicenceCreationStrategy implements LicenceCreationStrategy{

    @Override
    public LicenceTemplate createTemplate(TemplateCreationRequestDto dto, Track track) {
        List<Platform> platformList = List.of(
                Platform.SPOTIFY,
                Platform.SOUNDCLOUD,
                Platform.APPLE_MUSIC,
                Platform.YOUTUBE,
                Platform.TIKTOK);


        return LicenceTemplate.builder()
                .track(track)
                .validityPeriodDays(999999)
                .licenceType(LicenceType.EXCLUSIVE)
                .availablePlatforms(platformList)
                .price(dto.getPrice())
                .build();
    }
}
