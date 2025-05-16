package com.cz.cvut.fel.instumentalshop.dto.mapper;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.User;
import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UpdateProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.newDto.user.UserProfileDto;
import com.cz.cvut.fel.instumentalshop.dto.user.in.UserCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.user.out.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper pro převod mezi entitou {@link User} a DTO objekty.
 * Používá MapStruct pro generování implementace.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Převede entitu User na DTO pro zobrazení profilu.
     *
     * @param user entita uživatele
     * @return DTO s detaily profilu
     */
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "registrationDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "bio", source = "user.bio")
    UserProfileDto toProfileDto(User user);

    /**
     * Převede entitu User na obecné DTO UserDto (např. pro registraci).
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "balance", source = "user.balance")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "registrationDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "bio", source = "user.bio")
    UserDto toDto(User user);

    /**
     * Vytvoří entitu User z DTO pro registraci.
     * Ignoruje automaticky generovaná a citlivá pole.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "password", ignore = true)
    User fromCreationRequest(UserCreationRequestDto dto);

    /**
     * Aktualizuje entitu User z DTO s novými hodnotami.
     * Nezměněná (null) pole se ignorují.
     */
    @Mapping(target = "username", source = "dto.username")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "bio", source = "dto.bio")
    void updateFromDto(UpdateProfileDto dto, @MappingTarget User user);
}
