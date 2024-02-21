package com.cvut.cz.fel.ear.instumentalshop.dto.track.in;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProducerShareRequestDto {

    @NotBlank(message = "Producer name is required")
    @Size(max = 100, message = "Producer name must be less than 100 characters")
    @Pattern(regexp = "^[\\p{L}0-9\\s]+$", message = "Producer name can only contain letters, numbers, and spaces")
    private String producerName;

    @NotNull(message = "Profit percentage is required")
    @Min(value = 0, message = "Profit percentage must be at least 0")
    @Max(value = 100, message = "Profit percentage cannot be more than 100")
    private Integer profitPercentage;
}