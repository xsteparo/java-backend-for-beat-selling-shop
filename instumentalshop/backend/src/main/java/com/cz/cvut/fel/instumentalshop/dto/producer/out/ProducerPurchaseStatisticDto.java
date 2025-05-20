package com.cz.cvut.fel.instumentalshop.dto.producer.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProducerPurchaseStatisticDto {

    private Long customerId;

    private String customerUsername;

    private Integer totalPurchases;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastPurchaseDate;

    public ProducerPurchaseStatisticDto(Long customerId, String customerUsername, Long totalPurchases, LocalDateTime lastPurchaseDate) {
        this.customerId = customerId;
        this.customerUsername = customerUsername;
        this.totalPurchases = Math.toIntExact(totalPurchases);
        this.lastPurchaseDate = lastPurchaseDate;
    }

}
