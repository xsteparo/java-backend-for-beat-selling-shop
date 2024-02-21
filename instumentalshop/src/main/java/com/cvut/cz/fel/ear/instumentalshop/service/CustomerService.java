package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.in.IncreaseBalanceRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;
import jakarta.validation.Valid;

public interface CustomerService {

    UserDto register(@Valid UserCreationRequestDto customerCreationRequestDTO);

    BalanceResponseDto increaseBalance(IncreaseBalanceRequestDto requestDto);

    BalanceResponseDto getBalance();

    UserDto getCustomerById(Long id);

    UserDto updateCustomer(UserUpdateRequestDto requestDto);

    void deleteCustomer();

}
