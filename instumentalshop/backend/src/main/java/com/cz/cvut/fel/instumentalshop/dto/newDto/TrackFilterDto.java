package com.cz.cvut.fel.instumentalshop.dto.newDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackFilterDto {
    private String tab;

    private String search;

    private String genre;

    private String tempoRange;

    private String key;

    private String sort;
}
