package com.cvut.cz.fel.ear.instumentalshop.util.validator;

import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.dto.track.in.TrackRequestDto;

public interface TrackValidator {

    void validateTrackCreationRequestWithMultiOwners(TrackRequestDto requestDto, Producer leadProducer);

    void validateTrackCreationRequestWithSingleOwner(TrackRequestDto requestDto, Producer leadProducer);

    void validateTrackDeletionRequest(Long trackId, Long producerId);

    void validateUpdateRequest(TrackRequestDto requestDto, Long trackId, Long producerId);

}
