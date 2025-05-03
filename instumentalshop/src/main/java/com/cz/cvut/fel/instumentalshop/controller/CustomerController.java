package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.dto.balance.in.IncreaseBalanceRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import com.cz.cvut.fel.instumentalshop.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/customers/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreationRequestDto requestDto) {
        UserDto createdCustomer = customerService.register(requestDto);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/customers/increase-balance")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BalanceResponseDto> increaseBalance(@Valid @RequestBody IncreaseBalanceRequestDto requestDto){
        BalanceResponseDto responseDto = customerService.increaseBalance(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/customers/balance")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BalanceResponseDto> getBalance() {
        BalanceResponseDto customer = customerService.getBalance();
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> getCustomerById(@PathVariable Long id) {
        UserDto customer = customerService.getCustomerById(id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PutMapping("/customers/update")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<UserDto> updateCustomer(@Valid @RequestBody UserUpdateRequestDto requestDto) {
        UserDto customer = customerService.updateCustomer(requestDto);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @DeleteMapping("/customers/delete")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Void> deleteCustomer() {
        customerService.deleteCustomer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}