package com.cz.cvut.fel.instumentalshop.util.validator.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.Track;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.dto.track.in.ProducerShareRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.exception.*;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.repository.TrackRepository;
import com.cz.cvut.fel.instumentalshop.util.validator.TrackValidator;
import com.cz.cvut.fel.instumentalshop.util.validator.ValidatorBase;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrackValidatorImpl extends ValidatorBase implements TrackValidator {

    private final ProducerTrackInfoRepository producerTrackInfoRepository;
    private final PurchasedLicenceRepository purchasedLicenceRepository;
    private final TrackRepository trackRepository;

    @Override
    public void validateTrackCreationRequestWithSingleOwner(TrackRequestDto dto, Producer producer) {
        try {
            GenreType.valueOf(dto.getGenreType().name());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid genre type: " + dto.getGenreType());
        }
    }

    @Override
    public void validateTrackDeletionRequest(Long trackId, Long producerId) {
        Track t = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found"));
        if (!t.getProducer().getId().equals(producerId)) {
            throw new AccessDeniedException("Not the owner of this track");
        }
        if (purchasedLicenceRepository.existsByTrackId(trackId)) {
            throw new IllegalStateException("Cannot delete track with bought licences");
        }
    }

    @Override
    public void validateUpdateRequest(TrackRequestDto dto, Long trackId, Long producerId) {
        validateTrackDeletionRequest(trackId, producerId);
        try {
            GenreType.valueOf(dto.getGenreType().name());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid genre type: " + dto.getGenreType());
        }
    }

}
