package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.User;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Role;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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
