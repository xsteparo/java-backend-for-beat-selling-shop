package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserUpdateRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;

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
