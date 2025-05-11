package com.cz.cvut.fel.instumentalshop.dto.user.in;

import com.cz.cvut.fel.instumentalshop.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UserCreationRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]{3,15}$",
            message = "Username can only contain letters, numbers, dots, underscores, and hyphens"
    )
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * Avatar file uploaded by client; may be null or empty.
     */
    private MultipartFile avatar;

    private Role role;
}
