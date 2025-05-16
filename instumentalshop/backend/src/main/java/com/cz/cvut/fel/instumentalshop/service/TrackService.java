package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.exception.ProducerTrackInfoNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpRange;

import java.io.IOException;
import java.util.List;


/**
 * {@code TrackService} poskytuje operace pro práci se skladbami (Track) na platformě.
 * Zabezpečuje CRUD operace, filtrování, streamování a správu vztahů producentů.
 * <p>
 * Podporované funkce:
 * <ul>
 *   <li>Vyhledávání a filtrování skladb podle kritérií (genre, BPM, tónina, fulltext).</li>
 *   <li>Streamování audio obsahu s podporou Range požadavků.</li>
 *   <li>Správa skladeb producentem (vytvoření, úprava, smazání).</li>
 *   <li>Potvrzení dohody producentů o prodeji skladeb.</li>
 *   <li>Výpis skladeb pro daného producenta a jejich prodeje.</li>
 * </ul>
 */
public interface TrackService {

    void incrementPlays(Long trackId);

    /**
     * Vrátí stránkovaný seznam skladb podle zadaného filtru a stránkování.
     *
     * @param filter   DTO s parametry filtru (tab, search, genre, tempoRange, key, sort)
     * @param pageable stránkovací a řadicí informace
     * @return Stránka DTO skladb (TrackDto)
     */
    Page<TrackDto> findAll(TrackFilterDto filter, Pageable pageable);

    /**
     * Najde skladbu podle jejího ID.
     *
     * @param trackId ID hledané skladby
     * @return DTO skladby (TrackDto)
     * @throws EntityNotFoundException pokud skladba neexistuje
     */
    TrackDto findById(Long trackId);

    /**
     * Načte audio soubor skladby jako Spring Resource.
     *
     * @param trackId ID skladby
     * @return Resource reprezentující audio soubor
     * @throws IOException pokud nastane chyba při přístupu k souboru
     */
    Resource loadAsResource(Long trackId) throws IOException;

    /**
     * Vytvoří region pro HTTP Range streamování.
     *
     * @param resource      audio Resource
     * @param ranges        seznam HttpRange z hlavičky požadavku
     * @param contentLength délka Resource v bajtech
     * @return Region pro vrácení klientovi
     * @throws IOException pokud nastane chyba při čtení rozsahu
     */
    ResourceRegion buildRegion(Resource resource, List<HttpRange> ranges, long contentLength) throws IOException;

    /**
     * Vytvoří novou skladbu s nahráním audio souborů a metadaty.
     *
     * @param dto DTO s informacemi o skladbě a souborech
     * @return DTO nově vytvořené skladby
     * @throws IOException pokud nastane problém s nahráním souborů
     */
    TrackDto createTrack(TrackRequestDto dto) throws IOException;

    /**
     * Aktualizuje metadata existující skladby.
     *
     * @param trackId ID skladby k úpravě
     * @param dto     DTO s novými hodnotami
     * @return DTO aktualizované skladby
     * @throws EntityNotFoundException pokud skladba neexistuje
     */
    TrackDto updateTrack(Long trackId, TrackRequestDto dto);

    /**
     * Odstraní skladbu podle ID.
     *
     * @param trackId ID skladby k odstranění
     * @throws EntityNotFoundException pokud skladba neexistuje
     */
    void deleteTrack(Long trackId);

    /**
     * Vrátí seznam schválení producenta pro skladby, kde ještě nebylo potvrzeno.
     *
     * @return Seznam DTO informací o potřebě potvrzení (ProducerTrackInfoDto)
     */
    List<ProducerTrackInfoDto> getTrackApprovalsList();

    /**
     * Potvrdí, že producent souhlasí s prodejem skladby.
     *
     * @param trackId ID skladby k potvrzení
     * @return Aktualizovaný seznam všech potvrzení pro danou skladbu
     * @throws ProducerTrackInfoNotFoundException pokud není nalezena nevyřízená žádost
     */
    List<ProducerTrackInfoDto> confirmProducerAgreement(Long trackId);

    /**
     * Vrátí všechny skladby patřící danému producentovi.
     *
     * @param producerId ID producenta
     * @return Seznam DTO skladeb (TrackDto)
     */
    List<TrackDto> findAllByProducer(Long producerId);

    /**
     * Vrátí skladby daného zákazníka, zakoupené od konkrétního producenta.
     *
     * @param customerId ID zákazníka
     * @return Seznam DTO skladeb (TrackDto)
     */
    List<TrackDto> findCustomerPurchasedTracksForProducer(Long customerId);
}

