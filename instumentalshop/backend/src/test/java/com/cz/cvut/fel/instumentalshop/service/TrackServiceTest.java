//package com.cz.cvut.fel.instumentalshop.service;
//
//import com.cz.cvut.fel.instumentalshop.TestDataGenerator;
//import com.cz.cvut.fel.instumentalshop.domain.Producer;
//import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
//import com.cz.cvut.fel.instumentalshop.domain.Track;
//import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
//import com.cz.cvut.fel.instumentalshop.dto.mapper.TrackMapper;
//import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
//import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
//import com.cz.cvut.fel.instumentalshop.exception.DuplicatedProducersException;
//import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
//import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
//import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
//import com.cz.cvut.fel.instumentalshop.service.impl.TrackServiceImpl;
//import com.cz.cvut.fel.instumentalshop.util.validator.TrackValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class TrackServiceTest {
//
//    @Mock
//    private TrackRepository trackRepository;
//
//    @Mock
//    private ProducerRepository producerRepository;
//
//    @Mock
//    private ProducerTrackInfoRepository producerTrackInfoRepository;
//
//    @Mock
//    private AuthenticationService authenticationService;
//
//    @Mock
//    private TrackValidator trackValidator;
//
//    @Mock
//    private TrackMapper trackMapper;
//
//    @InjectMocks
//    private TrackServiceImpl trackService;
//
//    private Producer leadProducer;
//
//    private Producer producer1;
//
//    private Producer producer2;
//
//    private List<ProducerTrackInfo> producerTrackInfos;
//
//    private TrackRequestDto singleOwnerRequestDto;
//
//    private TrackRequestDto multipleOwnerRequestDto;
//
//    private TrackRequestDto invalidCreationRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        leadProducer = TestDataGenerator.createProducer(1L, "LeadProducer");
//        producer1 = TestDataGenerator.createProducer(2L, "Producer1");
//        producer2 = TestDataGenerator.createProducer(3L, "Producer2");
//        producerTrackInfos = List.of(
//                TestDataGenerator.createProducerTrackInfo(leadProducer, null, new BigDecimal("75")),
//                TestDataGenerator.createProducerTrackInfo(producer1, null, new BigDecimal("15")),
//                TestDataGenerator.createProducerTrackInfo(producer2, null, new BigDecimal("15"))
//        );
//
//        singleOwnerRequestDto = TestDataGenerator.createTrackRequestDto("SingleOwnerTrack", GenreType.HIPHOP, 120, null, null);
//        multipleOwnerRequestDto = TestDataGenerator.createTrackRequestDto("MultiOwnerTrack", GenreType.HIPHOP, 160, 70, List.of(
//                TestDataGenerator.createProducerShareRequestDto("Producer1", 15),
//                TestDataGenerator.createProducerShareRequestDto("Producer2", 15)
//                )
//        );
//
//        invalidCreationRequestDto = TestDataGenerator.createTrackRequestDto("FailedTrack", GenreType.HIPHOP, 140, null, List.of(
//                TestDataGenerator.createProducerShareRequestDto("Producer1", 500)
//                )
//        );
//
//    }
//
//    @Test
//    void createTrackWithSingleOwner() {
//        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(leadProducer);
//
//        Track savedTrack = new Track();
//        savedTrack.setId(1L);
//        savedTrack.setName(singleOwnerRequestDto.getName());
//        ProducerTrackInfo savedProducerTrackInfo = TestDataGenerator.createProducerTrackInfo(leadProducer, savedTrack, new BigDecimal("100"));;
//
//        savedProducerTrackInfo.setTrack(savedTrack);
//        savedProducerTrackInfo.setProducer(leadProducer);
//
//        when(producerTrackInfoRepository.save(any())).thenReturn(savedProducerTrackInfo);
//        when(trackMapper.toResponseWithSingleOwnerDto(any())).thenReturn(TrackDto.builder().build());
//
//        TrackDto result = trackService.createTrack(singleOwnerRequestDto);
//
//        assertNotNull(result);
//        verify(trackValidator).validateTrackCreationRequestWithSingleOwner(singleOwnerRequestDto, leadProducer);
//        verify(producerTrackInfoRepository).save(any(ProducerTrackInfo.class));
//        verify(trackMapper).toResponseWithSingleOwnerDto(any(Track.class));
//    }
//
//    @Test
//    void createTrackWithMultipleOwners() {
//        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(leadProducer);
//
//        Track savedTrack = new Track();
//        savedTrack.setId(1L);
//        savedTrack.setName(singleOwnerRequestDto.getName());
//        savedTrack.setProducerTrackInfos(producerTrackInfos);
//
//        when(trackRepository.save(any(Track.class))).thenReturn(savedTrack);
//        when(producerRepository.findProducerByUsername(multipleOwnerRequestDto.getProducerShares().get(0).getProducerName())).thenReturn(Optional.ofNullable(producer1));
//        when(producerRepository.findProducerByUsername(multipleOwnerRequestDto.getProducerShares().get(1).getProducerName())).thenReturn(Optional.ofNullable(producer2));
//        when(trackMapper.toResponseWithMultipleOwnersDto(savedTrack)).thenReturn(TrackDto.builder().build());
//
//        TrackDto result = trackService.createTrack(multipleOwnerRequestDto);
//
//        assertNotNull(result);
//        verify(trackValidator).validateTrackCreationRequestWithMultiOwners(multipleOwnerRequestDto, leadProducer);
//        verify(trackRepository).save(any(Track.class));
//        verify(trackMapper).toResponseWithMultipleOwnersDto(savedTrack);
//    }
//
//    @Test
//    void createTrack_Throws_Duplicated_Producers(){
//        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(leadProducer);
//
//        doThrow(DuplicatedProducersException.class)
//                .when(trackValidator)
//                .validateTrackCreationRequestWithMultiOwners(invalidCreationRequestDto, leadProducer);
//
//        assertThrows(DuplicatedProducersException.class, () -> {
//            trackService.createTrack(invalidCreationRequestDto);
//        });
//
//        verify(trackValidator).validateTrackCreationRequestWithMultiOwners(invalidCreationRequestDto, leadProducer);
//
//    }
//
//
//}