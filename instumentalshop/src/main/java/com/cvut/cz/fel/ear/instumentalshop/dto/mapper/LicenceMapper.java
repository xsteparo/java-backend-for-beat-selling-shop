package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;

import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceReport;
import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceTemplate;
import com.cvut.cz.fel.ear.instumentalshop.domain.PurchasedLicence;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.LicenceReportDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.PurchaseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.TemplateResponseDto;
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
