package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.TestDataGenerator;
import com.cz.cvut.fel.instumentalshop.domain.LicenceTemplate;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.TemplateResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.mapper.LicenceMapper;
import com.cz.cvut.fel.instumentalshop.exception.LicenceAlreadyExistsException;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.licence.LicenceTemplateServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
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

        standardLicenceRequestDto = TestDataGenerator.createLicenceRequestDto(LicenceType.NON_EXCLUSIVE, new BigDecimal("100.00"));
        exclusiveLicenceRequestDto = TestDataGenerator.createLicenceRequestDto(LicenceType.EXCLUSIVE, new BigDecimal("100.00"));

        responseDto = TestDataGenerator.createResponseDto();

        licenceTemplateService.initStrategies();

        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);

    }

    @Test
    void createStandardLicenceTemplate_Success() {
        responseDto.setLicenceType(LicenceType.NON_EXCLUSIVE);

        when(trackRepository.findTrackById(eq(track.getId()))).thenReturn(Optional.of(track));
        when(licenceTemplateRepository.save(any(LicenceTemplate.class))).thenAnswer(i -> i.getArgument(0));
        when(licenceMapper.toResponseDto(any(LicenceTemplate.class))).thenReturn(responseDto);

        TemplateResponseDto result = licenceTemplateService.createTemplate(track.getId(), standardLicenceRequestDto);

        assertNotNull(result);
        assertEquals(LicenceType.NON_EXCLUSIVE, result.getLicenceType());
        verify(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.NON_EXCLUSIVE);
        verify(licenceTemplateRepository).save(any(LicenceTemplate.class));
        verify(licenceMapper).toResponseDto(any(LicenceTemplate.class));
    }

    @Test
    void createStandardLicenceTemplate_ThrowsLicenceAlreadyExistsException() {
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(trackRepository.findTrackById(eq(track.getId()))).thenReturn(Optional.of(track));

        doThrow(new LicenceAlreadyExistsException("Licence already exists"))
                .when(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.NON_EXCLUSIVE);

        assertThrows(LicenceAlreadyExistsException.class, () ->
                licenceTemplateService.createTemplate(track.getId(), standardLicenceRequestDto));

        verify(licenceValidator).validateTemplateCreationRequest(producer, track.getId(), LicenceType.NON_EXCLUSIVE);
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