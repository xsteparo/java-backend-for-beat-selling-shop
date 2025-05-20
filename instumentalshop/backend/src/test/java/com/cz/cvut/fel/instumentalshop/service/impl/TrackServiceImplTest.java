package com.cz.cvut.fel.instumentalshop.service.impl;


import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.dto.mapper.TrackMapper;
import com.cz.cvut.fel.instumentalshop.dto.newDto.TrackFilterDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.out.TrackDto;
import com.cz.cvut.fel.instumentalshop.repository.LicenceTemplateRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.util.validator.TrackValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrackServiceImplTest {

    @InjectMocks
    private TrackServiceImpl service;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackMapper trackMapper;
    @Mock
    private TrackValidator trackValidator;
    @Mock
    private LicenceTemplateRepository tplRepo;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Inject private upload directory and initialize
        ReflectionTestUtils.setField(service, "tracksUploadDir", "target/uploads");
        service.init();
    }

    @Test
    void findById_existingId_returnsDto() {
        Track track = new Track();
        track.setId(1L);
        when(trackRepository.findById(1L)).thenReturn(java.util.Optional.of(track));
        TrackDto dto = new TrackDto();
        when(trackMapper.toResponseDto(track)).thenReturn(dto);

        TrackDto result = service.findById(1L);

        assertThat(result).isSameAs(dto);
        verify(trackRepository).findById(1L);
    }

    @Test
    void findById_notFound_throws() {
        when(trackRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.findById(2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Track not found");
    }

    @Test
    void incrementPlays_incrementsCount() {
        Track track = new Track(); track.setPlays(5);
        when(trackRepository.findById(3L)).thenReturn(java.util.Optional.of(track));

        service.incrementPlays(3L);

        assertThat(track.getPlays()).isEqualTo(6);
        verify(trackRepository).save(track);
    }

    @Test
    void incrementPlays_notFound_throws() {
        when(trackRepository.findById(4L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.incrementPlays(4L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getTopTracks_returnsMappedDtos_withCorrectSortAndLimit() {
        // Prepare sample tracks
        Track t1 = new Track(); t1.setRating(BigDecimal.valueOf(10));
        Track t2 = new Track(); t2.setRating(BigDecimal.valueOf(20));
        List<Track> tracks = List.of(t2, t1);
        Page<Track> page = new PageImpl<>(tracks);
        when(trackRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(trackMapper.toResponseDto(t2)).thenReturn(new TrackDto());
        when(trackMapper.toResponseDto(t1)).thenReturn(new TrackDto());

        int limit = 2;
        List<TrackDto> result = service.getTopTracks(limit);

        // Capture the pageable argument
        verify(trackRepository).findAll(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        Sort.Order order = pageable.getSort().getOrderFor("rating");

        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(pageable.getPageSize()).isEqualTo(limit);

        assertThat(result).hasSize(2);
    }



    @Test
    void updateTrack_forbiddenWhenDifferentProducer() {
        Producer owner = new Producer(); owner.setId(1L);
        Producer other = new Producer(); other.setId(2L);
        Track track = new Track(); track.setProducer(owner);
        when(authenticationService.getRequestingProducerFromSecurityContext()).thenReturn(other);
        when(trackRepository.findById(5L)).thenReturn(java.util.Optional.of(track));
        TrackRequestDto dto = TrackRequestDto.builder().name("test").genreType(null).bpm(100).nonExclusiveFile(null).build();

        assertThatThrownBy(() -> service.updateTrack(5L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void loadAsResource_notFound_throws() throws IOException {
        when(trackRepository.findFilePathById(6L)).thenReturn(null);

        assertThatThrownBy(() -> service.loadAsResource(6L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Track not found");
    }

}