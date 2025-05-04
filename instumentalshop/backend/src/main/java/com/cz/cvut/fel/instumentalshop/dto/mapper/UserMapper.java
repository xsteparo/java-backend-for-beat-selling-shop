package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", expression = "java(roleToString(source.getRole()))")
    @Mapping(target = "registrationDate", source = "registrationDate", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "username", source = "username")
    UserDto toCustomerResponseDto(Customer source);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", expression = "java(roleToString(source.getRole()))")
    @Mapping(target = "registrationDate", source = "registrationDate", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "balance", source = "salary")
    @Mapping(target = "username", source = "username")
    UserDto toProducerResponseDto(Producer source);

    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }

}
