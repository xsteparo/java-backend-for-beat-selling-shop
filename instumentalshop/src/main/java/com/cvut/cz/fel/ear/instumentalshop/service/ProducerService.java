package com.cvut.cz.fel.ear.instumentalshop.service;

import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;

import java.util.List;

public interface ProducerService {

    UserDto register(UserCreationRequestDto requestDto);

    BalanceResponseDto getBalance();

    List<ProducerPurchaseStatisticDto> getCustomerPurchaseStatisticsForProducer();

    List<UserDto> getAllProducers();

    UserDto getProducerById(Long id);

    UserDto updateProducer(UserUpdateRequestDto requestDto);

    void deleteProducer();

}
