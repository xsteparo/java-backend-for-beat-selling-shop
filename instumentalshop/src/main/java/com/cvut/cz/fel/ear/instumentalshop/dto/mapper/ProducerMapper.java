package com.cvut.cz.fel.ear.instumentalshop.dto.mapper;

import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.Role;
import com.cvut.cz.fel.ear.instumentalshop.dto.user.out.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProducerMapper {
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", expression = "java(roleToString(source.getRole()))")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "registrationDate", source = "registrationDate")
    @Mapping(target = "balance", source = "salary", ignore = true)
    UserDto toResponseDto(Producer source);

    List<UserDto> toResponseDto(List<Producer> source);

    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }
}
