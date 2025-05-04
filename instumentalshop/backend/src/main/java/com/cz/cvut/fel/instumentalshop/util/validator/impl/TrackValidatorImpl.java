package com.cz.cvut.fel.instumentalshop.util.validator.impl;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.domain.enums.GenreType;
import com.cz.cvut.fel.instumentalshop.dto.track.in.ProducerShareRequestDto;
import com.cz.cvut.fel.instumentalshop.dto.track.in.TrackRequestDto;
import com.cz.cvut.fel.instumentalshop.exception.*;
import com.cz.cvut.fel.instumentalshop.repository.ProducerRepository;
import com.cz.cvut.fel.instumentalshop.repository.ProducerTrackInfoRepository;
import com.cz.cvut.fel.instumentalshop.repository.PurchasedLicenceRepository;
import com.cz.cvut.fel.instumentalshop.util.validator.TrackValidator;
import com.cz.cvut.fel.instumentalshop.util.validator.ValidatorBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrackValidatorImpl extends ValidatorBase implements TrackValidator {

    private final ProducerRepository producerRepository;

    private final ProducerTrackInfoRepository producerTrackInfoRepository;

    private final PurchasedLicenceRepository purchasedLicenceRepository;

    public void validateTrackCreationRequestWithMultiOwners(TrackRequestDto requestDto, Producer leadProducer) {
        validateOwnershipData(requestDto);
        validateGenre(String.valueOf(requestDto.getGenreType()));
        validateTotalPercentage(requestDto);
        checkIfProducerNamesExist(requestDto.getProducerShares(), leadProducer);
    }

    @Override
    public void validateTrackCreationRequestWithSingleOwner(TrackRequestDto requestDto, Producer leadProducer) {
        validateGenre(String.valueOf(requestDto.getGenreType()));
    }

    @Override
    public void validateTrackDeletionRequest(Long trackId, Long producerId) {
        validateIsLeadProducer(producerTrackInfoRepository, trackId, producerId);
        validateActiveLicences(trackId);
    }

    @Override
    public void validateUpdateRequest(TrackRequestDto requestDto, Long trackId, Long producerId) {
        validateIsLeadProducer(producerTrackInfoRepository, trackId, producerId);
        validateGenre(String.valueOf(requestDto.getGenreType()));
    }

    private void validateActiveLicences(Long trackId) {
        if (purchasedLicenceRepository.existsByTrackId(trackId)) {
            throw new DeleteRequestException("Cannot delete track with bought licences by customer");
        }
    }

    private void validateTotalPercentage(TrackRequestDto requestDto) {
        BigDecimal totalPercentage = calculateTotalPercentage(requestDto);
        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new InvalidProfitPercentageException("Total percentage must be 100%", totalPercentage.intValue());
        }
    }

    private void validateGenre(String genreTypeString) {
        try {
            GenreType.valueOf(genreTypeString.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid genre type: " + genreTypeString);
        }
    }

    private void checkIfProducerNamesExist(List<ProducerShareRequestDto> producerShareRequestDtos, Producer leadProducer) {
        Set<String> allNamesFromShareDto = extractProducerNamesFromProducerShareDto(producerShareRequestDtos);
        checkIfLeadProducerInsideShares(allNamesFromShareDto, leadProducer.getUsername());
        checkForDuplicateProducersInShares(producerShareRequestDtos);
        checkForNonExistentProducers(allNamesFromShareDto);
    }

    private void validateOwnershipData(TrackRequestDto requestDto) {
        if (isNotSingleOwnerAndNotMultiplyOwners(requestDto)) {
            throw new IllegalArgumentException("Lead producer percentage attribute and producer shares information are required");
        }

    }

    private boolean isNotSingleOwnerAndNotMultiplyOwners(TrackRequestDto requestDto) {
        return !isSingleOwner(requestDto) && !areMultiplyOwners(requestDto);
    }

    private boolean isSingleOwner(TrackRequestDto requestDto) {
        return requestDto.getMainProducerPercentage() == null &&
                (requestDto.getProducerShares() == null || requestDto.getProducerShares().isEmpty());
    }

    private boolean areMultiplyOwners(TrackRequestDto requestDto) {
        return requestDto.getMainProducerPercentage() != null && !requestDto.getProducerShares().isEmpty();
    }

    private BigDecimal calculateTotalPercentage(TrackRequestDto requestDto) {
        BigDecimal totalPercentage = BigDecimal.ZERO;

        totalPercentage = totalPercentage.add(BigDecimal.valueOf(requestDto.getMainProducerPercentage()));

        for (ProducerShareRequestDto producerShareRequestDto : requestDto.getProducerShares()) {
            totalPercentage = totalPercentage.add(BigDecimal.valueOf(producerShareRequestDto.getProfitPercentage()));
        }

        return totalPercentage;
    }

    private void checkIfLeadProducerInsideShares(Set<String> producerNames, String mainProducerName) {
        if (producerNames.contains(mainProducerName)) {
            throw new MainProducerFoundInShareListException("Requesting producer name found in share list", mainProducerName);
        }
    }

    private void checkForDuplicateProducersInShares(List<ProducerShareRequestDto> producerShareRequestDtos) {
        Set<String> seenNames = new HashSet<>();
        Set<String> duplicatedNames = producerShareRequestDtos.stream()
                .map(ProducerShareRequestDto::getProducerName)
                .filter(producerName -> !seenNames.add(producerName))
                .collect(Collectors.toSet());

        if (!duplicatedNames.isEmpty()) {
            throw new DuplicatedProducersException("Duplicate producer names found in share list", duplicatedNames);
        }
    }

    private void checkForNonExistentProducers(Set<String> producerNames) {
        Set<Producer> allOwnersFromRepository = producerRepository.findByUsernameIn(producerNames);
        Set<String> allOwnerNamesFromRepository = allOwnersFromRepository.stream()
                .map(Producer::getUsername)
                .collect(Collectors.toSet());

        if (!allOwnerNamesFromRepository.containsAll(producerNames)) {
            Set<String> notFoundNames = new HashSet<>(producerNames);
            notFoundNames.removeAll(allOwnerNamesFromRepository);
            throw new UserNotFoundException("Producers not found or duplicated", notFoundNames);
        }
    }

    private Set<String> extractProducerNamesFromProducerShareDto(List<ProducerShareRequestDto> producerShareRequestDtos) {
        return producerShareRequestDtos.stream()
                .map(ProducerShareRequestDto::getProducerName)
                .collect(Collectors.toCollection(HashSet::new));
    }

}
