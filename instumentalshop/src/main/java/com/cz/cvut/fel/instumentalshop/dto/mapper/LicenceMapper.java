package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.LicenceReport;
import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.LicenceReportDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.TemplateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LicenceMapper {

    @Mapping(target = "licenceTemplateId", source = "id")
    @Mapping(target = "trackId", source = "track.id")
    TemplateResponseDto toResponseDto(LicenceTemplate source);

    List<TemplateResponseDto> toResponseDto(List<LicenceTemplate> source);

    PurchaseDto toResponseDto(PurchasedLicence purchasedLicence);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "purchaseId", source = "purchasedLicence.id")
    LicenceReportDto toResponseDto(LicenceReport licenceTemplate);

}
