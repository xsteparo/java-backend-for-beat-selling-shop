package com.cz.cvut.fel.instumentalshop.dto.track.in;

import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class TrackRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Name can only contain alphanumeric characters and spaces")
    private String name;

    @NotNull(message = "Genre type is required")
    private GenreType genreType;

    @NotNull(message = "BPM is required")
    @Min(value = 20, message = "BPM must be at least 20")
    @Max(value = 300, message = "BPM cannot be more than 300")
    private Integer bpm;

    private String key;

    private Integer price;

    private Integer mainProducerPercentage;

    private List<@Valid ProducerShareRequestDto> producerShares;

    /**
     * Non-Exclusive license: mp3
     */
    @NotNull(message = "Non-exclusive file is required")
    private MultipartFile nonExclusiveFile;

    /**
     * Premium license: wav
     */
    private MultipartFile premiumFile;

    /**
     * Exclusive license: zip (mp3+wav+лицензия внутрь)
     */
    private MultipartFile exclusiveFile;

}
