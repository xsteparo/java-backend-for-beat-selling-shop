package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;


import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Role;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", expression = "java(roleToString(source.getRole()))")
    @Mapping(target = "registrationDate", source = "registrationDate", dateFormat = "yyyy-MM-dd HH:mm")
    UserDto toResponseDto(Customer source);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", expression = "java(roleToString(source.getRole()))")
    @Mapping(target = "registrationDate", source = "registrationDate", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "balance", source = "balance", ignore = true)
    UserDto toGetResponseDto(Customer source);

    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }

}