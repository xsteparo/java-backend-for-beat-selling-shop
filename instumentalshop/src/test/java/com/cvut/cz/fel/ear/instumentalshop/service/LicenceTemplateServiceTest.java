package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.TestDataGenerator;
import com.cvut.cz.fel.ear.instumentalshop.domain.LicenceTemplate;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Track;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.TemplateResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.mapper.LicenceMapper;
import com.cvut.cz.fel.ear.instumentalshop.exception.LicenceAlreadyExistsException;
import com.cvut.cz.fel.ear.instumentalshop.repository.LicenceTemplateRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.TrackRepository;
import com.cvut.cz.fel.ear.instumentalshop.service.impl.licence.LicenceTemplateServiceImpl;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.LicenceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LicenceTemplateServiceTest {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private LicenceTemplateRepository licenceTemplateRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private LicenceValidator licenceValidator;

    @Mock
    private LicenceMapper licenceMapper;

    @InjectMocks
    private LicenceTemplateServiceImpl licenceTemplateService;

    private Producer producer;
    private Track track;
    private TemplateCreationRequestDto standardLicenceRequestDto;
    private TemplateCreationRequestDto exclusiveLicenceRequestDto;
    private TemplateResponseDto responseDto;

    @BeforeEach
    void setUp() {
        producer = TestDataGenerator.createProducer(1L, "producer1");
        track = TestDataGenerator.createTrack(1L, "track1");

        standardLicenceRequestDto = TestDataGenerator.createLicenceRequestDto(LicenceType.STANDARD, new BigDecimal("100.00"));
        exclusiveLicenceRequestDto = TestDataGenerator.createLicenceRequestDto(LicenceType.EXCLUSIVE, new BigDecimal("100.00"));

        responseDto = TestDataGenerator.createResponseDto();

        licenceTemplateService.initStrategies();

        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);

    }

    @Test
    void createStandardLicenceTemplate_Success() {
        responseDto.setLicenceType(LicenceType.STANDARD);

        when(trackRepository.findTrackById(eq(track.getId()))).thenReturn(Optional.of(track));
        when(licenceTemplateRepository.save(any(LicenceTemplate.class))).thenAnswer(i -> i.getArgument(0));
        when(licenceMapper.toResponseDto(any(LicenceTemplate.class))).thenReturn(responseDto);

        TemplateResponseDto result = licenceTemplateService.createTemplate(track.getId(), standardLicenceRequestDto);

        assertNotNull(result);
        assertEquals(LicenceType.STANDARD, result.getLicenceType());
        verify(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.STANDARD);
        verify(licenceTemplateRepository).save(any(LicenceTemplate.class));
        verify(licenceMapper).toResponseDto(any(LicenceTemplate.class));
    }

    @Test
    void createStandardLicenceTemplate_ThrowsLicenceAlreadyExistsException() {
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(trackRepository.findTrackById(eq(track.getId()))).thenReturn(Optional.of(track));

        doThrow(new LicenceAlreadyExistsException("Licence already exists"))
                .when(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.STANDARD);

        assertThrows(LicenceAlreadyExistsException.class, () ->
                licenceTemplateService.createTemplate(track.getId(), standardLicenceRequestDto));

        verify(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.STANDARD);
        verify(licenceTemplateRepository, never()).save(any(LicenceTemplate.class));
    }
    @Test
    void createExclusiveLicenceTemplate_Success() {
        responseDto.setLicenceType(LicenceType.EXCLUSIVE);

        when(trackRepository.findTrackById(eq(track.getId()))).thenReturn(Optional.of(track));
        when(licenceTemplateRepository.save(any(LicenceTemplate.class))).thenAnswer(i -> i.getArgument(0));
        when(licenceMapper.toResponseDto(any(LicenceTemplate.class))).thenReturn(responseDto);

        TemplateResponseDto result = licenceTemplateService.createTemplate(track.getId(), exclusiveLicenceRequestDto);

        assertNotNull(result);
        assertEquals(LicenceType.EXCLUSIVE, result.getLicenceType());
        verify(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.EXCLUSIVE);
        verify(licenceTemplateRepository).save(any(LicenceTemplate.class));
        verify(licenceMapper).toResponseDto(any(LicenceTemplate.class));
    }
}