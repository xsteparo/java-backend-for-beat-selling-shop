package com.cz.cvut.fel.instumentalshop.util.validator;

import com.cz.cvut.fel.instumentalshop.domain.ProducerTrackInfo;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

public abstract class ValidatorBase {

    protected void validateIsLeadProducer(ProducerTrackInfoRepository producerTrackInfoRepository, Long trackId, Long producerId) {
        ProducerTrackInfo trackInfo = producerTrackInfoRepository
                .findByTrackIdAndProducerId(trackId, producerId)
                .orElseThrow(() -> new EntityNotFoundException("Track is not found"));

        if (!trackInfo.getOwnsPublishingTrack()) {
            throw new AccessDeniedException("Only lead producer can perform this operation");
        }
    }
}
