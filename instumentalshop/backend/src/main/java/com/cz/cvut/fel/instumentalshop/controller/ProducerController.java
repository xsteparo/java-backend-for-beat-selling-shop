package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.PurchaseDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.in.TopProducerRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.LicencePurchaseService;
import com.cz.cvut.fel.instumentalshop.service.ProducerService;
import com.cz.cvut.fel.instumentalshop.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * {@code ProducerController} poskytuje REST API pro operace spojené s producenty.
 * Obsahuje veřejné i zabezpečené endpointy pro správu profilů,
 * získávání statistik a správy vlastních skladeb.
 */
@RestController
@RequestMapping("/api/v1/producers")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;
    private final TrackService trackService;
    private final LicencePurchaseService licenceService;
    private final AuthenticationService auth;

    /**
     * FR09: Registrace nového producenta.
     *
     * @param dto DTO s údaji pro registraci
     * @return vytvořený uživatel
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreationRequestDto dto) {
        UserDto created = producerService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * FR11: Získání profilu producenta podle ID (veřejné).
     *
     * @param producerId ID producenta
     * @return DTO producenta
     */
    @GetMapping("/{producerId}")
    public ResponseEntity<UserDto> getProducerById(@PathVariable Long producerId) {
        UserDto dto = producerService.getProducerById(producerId);
        return ResponseEntity.ok(dto);
    }

    /**
     * FR13: Výpis všech producentů (veřejné).
     *
     * @return seznam producentů
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllProducers() {
        List<UserDto> list = producerService.getAllProducers();
        return ResponseEntity.ok(list);
    }

    /**
     * FR22: Výpis top producentů podle popularity.
     *
     * @param limit počet záznamů
     * @return seznam top producentů
     */
    @GetMapping("/top")
    public ResponseEntity<List<TopProducerRequestDto>> getTopProducers(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopProducerRequestDto> top = producerService.getTopProducers(limit);
        return ResponseEntity.ok(top);
    }

    // --- Endpoints pro přihlášeného PRODUCERA ---

    /**
     * Získání vlastního profilu.
     *
     * @return DTO profilu
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<UserDto> getMyProfile() {
        Long id = auth.getRequestingProducerFromSecurityContext().getId();
        UserDto dto = producerService.getProducerById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Úprava vlastního profilu.
     *
     * @param dto DTO s novými údaji
     * @return aktualizovaný profil
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<UserDto> updateMyProfile(
            @Valid @RequestBody UserUpdateRequestDto dto) {
        UserDto updated = producerService.updateProducer(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Smazání vlastního účtu producenta.
     */
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<Void> deleteMyAccount() {
        producerService.deleteProducer();
        return ResponseEntity.noContent().build();
    }

    /**
     * FR10: Výpis vlastních skladeb.
     *
     * @return seznam skladeb producenta
     */
    @GetMapping("/me/tracks")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<List<TrackDto>> getMyTracks() {
        Long id = auth.getRequestingProducerFromSecurityContext().getId();
        List<TrackDto> tracks = trackService.findAllByProducer(id);
        return ResponseEntity.ok(tracks);
    }

    /**
     * FR10: Výpis statistik nákupů zákazníků pro tohoto producenta.
     *
     * @return seznam statistik nákupů podle zákazníka
     */
    @GetMapping("/me/customer-stats")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<List<ProducerPurchaseStatisticDto>> getCustomerStats() {
        List<ProducerPurchaseStatisticDto> stats = producerService.getCustomerPurchaseStatisticsForProducer();
        return ResponseEntity.ok(stats);
    }

    /**
     * FR10: Získání zůstatku na účtu producenta.
     *
     * @return DTO se zůstatkem
     */
    @GetMapping("/me/balance")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<BalanceResponseDto> getMyBalance() {
        BalanceResponseDto bal = producerService.getBalance();
        return ResponseEntity.ok(bal);
    }

    /**
     * FR10: Výpis příjmů podle skladeb.
     *
     * @return seznam příjmů
     */
    @GetMapping("/me/incomes")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<List<ProducerIncomeDto>> getMyIncomes() {
        List<ProducerIncomeDto> incomes = licenceService.getProducerIncomesByTracks();
        return ResponseEntity.ok(incomes);
    }

    /**
     * FR10: Výpis prodejů (zakoupených licencí) pro tohoto producenta.
     *
     * @return seznam prodejů
     */
    @GetMapping("/me/sales")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<List<PurchaseDto>> getMySales() {
        List<PurchaseDto> sales = licenceService.getAllPurchasedLicences();
        return ResponseEntity.ok(sales);
    }
}

