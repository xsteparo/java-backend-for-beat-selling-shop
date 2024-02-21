package com.cvut.cz.fel.ear.instumentalshop.service.impl.licence;


import com.cvut.cz.fel.ear.instumentalshop.domain.*;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.TemplateResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.LicenceMapper;
import com.cvut.cz.fel.ear.instumentalshop.repository.*;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import com.cvut.cz.fel.ear.instumentalshop.service.LicenceTemplateService;
import com.cvut.cz.fel.ear.instumentalshop.service.impl.licence.strategy.ExclusiveLicenceCreationStrategy;
import com.cvut.cz.fel.ear.instumentalshop.service.impl.licence.strategy.LicenceCreationStrategy;
import com.cvut.cz.fel.ear.instumentalshop.service.impl.licence.strategy.StandardLicenceCreationStrategy;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.LicenceValidator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LicenceTemplateServiceImpl implements LicenceTemplateService {

    private final AuthenticationService authenticationService;

    private final LicenceTemplateRepository licenceTemplateRepository;

    private final TrackRepository trackRepository;

    private final LicenceMapper licenceMapper;

    private final LicenceValidator licenceValidator;

    private final Map<LicenceType, LicenceCreationStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void initStrategies() {
        strategies.put(LicenceType.STANDARD, new StandardLicenceCreationStrategy());
        strategies.put(LicenceType.EXCLUSIVE, new ExclusiveLicenceCreationStrategy());
    }

    @Transactional
    @Override
    public TemplateResponseDto createTemplate(Long trackId, TemplateCreationRequestDto requestDto) {
        Producer leadProducer = authenticationService.getRequestingProducerFromSecurityContext();
        Track track = trackRepository.findTrackById(trackId).orElseThrow(() -> new EntityNotFoundException("Track is not found"));

        licenceValidator.validateTemplateCreationRequest(leadProducer, trackId, requestDto.getLicenceType());

        LicenceCreationStrategy strategy = strategies.get(requestDto.getLicenceType());

        LicenceTemplate licenceTemplate = strategy.createTemplate(requestDto, track);

        licenceTemplate = licenceTemplateRepository.save(licenceTemplate);

        return licenceMapper.toResponseDto(licenceTemplate);
    }

    @Override
    @Transactional
    public TemplateResponseDto getTemplateByTypeAndTrackId(Long trackId, LicenceType licenceType) {
        LicenceTemplate licenceTemplate = licenceTemplateRepository.findByTrackIdAndLicenceType(trackId, licenceType)
                .orElseThrow(() -> new EntityNotFoundException("Template is not found"));

        return licenceMapper.toResponseDto(licenceTemplate);

    }

    @Override
    @Transactional
    public List<TemplateResponseDto> getAllTemplatesByTrack(Long trackId) {
        List<LicenceTemplate> licenceTemplates = licenceTemplateRepository.findByTrackId(trackId);

        return licenceMapper.toResponseDto(licenceTemplates);

    }

    @Override
    @Transactional
    public TemplateResponseDto updateTemplate(Long trackId, LicenceType licenceType, TemplateUpdateRequestDto requestDto) {
        Producer leadProducer = authenticationService.getRequestingProducerFromSecurityContext();
        licenceValidator.validateTemplateUpdateRequest(leadProducer, trackId, requestDto);

        LicenceTemplate licenceTemplate = fetchLicenceTemplate(trackId, licenceType);
        updateLicenceTemplate(licenceTemplate, requestDto);

        LicenceTemplate updatedTemplate = licenceTemplateRepository.save(licenceTemplate);
        return licenceMapper.toResponseDto(updatedTemplate);
    }

    private LicenceTemplate fetchLicenceTemplate(Long trackId, LicenceType licenceType) {
        return licenceTemplateRepository.findByTrackIdAndLicenceType(trackId, licenceType)
                .orElseThrow(() -> new EntityNotFoundException("Template is not found"));
    }

    private void updateLicenceTemplate(LicenceTemplate licenceTemplate, TemplateUpdateRequestDto requestDto) {
        if (licenceTemplate.getLicenceType() == LicenceType.STANDARD) {
            updateStandardLicenceTemplate(licenceTemplate, requestDto);
        } else if (licenceTemplate.getLicenceType() == LicenceType.EXCLUSIVE) {
            licenceTemplate.setPrice(requestDto.getPrice());
        }
    }

    private void updateStandardLicenceTemplate(LicenceTemplate licenceTemplate, TemplateUpdateRequestDto requestDto) {
        if (requestDto.getValidityPeriodDays() != null) {
            licenceTemplate.setValidityPeriodDays(requestDto.getValidityPeriodDays());
        }
        if (requestDto.getPrice() != null) {
            licenceTemplate.setPrice(requestDto.getPrice());
        }
        if (requestDto.getPlatforms() != null && !requestDto.getPlatforms().isEmpty()) {
            licenceTemplate.setAvailablePlatforms(requestDto.getPlatforms());
        }
    }

    @Override
    @Transactional
    public void deleteTemplate(Long trackId, LicenceType licenceType) {
        Producer leadProducer = authenticationService.getRequestingProducerFromSecurityContext();

        licenceValidator.validateTemplateDeleteRequest(leadProducer, trackId);

        LicenceTemplate licenceTemplate = licenceTemplateRepository.findByTrackIdAndLicenceType(trackId, licenceType).orElseThrow(() -> new EntityNotFoundException("Template was not found for this track"));

        licenceTemplateRepository.delete(licenceTemplate);
    }

}
