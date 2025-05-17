package com.cz.cvut.fel.instumentalshop.service.newTests;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.mapper.ProducerTrackInfoMapper;
import com.cz.cvut.fel.instumentalshop.dto.mapper.TrackMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.ProducerTrackInfoDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.exception.ProducerTrackInfoNotFoundException;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.impl.TrackServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpRange;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplTest {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ProducerTrackInfoRepository producerTrackInfoRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackMapper trackMapper;
    @Mock
    private ProducerTrackInfoMapper producerTrackInfoMapper;

    @InjectMocks
    private TrackServiceImpl trackService;

//    @BeforeEach
//    void setUp() {
//        // Manually initialize upload path for resource loading tests
//        trackService.tracksUploadPath = null; // not used in tested methods
//    }

    @Test
    void testBuildRegion_NoRanges() throws IOException {
        Resource resource = new ByteArrayResource(new byte[500]);
        ResourceRegion region = trackService.buildRegion(resource, Collections.emptyList(), 500);
        assertEquals(0, region.getPosition());
        assertEquals(500, region.getCount());
    }

    @Test
    void testBuildRegion_WithRange() throws IOException {
        byte[] data = new byte[2000];
        Resource resource = new ByteArrayResource(data);
        List<HttpRange> ranges = HttpRange.parseRanges("bytes=100-600");
        ResourceRegion region = trackService.buildRegion(resource, ranges, 2000);
        assertEquals(100, region.getPosition());
        assertEquals(501, region.getCount());
    }

    @Test
    void testFindById_Success() {
        Track track = new Track();
        track.setId(1L);
        TrackDto dto = new TrackDto();
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(trackMapper.toResponseDto(track)).thenReturn(dto);
        TrackDto result = trackService.findById(1L);
        assertSame(dto, result);
    }

    @Test
    void testFindById_NotFound() {
        when(trackRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trackService.findById(42L));
    }

    @Test
    void testGetTrackApprovalsList() {
        Producer producer = new Producer(); producer.setId(5L);
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        ProducerTrackInfo info = new ProducerTrackInfo();
        when(producerTrackInfoRepository.findByProducerIdAndAgreedForSelling(5L, false))
                .thenReturn(List.of(info));
        ProducerTrackInfoDto dto = new ProducerTrackInfoDto();
        when(producerTrackInfoMapper.toResponseDto(info)).thenReturn(dto);
        List<ProducerTrackInfoDto> result = trackService.getTrackApprovalsList();
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }


    @Test
    void testFindAllByProducer() {
        Track track = new Track();
        TrackDto dto = new TrackDto();
        when(trackRepository.findTracksByProducerId(3L)).thenReturn(List.of(track));
        when(trackMapper.toResponseDto(track)).thenReturn(dto);
        List<TrackDto> result = trackService.findAllByProducer(3L);
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void testFindCustomerPurchasedTracksForProducer() {
        Producer producer = new Producer(); producer.setId(9L);
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(producer);
        Track track = new Track();
        TrackDto dto = new TrackDto();
        when(trackRepository.findCustomerBoughtTracksForProducer(2L, 9L)).thenReturn(List.of(track));
        when(trackMapper.toResponseDto(track)).thenReturn(dto);
        List<TrackDto> result = trackService.findCustomerPurchasedTracksForProducer(2L);
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void testFindAll_FilteringAndPaging() {
        TrackFilterDto filter = TrackFilterDto.builder()
                .tab("new").search("x").genre("POP")
                .tempoRange("80-120").key("C").sort("rating").build();

        Track track = new Track();
        TrackDto dto = new TrackDto();

        PageRequest pageReq = PageRequest.of(0, 10);
        Page<Track> page = new PageImpl<>(List.of(track), pageReq, 1);

        when(trackRepository.findAll(any(Specification.class), eq(pageReq)))
                .thenReturn(page);
        when(trackMapper.toResponseDto(track)).thenReturn(dto);

        Page<TrackDto> result = trackService.findAll(filter, pageReq);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
    }
}
