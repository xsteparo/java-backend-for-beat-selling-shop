package com.cvut.cz.fel.ear.instumentalshop.service.impl.licence.strategy;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceTemplate;
import com.cvut.cz.fel.ear.instumentalshop.domain.Track;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateCreationRequestDto;

public interface LicenceCreationStrategy {

    LicenceTemplate createTemplate(TemplateCreationRequestDto dto, Track track);

}
