package com.cz.cvut.fel.instumentalshop.util.validator;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;

public interface TrackValidator {

    void validateTrackCreationRequestWithSingleOwner(TrackRequestDto requestDto, Producer leadProducer);

    void validateTrackDeletionRequest(Long trackId, Long producerId);

    void validateUpdateRequest(TrackRequestDto requestDto, Long trackId, Long producerId);

}
