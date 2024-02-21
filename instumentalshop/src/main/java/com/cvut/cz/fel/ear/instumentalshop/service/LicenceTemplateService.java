package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.TemplateResponseDto;

import java.util.List;

public interface LicenceTemplateService {

    TemplateResponseDto createTemplate(Long trackId, TemplateCreationRequestDto requestDto);

    TemplateResponseDto getTemplateByTypeAndTrackId(Long trackId, LicenceType licenceType);

    List<TemplateResponseDto> getAllTemplatesByTrack(Long trackId);

    TemplateResponseDto updateTemplate(Long trackId, LicenceType licenceType, TemplateUpdateRequestDto requestDto);

    void deleteTemplate(Long trackId, LicenceType licenceType);

}
