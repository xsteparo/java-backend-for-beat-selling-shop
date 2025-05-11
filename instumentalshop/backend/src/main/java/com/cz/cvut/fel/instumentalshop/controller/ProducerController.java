package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.ProducerIncomeDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.in.TopProducerRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.service.LicencePurchaseService;
import com.cz.cvut.fel.instumentalshop.service.ProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/producers")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    private final LicencePurchaseService licencePurchaseService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreationRequestDto requestDto) {
        UserDto producerDto = producerService.register(requestDto);
        return new ResponseEntity<>(producerDto, HttpStatus.CREATED);
    }

    @GetMapping("/customer-purchase-statistics")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<ProducerPurchaseStatisticDto>> getCustomerPurchaseInfoByProducer() {
        List<ProducerPurchaseStatisticDto> customersDto = producerService.getCustomerPurchaseStatisticsForProducer();
        return ResponseEntity.ok(customersDto);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<BalanceResponseDto> getBalance() {
        BalanceResponseDto producerDto = producerService.getBalance();
        return ResponseEntity.ok(producerDto);
    }

    @GetMapping("/incomes")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<List<ProducerIncomeDto>> getProducerIncomes() {
        List<ProducerIncomeDto> incomeDto = licencePurchaseService.getProducerIncomesByTracks();
        return ResponseEntity.ok(incomeDto);
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserDto>> getAllProducers() {
        List<UserDto> producersDto = producerService.getAllProducers();
        return ResponseEntity.ok(producersDto);
    }

    @GetMapping("/{producerId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> getProducerById(@PathVariable Long producerId) {
        UserDto producerDto = producerService.getProducerById(producerId);
        return ResponseEntity.ok(producerDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<UserDto> updateProducer(@RequestBody @Valid UserUpdateRequestDto requestDto) {
        UserDto producerDto = producerService.updateProducer(requestDto);
        return ResponseEntity.ok(producerDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('PRODUCER')")
    public ResponseEntity<Void> deleteProducer() {
        producerService.deleteProducer();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<TopProducerRequestDto>> getTopProducers(@RequestParam(defaultValue = "10") int limit) {
        List<TopProducerRequestDto> topProducers = producerService.getTopProducers(limit);
        return ResponseEntity.ok(topProducers);
    }

}
