package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.balance.in.IncreaseBalanceRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import jakarta.validation.Valid;

public interface CustomerService {

    UserDto register(@Valid UserCreationRequestDto customerCreationRequestDTO);

    BalanceResponseDto increaseBalance(IncreaseBalanceRequestDto requestDto);

    BalanceResponseDto getBalance();

    UserDto getCustomerById(Long id);

    UserDto updateCustomer(UserUpdateRequestDto requestDto);

    void deleteCustomer();

}
