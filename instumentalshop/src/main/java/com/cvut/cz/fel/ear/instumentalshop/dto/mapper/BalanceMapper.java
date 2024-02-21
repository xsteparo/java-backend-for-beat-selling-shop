package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;

import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.dto.balance.out.BalanceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "balance", source = "balance")
    BalanceResponseDto toResponseDto(Customer customer);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "balance", source = "salary")
    BalanceResponseDto toResponseDto(Producer producer);

}
