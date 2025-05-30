package com.cz.cvut.fel.instumentalshop.service.impl;


import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.PurchasedLicence;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.domain.enums.KeyType;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TrackSpecificationBuilder} vytváří JPA {@link Specification} pro entitu {@link Track}
 * na základě parametrů z {@link TrackFilterDto}.
 * <p>
 * Podporované filtry:
 * - Hledání dle názvu skladby nebo uživatelského jména producenta (search)
 * - Filtr žánru (genre)
 * - Rozsah tempa BPM (tempoRange)
 * - Klíč tóniny (key)
 * <p>
 * Specifikace jsou skládány pomocí logického AND.
 */
public class TrackSpecificationBuilder {

    /**
     * Vytvoří specifikaci dle zadaného filtru.
     *
     * @param filter DTO s parametry filtru
     * @return Spojená specifikace pro entitu Track
     */
    public static Specification<Track> fromFilter(TrackFilterDto filter) {
        List<Specification<Track>> specs = new ArrayList<>();

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            specs.add(searchSpec(filter.getSearch()));
        }
        if (filter.getGenre() != null && !filter.getGenre().isBlank()) {
            specs.add(genreSpec(filter.getGenre()));
        }
        if (filter.getTempoRange() != null && !filter.getTempoRange().isBlank()) {
            specs.add(tempoRangeSpec(filter.getTempoRange()));
        }
        if (filter.getKey() != null && !filter.getKey().isBlank()) {
            specs.add(keySpec(filter.getKey()));
        }

        specs.add(excludeExclusiveSoldSpec());


        // Spojit všechny specifikace pomocí AND
        Specification<Track> result = specs.stream()
                .reduce(all(), Specification::and);
        return result;
    }

    /**
     * Vrátí specifikaci, která vždy provádí prázdné podmínky (vždy true).
     */
    private static Specification<Track> all() {
        return (root, query, cb) -> cb.conjunction();
    }

    /**
     * Specifikace pro fulltextové hledání.
     *
     * @param search Text pro hledání
     */
    private static Specification<Track> searchSpec(String search) {
        String lowered = search.toLowerCase().trim();
        return (root, query, cb) -> {
            Join<Track, Producer> producerJoin = root.join("producer", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + lowered + "%"),
                    cb.like(cb.lower(producerJoin.get("username")), "%" + lowered + "%")
            );
        };
    }

    /**
     * Specifikace pro filtraci dle žánru.
     *
     * @param genre Řetězec s názvem žánru (např. "POP")
     */
    private static Specification<Track> genreSpec(String genre) {
        GenreType type = GenreType.valueOf(genre.trim().toUpperCase());
        return (root, query, cb) -> cb.equal(root.get("genre"), type);
    }

    /**
     * Specifikace pro filtraci dle rozsahu BPM.
     *
     * @param tempoRange Řetězec ve formátu "min-max" (např. "80-120")
     */
    private static Specification<Track> tempoRangeSpec(String tempoRange) {
        String[] parts = tempoRange.trim().split("-");
        int low = Integer.parseInt(parts[0]);
        int high = Integer.parseInt(parts[1]);
        return (root, query, cb) -> cb.between(root.get("bpm"), low, high);
    }

    /**
     * Specifikace pro filtraci dle tóniny.
     *
     * @param key Řetězec s tóninou (např. "C#")
     */
    private static Specification<Track> keySpec(String key) {
        KeyType kt = KeyType.fromString(key.trim().toUpperCase());
        return (root, query, cb) -> cb.equal(root.get("keyType"), kt);
    }

    private static Specification<Track> excludeExclusiveSoldSpec() {
        return (root, query, cb) -> {
            // EXISTS podmínka: je někdo, kdo koupil EXCLUSIVE licenci na tento track
            var subquery = query.subquery(Long.class);
            var purchased = subquery.from(PurchasedLicence.class);
            subquery.select(cb.literal(1L));
            subquery.where(
                    cb.equal(purchased.get("track"), root),
                    cb.equal(purchased.get("licenceTemplate").get("licenceType"), LicenceType.EXCLUSIVE)
            );

            return cb.not(cb.exists(subquery));
        };
    }
}
