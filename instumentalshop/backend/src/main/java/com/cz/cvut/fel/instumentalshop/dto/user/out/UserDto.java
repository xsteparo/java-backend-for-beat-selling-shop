package com.cz.cvut.fel.instumentalshop.dto.user.out;

import com.cz.cvut.fel.instumentalshop.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDto {

    private Long userId;

    private String username;

    private String email;

    private String role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime registrationDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal balance;

    private String avatarUrl;

    public static UserDto fromEntity(User user){
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null
                        ? user.getRole().name().toLowerCase()
                        : null,
                user.getRegistrationDate()
                ,
                user.getBalance(),
                user.getAvatarUrl()
        );
    }
    }


