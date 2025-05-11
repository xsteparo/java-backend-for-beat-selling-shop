package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.dto.balance.out.BalanceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "balance", source = "balance")
    BalanceResponseDto toResponseDto(Customer customer);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "balance", source = "balance")
    BalanceResponseDto toResponseDto(Producer producer);

}
