package com.cz.cvut.fel.instumentalshop;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateCreationRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.licence.out.TemplateResponseDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.ProducerShareRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDataGenerator {

    public static Customer createCustomer(Long id, BigDecimal balance) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setBalance(balance);
        return customer;
    }

    public static Track createTrack(Long id, String name) {
        Track track = new Track();
        track.setId(id);
        track.setName(name);
        return track;
    }

    public static Track createTrack(Long id, boolean isExclusiveBought, boolean isAllProducersAgreedForSelling) {
        Track track = mock(Track.class);
        when(track.getId()).thenReturn(id);
        when(track.isExclusiveBought()).thenReturn(isExclusiveBought);
        when(track.isAllProducersAgreedForSelling()).thenReturn(isAllProducersAgreedForSelling);
        return track;
    }

    public static LicenceTemplate createLicenceTemplate(Long id, Track track, BigDecimal price, LicenceType licenceType, int validityDays) {
        LicenceTemplate licenceTemplate = new LicenceTemplate();
        licenceTemplate.setId(id);
        licenceTemplate.setTrack(track);
        licenceTemplate.setPrice(price);
        licenceTemplate.setLicenceType(licenceType);
        licenceTemplate.setValidityPeriodDays(validityDays);
        return licenceTemplate;
    }

    public static LicenceTemplate createLicenceTemplate(BigDecimal price, LicenceType licenceType) {
        LicenceTemplate licenceTemplate = mock(LicenceTemplate.class);
        when(licenceTemplate.getPrice()).thenReturn(price);
        when(licenceTemplate.getLicenceType()).thenReturn(licenceType);
        return licenceTemplate;
    }

    public static Producer createProducer(Long id, String username) {
        Producer producer = new Producer();
        producer.setId(id);
        producer.setUsername(username);
        producer.setSoldLicences(new ArrayList<>());
        return producer;
    }

    public static Producer createProducer(Long id) {
        Producer producer = mock(Producer.class);
        when(producer.getId()).thenReturn(id);
        return producer;
    }

    public static Customer createCustomerWithBalance(BigDecimal balance) {
        Customer customer = mock(Customer.class);
        when(customer.getBalance()).thenReturn(balance);
        return customer;
    }

    public static ProducerTrackInfo createProducerTrackInfo(Producer producer, Track track, BigDecimal profitPercentage) {
        ProducerTrackInfo producerTrackInfo = new ProducerTrackInfo();
        producerTrackInfo.setProducer(producer);
        producerTrackInfo.setTrack(track);
        producerTrackInfo.setProfitPercentage(profitPercentage);
        return producerTrackInfo;
    }

    public static ProducerTrackInfo createProducerTrackInfo(boolean ownsPublishingTrack) {
        ProducerTrackInfo trackInfo = mock(ProducerTrackInfo.class);
        when(trackInfo.getOwnsPublishingTrack()).thenReturn(ownsPublishingTrack);
        return trackInfo;
    }

    public static TrackRequestDto createTrackRequestDto(String name, GenreType genreType, int bpm, Integer mainProducerPercentage, List<ProducerShareRequestDto> producerShares) {
        return TrackRequestDto.builder()
                .name(name)
                .genreType(genreType)
                .bpm(bpm)
                .mainProducerPercentage(mainProducerPercentage)
                .producerShares(producerShares)
                .build();
    }

    public static ProducerShareRequestDto createProducerShareRequestDto(String producerName, int profitPercentage) {
        return ProducerShareRequestDto.builder()
                .producerName(producerName)
                .profitPercentage(profitPercentage)
                .build();
    }

    public static TemplateCreationRequestDto createLicenceRequestDto(LicenceType type, BigDecimal price) {
        TemplateCreationRequestDto requestDto = new TemplateCreationRequestDto();
        requestDto.setLicenceType(type);
        requestDto.setPrice(price);
        // Добавьте другие необходимые параметры
        return requestDto;
    }

    public static TemplateResponseDto createResponseDto() {
        return new TemplateResponseDto();
    }

    public static TrackRequestDto createValidMultiOwnerTrackRequestDto(Integer producerPercentage1, Integer producerPercentage2) {
        ProducerShareRequestDto producerShareRequestDto1 = ProducerShareRequestDto.builder()
                .producerName("producer1")
                .profitPercentage(producerPercentage1)
                .build();
        ProducerShareRequestDto producerShareRequestDto2 = ProducerShareRequestDto.builder()
                .producerName("producer2")
                .profitPercentage(producerPercentage2)
                .build();

        return TrackRequestDto.builder()
                .name("EXAMPLE 1")
                .bpm(120)
                .genreType(GenreType.HIPHOP)
                .mainProducerPercentage(70)
                .producerShares(List.of(producerShareRequestDto1, producerShareRequestDto2))
                .build();
    }

    public static Producer createValidProducer(Long id, String username) {
        return Producer.builder()
                .id(id)
                .username(username)
                .build();
    }

}