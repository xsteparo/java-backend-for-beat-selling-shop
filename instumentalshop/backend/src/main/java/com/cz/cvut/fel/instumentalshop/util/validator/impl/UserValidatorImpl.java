package com.cz.cvut.fel.instumentalshop.util.validator.impl;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.exception.DeleteRequestException;
import com.cz.cvut.fel.instumentalshop.exception.UserAlreadyExistsException;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;
import com.cz.cvut.fel.instumentalshop.util.validator.UserValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserValidatorImpl implements UserValidator {

    @Override
    public void validateUserCreationRequest(UserRepository userRepository, String username) {
        checkIfProducerExists(userRepository, username);
    }

    @Override
    public void validateProducerDeletionRequest(Producer producer, String username) {
        checkIfProducerCanDeleteHimself(producer, username);
    }

    @Override
    public void validateCustomerDeletionRequest(Customer customer) {
        if (hasCustomerBalance(customer)) {
            throw new DeleteRequestException("Cannot delete customer with non-zero balance");
        }

        if (hasCustomerOrders(customer)) {
            throw new DeleteRequestException("Cannot delete customer with purchased tracks");
        }
    }

    private boolean hasCustomerBalance(Customer customer) {
        return customer.getBalance().compareTo(BigDecimal.ZERO) != 0;
    }

    private boolean hasCustomerOrders(Customer customer) {
        return !customer.getOrders().isEmpty();
    }

    private void checkIfProducerCanDeleteHimself(Producer producer, String username) {
        if (hasProducerTracks(producer) || hasProducerSoldLicences(producer) || hasProducerSalary(producer)) {
            throw new DeleteRequestException("Producer with username " + username + " cannot be deleted because he has tracks, sold licences or salary");
        }
    }

    private void checkIfProducerExists(UserRepository userRepository, String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("User with username " + username + " already exists");
        }
    }

    private boolean hasProducerTracks(Producer producer) {
        return producer.getTracks() != null && !producer.getTracks().isEmpty();
    }

    private boolean hasProducerSoldLicences(Producer producer) {
        return producer.getSoldLicences() != null && !producer.getSoldLicences().isEmpty();
    }

    private boolean hasProducerSalary(Producer producer) {
        return producer.getSalary() != null && producer.getSalary().compareTo(BigDecimal.ZERO) != 0;
    }
}
