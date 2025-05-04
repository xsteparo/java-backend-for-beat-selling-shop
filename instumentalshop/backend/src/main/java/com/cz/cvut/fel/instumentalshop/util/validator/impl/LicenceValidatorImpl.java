package com.cz.cvut.fel.instumentalshop.util.validator.impl;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.ReportStatus;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.exception.*;
import com.cz.cvut.fel.instumentalshop.repository.LicenceReportRepository;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
import com.cz.cvut.fel.instumentalshop.util.validator.ValidatorBase;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LicenceValidatorImpl extends ValidatorBase implements LicenceValidator {

    private final ProducerTrackInfoRepository producerTrackInfoRepository;

    private final LicenceTemplateRepository licenceTemplateRepository;

    private final LicenceReportRepository licenceReportRepository;

    private final PurchasedLicenceRepository purchasedLicenceRepository;

    @Override
    public void validateTemplateCreationRequest(Producer producer, Long trackId, LicenceType licenceType) {
        validateIsLeadProducer(producerTrackInfoRepository, trackId, producer.getId());
        validateIfCurrentLicenceTypeExists(trackId, licenceType);
        validateTemplateType(licenceType);
    }

    @Override
    public void validateTemplateUpdateRequest(Producer producer, Long trackId, TemplateUpdateRequestDto requestDto) {
        validateIsLeadProducer(producerTrackInfoRepository, trackId, producer.getId());

        List<PurchasedLicence> purchasedLicences = purchasedLicenceRepository.findPurchasedLicenceByTrackId(trackId);
        if (!purchasedLicences.isEmpty()) {
            throw new TrackIsAlreadyBoughtException("You can't UPDATE licence template, because track is already bought");
        }

        if (requestDto.getValidityPeriodDays() != null && requestDto.getValidityPeriodDays() < 1) {
            throw new IllegalArgumentException("Validity period must be at least 1 day");
        }
        if (requestDto.getPrice() != null && requestDto.getPrice().compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }

    @Override
    public void validateTemplateDeleteRequest(Producer producer, Long trackId) {
        validateIsLeadProducer(producerTrackInfoRepository, trackId, producer.getId());
        List<PurchasedLicence> purchasedLicences = purchasedLicenceRepository.findPurchasedLicenceByTrackId(trackId);

        if (!purchasedLicences.isEmpty()) {
            throw new DeleteRequestException("You can't DELETE licence template, because track is already bought");
        }
    }

    @Override
    public void validateReportGetRequest(User user, Long reportId) {
        boolean isRelatedToReport = (user.getRole() == Role.CUSTOMER && licenceReportRepository.isCustomerRelatedToReport(reportId, user.getId()))
                || (user.getRole() == Role.PRODUCER && licenceReportRepository.isProducerRelatedToReport(reportId, user.getId()));

        if (!isRelatedToReport) {
            throw new IllegalArgumentException(user.getRole() + " is not related to this report");
        }
    }

    @Override
    public void validateReportCreateRequest(PurchasedLicence purchasedLicence, Producer producer) {
        if (purchasedLicence.getProducers().contains(producer)) {
            throw new IllegalArgumentException("Producer is already related to this purchased licence");
        }
    }

    @Override
    public void validateReportUpdateStatus(Producer producer, LicenceReport licenceReport) {
        if (licenceReport.getReportStatus() != ReportStatus.PENDING) {
            throw new IllegalArgumentException("Report status is not pending");
        }
        if (!licenceReport.getPurchasedLicence().getProducers().contains(producer)) {
            throw new AccessDeniedException("Producer is not allowed to update this report");
        }

    }

    @Override
    public void validateReportDeleteRequest(Producer producer, Long reportId) {
        LicenceReport licenceReport = licenceReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Licence report not found with id: " + reportId));

        if (!licenceReport.getPurchasedLicence().getProducers().contains(producer)) {
            throw new AccessDeniedException("Producer is not allowed to delete this report");
        }
    }

    @Override
    public void validatePurchaseCreateRequest(Customer customer, Track track, LicenceType licenceType) {
        validateTrackAvailability(track, licenceType);
        validateCustomerBalance(customer, track, licenceType);
        validateIfTrackAlreadyBoughtByCustomer(customer, track.getId(), licenceType);
    }

    @Override
    public void validatePurchasedLicenceGetRequest(User user, PurchasedLicence purchasedLicence) {
        boolean isCustomer = isCustomer(user, purchasedLicence);
        boolean isProducer = isProducer(user, purchasedLicence);
        if (!isCustomer && !isProducer) {
            throw new AccessDeniedException("User does not have access to this purchased licence");
        }
    }

    private boolean isCustomer(User user, PurchasedLicence purchasedLicence) {
        return user.getRole() == Role.CUSTOMER && user.equals(purchasedLicence.getCustomer());
    }

    private boolean isProducer(User user, PurchasedLicence purchasedLicence) {
        return user.getRole() == Role.PRODUCER && isProducerOfLicence(user, purchasedLicence);
    }

    private boolean isProducerOfLicence(User currentUser, PurchasedLicence purchasedLicence) {
        return purchasedLicence.getProducers().stream()
                .anyMatch(producer -> producer.getId().equals(currentUser.getId()));
    }

    private void validateIfCurrentLicenceTypeExists(Long trackId, LicenceType licenceType) {
        boolean alreadyExistsLicenceType = licenceTemplateRepository.existsByTrackIdAndLicenceType(trackId, licenceType);
        if (alreadyExistsLicenceType) {
            throw new LicenceAlreadyExistsException("A license of this type already exists for this track.");
        }
    }

    private void validateTemplateType(LicenceType licenceType) {
        try {
            LicenceType.valueOf(String.valueOf(licenceType));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid licence type: " + licenceType);
        }
    }

    private void validateTrackAvailability(Track track, LicenceType licenceType) {
        areAllProducersAgreed(track);
        checkIfLicenceIsExclusive(track, licenceType);
        checkIfLicenceExists(track.getId(), licenceType);
    }

    private void validateCustomerBalance(Customer customer, Track track, LicenceType licenceType) {
        LicenceTemplate licenceTemplate = licenceTemplateRepository.findByTrackAndLicenceType(track, licenceType).orElseThrow(() -> new EntityNotFoundException(licenceType + "Template was not found"));

        if (customer.getBalance().compareTo(licenceTemplate.getPrice()) < 0) {
            throw new NotEnoughBalanceException("Not enough balance");
        }
    }

    private void validateIfTrackAlreadyBoughtByCustomer(Customer customer, Long trackId, LicenceType licenceType) {
        if (purchasedLicenceRepository.existsByCustomerIdAndTrackIdAndLicenceTemplate_LicenceType(customer.getId(), trackId, licenceType)) {
            throw new TrackIsAlreadyBoughtException("Track is already bought by you");
        }
    }

    private void checkIfLicenceIsExclusive(Track track, LicenceType licenceType) {
        if (track.isExclusiveBought() && licenceType == LicenceType.EXCLUSIVE) {
            throw new TrackIsAlreadyBoughtException("Exclusive licence already bought by previous customer");
        }
    }

    private void areAllProducersAgreed(Track track) {
        if (!track.isAllProducersAgreedForSelling()) {
            throw new ProducersAreNotAgreedException("Some co-producers have not approved track selling yet");
        }
    }

    private void checkIfLicenceExists(Long trackId, LicenceType licenceType) {
        if (!licenceTemplateRepository.existsByTrackIdAndLicenceType(trackId, licenceType)) {
            throw new IllegalArgumentException("This type of licence does not exist");
        }
    }

}
