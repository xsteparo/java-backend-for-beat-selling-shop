package com.cz.cvut.fel.instumentalshop.dto.producer.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopProducerRequestDto {

    private Long id;
    private String username;
    private double rating;

}
