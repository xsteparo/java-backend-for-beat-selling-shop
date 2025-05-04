package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.TemplateResponseDto;

import java.util.List;

public interface LicenceTemplateService {

    TemplateResponseDto createTemplate(Long trackId, TemplateCreationRequestDto requestDto);

    TemplateResponseDto getTemplateByTypeAndTrackId(Long trackId, LicenceType licenceType);

    List<TemplateResponseDto> getAllTemplatesByTrack(Long trackId);

    TemplateResponseDto updateTemplate(Long trackId, LicenceType licenceType, TemplateUpdateRequestDto requestDto);

    void deleteTemplate(Long trackId, LicenceType licenceType);

}
