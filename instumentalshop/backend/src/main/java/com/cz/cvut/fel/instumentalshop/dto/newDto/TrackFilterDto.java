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
    /** Вкладка: "new", "top" или "trending" */
    private String tab;

    /** Строка поиска по названию или имени продюсера */
    private String search;

    /** Жанр (строка вида "DNB", "POP" и т.п.) */
    private String genre;

    /** Диапазон темпа в формате "min-max", например "80-120" */
    private String tempoRange;

    /** Тональность ("C", "D#", и т.п.) */
    private String key;

    /** Сортировка: например "-rating" или "createdAt" */
    private String sort;
}
