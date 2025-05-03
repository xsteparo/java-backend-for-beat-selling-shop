package com.cz.cvut.fel.instumentalshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cz.cvut.fel.instumentalshop.domain.LicenceReport;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.repository.LicenceReportRepository;
import com.cz.cvut.fel.instumentalshop.service.impl.licence.LicenceReportServiceImpl;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LicenceReportServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LicenceReportRepository licenceReportRepository;

    @Mock
    private LicenceValidator licenceValidator;

    @InjectMocks
    private LicenceReportServiceImpl licenceReportService;

    private Producer producer;
    private LicenceReport licenceReport;

    @BeforeEach
    void setUp() {
        producer = new Producer();
        licenceReport = new LicenceReport();
        licenceReport.setId(1L);

    }

    @Test
    void deleteReportById_Successful() {
        Long reportId = 1L;

        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        when(licenceReportRepository.findById(reportId)).thenReturn(Optional.of(licenceReport));

        licenceReportService.deleteReportById(reportId);

        verify(licenceValidator).validateReportDeleteRequest(producer, reportId);
        verify(licenceReportRepository).delete(licenceReport);
    }

    @Test
    void deleteReportById_ReportNotFound_ThrowsEntityNotFoundException() {
        Long reportId = 2L;

        when(licenceReportRepository.findById(reportId)).thenThrow(new EntityNotFoundException("Licence report not found with id: " + reportId));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> licenceReportService.deleteReportById(reportId),
                "Expected deleteReportById to throw EntityNotFoundException, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Licence report not found with id: " + reportId));
        verify(licenceReportRepository, never()).delete(any(LicenceReport.class));
    }
}