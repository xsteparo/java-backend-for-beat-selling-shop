package com.cz.cvut.fel.instumentalshop.service.impl.licence;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.domain.enums.Platform;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.PurchaseRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.repository.*;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.LicencePurchaseService;
import com.cz.cvut.fel.instumentalshop.util.validator.LicenceValidator;
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
    private final EntityManager em;

    private final AuthenticationService       auth;
    private final LicenceTemplateRepository   tplRepo;
    private final PurchasedLicenceRepository  licRepo;
    private final ProducerRepository          producerRepo;
    private final TrackRepository             trackRepo;
    private final ProducerTrackInfoRepository ptiRepo;
    private final LicenceValidator            validator;

    /*─────────────────────────────────── API ───────────────────────────────────*/

    @Override @Transactional
    public PurchaseDto purchaseLicence(PurchaseRequestDto dto, Long trackId) {

        Customer customer = auth.getRequestingCustomerFromSecurityContext();
        Track    track    = trackRepo.findTrackById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found"));

        validator.validatePurchaseCreateRequest(customer, track, dto.getLicenceType());

        PurchasedLicence lic = processPurchase(track, customer, dto.getLicenceType());
        return buildDto(lic);
    }

    @Override @Transactional
    public PurchaseDto getPurchasedLicenceById(Long id) {
        User user = auth.getRequestingUserFromSecurityContext();

        PurchasedLicence lic = licRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchased licence not found"));

        validator.validatePurchasedLicenceGetRequest(user, lic);
        return buildDto(lic);
    }

    @Override @Transactional
    public List<PurchaseDto> getAllPurchasedLicences() {

        User user = auth.getRequestingUserFromSecurityContext();
        List<PurchasedLicence> list;

        if (user.getRole() == Role.CUSTOMER) {
            list = licRepo.findByCustomerId(user.getId());

        } else if (user.getRole() == Role.PRODUCER) {
            list = licRepo.findForProducerByProducerId(user.getId());

        } else throw new AccessDeniedException("Unsupported role");

        return list.stream().map(this::buildDto).toList();
    }

    @Override @Transactional
    public List<ProducerIncomeDto> getProducerIncomesByTracks() {
        Producer producer = auth.getRequestingProducerFromSecurityContext();
        return em.createNamedQuery(
                        "ProducerTrackInfo.findProducerIncomeByTracks",
                        ProducerIncomeDto.class)
                .setParameter("producerId", producer.getId())
                .getResultList();
    }

    /*────────────────────────────  INTERNAL  ────────────────────────────*/

    private PurchasedLicence processPurchase(Track track,
                                             Customer customer,
                                             LicenceType type) {
        LicenceTemplate tpl = tplRepo.findByTrackAndLicenceType(track, type)
                .orElseThrow(() -> new EntityNotFoundException(type + " template not found"));

        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime until = tpl.getValidityPeriodDays() == null
                ? null
                : now.plusDays(tpl.getValidityPeriodDays());

        // 1) Списываем со счёта покупателя
        customer.setBalance(customer.getBalance().subtract(tpl.getPrice()));

        // 2) Создаём запись о покупке
        PurchasedLicence lic = PurchasedLicence.builder()
                .track(track)
                .licenceTemplate(tpl)
                .purchaseDate(now)
                .expiredDate(until)
                .customer(customer)
                .producer(track.getProducer())
                .build();

        lic = licRepo.save(lic);

        // 3) Распределяем доход
        distributeIncomeAmongProducers(track.getId(), tpl.getPrice());

        return lic;
    }

    private PurchaseDto buildDto(PurchasedLicence lic) {

        LicenceTemplate tpl = lic.getLicenceTemplate();

        return PurchaseDto.builder()
                .purchaseId(lic.getId())
                .licenceType(tpl.getLicenceType())
                .purchaseDate(lic.getPurchaseDate())
                .expiredDate(lic.getExpiredDate())
                .price(tpl.getPrice())
                .trackId(tpl.getTrack().getId())
                .validityPeriodDays(tpl.getValidityPeriodDays())
                .availablePlatforms(tpl.getAvailablePlatforms())
                .producerOwners(Map.of(lic.getProducer().getId(), lic.getProducer().getUsername()))
                .build();
    }

    private void distributeIncomeAmongProducers(Long trackId, BigDecimal total) {

        List<ProducerTrackInfo> infos = ptiRepo.findByTrackId(trackId);

        infos.forEach(info -> {
            BigDecimal pct    = info.getProfitPercentage();                // %
            BigDecimal income = total.multiply(pct)
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);

            Producer p = info.getProducer();
            p.setBalance(p.getBalance().add(income));
            producerRepo.save(p);
        });
    }
}


