package com.cz.cvut.fel.instumentalshop.dto.user.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,15}$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

}
