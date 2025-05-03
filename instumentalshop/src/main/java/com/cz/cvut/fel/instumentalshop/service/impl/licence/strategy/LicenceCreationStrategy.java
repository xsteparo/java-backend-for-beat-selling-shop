package com.cz.cvut.fel.instumentalshop.service.impl.licence.strategy;

import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateCreationRequestDto;

public interface LicenceCreationStrategy {

    LicenceTemplate createTemplate(TemplateCreationRequestDto dto, Track track);

}
