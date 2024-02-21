package com.cvut.cz.fel.ear.instumentalshop.controller;

import com.cvut.cz.fel.ear.instumentalshop.domain.PurchasedLicence;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;
import com.cvut.cz.fel.ear.instumentalshop.service.LicencePurchaseService;
import com.cvut.cz.fel.ear.instumentalshop.service.ProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    private final LicencePurchaseService licencePurchaseService;

    @PostMapping("/producers/register")
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreationRequestDto requestDto) {
        UserDto producerDto = producerService.register(requestDto);
        return new ResponseEntity<>(producerDto, HttpStatus.CREATED);
    }

    @GetMapping("/producers/customer-purchase-statistics")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<ProducerPurchaseStatisticDto>> getCustomerPurchaseInfoByProducer() {
        List<ProducerPurchaseStatisticDto> customersDto = producerService.getCustomerPurchaseStatisticsForProducer();
        return ResponseEntity.ok(customersDto);
    }

    @GetMapping("/producers/balance")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<BalanceResponseDto> getBalance() {
        BalanceResponseDto producerDto = producerService.getBalance();
        return ResponseEntity.ok(producerDto);
    }

    @GetMapping("/producers/incomes")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<ProducerIncomeDto>> getProducerIncomes() {
        List<ProducerIncomeDto> incomeDto = licencePurchaseService.getProducerIncomesByTracks();
        return ResponseEntity.ok(incomeDto);
    }

    @GetMapping("/producers")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserDto>> getAllProducers() {
        List<UserDto> producersDto = producerService.getAllProducers();
        return ResponseEntity.ok(producersDto);
    }

    @GetMapping("/producers/{producerId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> getProducerById(@PathVariable Long producerId) {
        UserDto producerDto = producerService.getProducerById(producerId);
        return ResponseEntity.ok(producerDto);
    }

    @PutMapping("/producers/update")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<UserDto> updateProducer(@RequestBody @Valid UserUpdateRequestDto requestDto) {
        UserDto producerDto = producerService.updateProducer(requestDto);
        return ResponseEntity.ok(producerDto);
    }

    @DeleteMapping("/producers/delete")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<Void> deleteProducer() {
        producerService.deleteProducer();
        return ResponseEntity.ok().build();
    }

}
