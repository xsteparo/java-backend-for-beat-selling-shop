package com.cvut.cz.fel.ear.instumentalshop.util.validator;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import com.cvut.cz.fel.ear.instumentalshop.repository.ProducerRepository;
import com.cvut.cz.fel.ear.instumentalshop.repository.UserRepository;

public interface UserValidator {

    void validateUserCreationRequest(UserRepository userRepository, String username);

    void validateProducerDeletionRequest(Producer producer, String username);

    void validateCustomerDeletionRequest(Customer customer);

}
