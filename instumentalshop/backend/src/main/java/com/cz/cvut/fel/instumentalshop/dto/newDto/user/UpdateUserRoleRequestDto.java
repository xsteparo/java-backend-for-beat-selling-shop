package com.cz.cvut.fel.instumentalshop.dto.newDto.user;

import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDto {
    @NotNull
    private Role role;
}
