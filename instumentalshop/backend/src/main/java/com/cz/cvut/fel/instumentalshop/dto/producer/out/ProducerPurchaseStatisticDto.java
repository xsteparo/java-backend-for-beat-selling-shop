package com.cz.cvut.fel.instumentalshop.dto.producer.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProducerPurchaseStatisticDto {

    private Long customerId;

    private String customerUsername;

    private Integer totalPurchases;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastPurchaseDate;

}
