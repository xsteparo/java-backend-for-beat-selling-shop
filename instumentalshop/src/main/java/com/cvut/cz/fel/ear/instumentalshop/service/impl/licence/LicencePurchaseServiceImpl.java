package com.cvut.cz.fel.ear.instumentalshop.service.impl.licence;

import com.cvut.cz.fel.ear.instumentalshop.domain.*;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Platform;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Role;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.out.PurchaseDto;
import com.cvut.cz.fel.ear.instumentalshop.repository.*;
import com.cvut.cz.fel.ear.instumentalshop.service.AuthenticationService;
import com.cvut.cz.fel.ear.instumentalshop.service.LicencePurchaseService;
import com.cvut.cz.fel.ear.instumentalshop.util.validator.LicenceValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicencePurchaseServiceImpl implements LicencePurchaseService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AuthenticationService authenticationService;

    private final LicenceTemplateRepository licenceTemplateRepository;

    private final PurchasedLicenceRepository purchasedLicenceRepository;

    private final ProducerRepository producerRepository;

    private final TrackRepository trackRepository;

    private final ProducerTrackInfoRepository producerTrackInfoRepository;

    private final LicenceValidator licenceValidator;

    @Override
    @Transactional
    public PurchaseDto purchaseLicence(PurchaseRequestDto requestDto, Long trackId) {
        Customer customer = authenticationService.getRequestingCustomerFromSecurityContext();

        Track track = trackRepository.findTrackById(trackId).orElseThrow(() -> new EntityNotFoundException("Track is not found"));

        licenceValidator.validatePurchaseCreateRequest(customer, track, requestDto.getLicenceType());

        PurchasedLicence purchasedLicence = processPurchase(track, customer, requestDto);

        return buildPurchaseResponseDto(purchasedLicence);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseDto getPurchasedLicenceById(Long purchaseId) {
        User currentUser = authenticationService.getRequestingUserFromSecurityContext();

        PurchasedLicence purchasedLicence = purchasedLicenceRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchased Licence not found"));

        licenceValidator.validatePurchasedLicenceGetRequest(currentUser, purchasedLicence);

        return buildPurchaseResponseDto(purchasedLicence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseDto> getAllPurchasedLicences() {
        User currentUser = authenticationService.getRequestingUserFromSecurityContext();
        List<PurchasedLicence> purchasedLicences;

        if (currentUser.getRole() == Role.CUSTOMER) {
            purchasedLicences = purchasedLicenceRepository.findByCustomerId(currentUser.getId());
        } else if (currentUser.getRole() == Role.PRODUCER) {
            purchasedLicences = purchasedLicenceRepository.findForProducerByProducerId(currentUser.getId());
        } else {
            throw new AccessDeniedException("User role is not supported for this operation");
        }

        return purchasedLicences.stream()
                .map(this::buildPurchaseResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProducerIncomeDto> getProducerIncomesByTracks() {
        Producer producer = authenticationService.getRequestingProducerFromSecurityContext();
        List<ProducerIncomeDto> result = entityManager.createNamedQuery("ProducerTrackInfo.findProducerIncomeByTracks", ProducerIncomeDto.class)
                .setParameter("producerId", producer.getId())
                .getResultList();
        return result;
    }

    private PurchasedLicence processPurchase(Track track, Customer customer, PurchaseRequestDto requestDto) {
        checkIfLicenceIsExclusive(track, requestDto.getLicenceType());

        LicenceTemplate licenceTemplate = licenceTemplateRepository.findByTrackAndLicenceType(track, requestDto.getLicenceType())
                .orElseThrow(() -> new EntityNotFoundException(requestDto.getLicenceType() + "Template was not found"));

        List<Producer> producers = getTrackProducers(track.getId());

        LocalDateTime purchaseDate = LocalDateTime.now();
        LocalDateTime expiredDate = calculateExpiredDate(licenceTemplate, purchaseDate);

        updateCustomerBalance(customer, licenceTemplate.getPrice());

        PurchasedLicence purchasedLicence = buildPurchasedLicence(track, customer, producers, licenceTemplate, purchaseDate, expiredDate);

        purchasedLicence = purchasedLicenceRepository.save(purchasedLicence);

        distributeIncomeAmongProducers(track.getId(), licenceTemplate.getPrice());

        addPurchasedLicenceToProducers(producers, purchasedLicence);

        return purchasedLicence;
    }

    private void checkIfLicenceIsExclusive(Track track, LicenceType licenceType) {
        if (licenceType == LicenceType.EXCLUSIVE) {
            track.setExclusiveBought(true);
        }
    }

    private LocalDateTime calculateExpiredDate(LicenceTemplate licenceTemplate, LocalDateTime purchaseDate) {
        return purchaseDate.plusDays(licenceTemplate.getValidityPeriodDays());
    }

    private void updateCustomerBalance(Customer customer, BigDecimal amountToSubtract) {
        BigDecimal newBalance = customer.getBalance().subtract(amountToSubtract);
        customer.setBalance(newBalance);
    }

    private List<Producer> getTrackProducers(Long trackId) {
        return producerTrackInfoRepository.findByTrackId(trackId).stream()
                .map(ProducerTrackInfo::getProducer)
                .toList();
    }

    private PurchasedLicence buildPurchasedLicence(Track track, Customer customer,
                                                   List<Producer> producers, LicenceTemplate licenceTemplate,
                                                   LocalDateTime purchaseDate, LocalDateTime expiredDate) {
        return PurchasedLicence.builder()
                .track(track)
                .licenceTemplate(licenceTemplate)
                .purchaseDate(purchaseDate)
                .expiredDate(expiredDate)
                .customer(customer)
                .producers(producers)
                .build();
    }

    private PurchaseDto buildPurchaseResponseDto(PurchasedLicence purchasedLicence) {

        List<Platform> platforms = purchasedLicence.getLicenceTemplate().getAvailablePlatforms();

        Map<Long, String> producerOwners = purchasedLicence.getProducers().stream()
                .collect(Collectors.toMap(Producer::getId, Producer::getUsername));

        return PurchaseDto.builder()
                .purchaseId(purchasedLicence.getId())
                .licenceType(purchasedLicence.getLicenceTemplate().getLicenceType())
                .purchaseDate(purchasedLicence.getPurchaseDate())
                .expiredDate(purchasedLicence.getExpiredDate())
                .price(purchasedLicence.getLicenceTemplate().getPrice())
                .trackId(purchasedLicence.getLicenceTemplate().getTrack().getId())
                .validityPeriodDays(purchasedLicence.getLicenceTemplate().getValidityPeriodDays())
                .availablePlatforms(platforms)
                .producerOwners(producerOwners)
                .build();
    }

    private void distributeIncomeAmongProducers(Long trackId, BigDecimal totalIncome) {
        List<ProducerTrackInfo> producerTrackInfos = producerTrackInfoRepository.findByTrackId(trackId);
        producerTrackInfos.forEach(producerTrackInfo -> {
            BigDecimal percentage = producerTrackInfo.getProfitPercentage();
            BigDecimal producerIncome = totalIncome.multiply(percentage).divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
            Producer producer = producerTrackInfo.getProducer();
            producer.setSalary(producer.getSalary().add(producerIncome));
            producerRepository.save(producer);
        });
    }

    private void addPurchasedLicenceToProducers(List<Producer> producers, PurchasedLicence purchasedLicence) {
        producers.forEach(producer -> {
            producer.getSoldLicences().add(purchasedLicence);
            producerRepository.save(producer);
        });
    }

}
