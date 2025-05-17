package com.cz.cvut.fel.instumentalshop.validator;

import com.cz.cvut.fel.instumentalshop.TestDataGenerator;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.exception.InvalidProfitPercentageException;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.util.validator.impl.TrackValidatorImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class TrackValidatorImplTest {

    @Mock
    private ProducerRepository producerRepository;

    @InjectMocks
    private TrackValidatorImpl trackValidator;

    @Test
    void validateTrackCreationRequestWithMultiOwners_ValidData_NoException() {
        TrackRequestDto requestDto = TestDataGenerator.createValidMultiOwnerTrackRequestDto(15, 15);
        Producer leadProducer = TestDataGenerator.createValidProducer(1L, "producer3");
        Producer expectedProducer1 = TestDataGenerator.createValidProducer(2L, "producer1");
        Producer expectedProducer2 = TestDataGenerator.createValidProducer(3L, "producer2");

        when(producerRepository.findByUsernameIn(anySet())).thenReturn(Set.of(expectedProducer1, expectedProducer2));
    }

    @Test
    void validateTrackCreationRequestWithMultiOwners_NonValidData_InvalidPercentageException() {
        TrackRequestDto requestDto = TestDataGenerator.createValidMultiOwnerTrackRequestDto(16, 15);
        Producer leadProducer = TestDataGenerator.createValidProducer(1L, "producer3");

    }

}