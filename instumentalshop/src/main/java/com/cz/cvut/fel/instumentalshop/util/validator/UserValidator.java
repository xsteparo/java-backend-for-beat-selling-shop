package com.cz.cvut.fel.instumentalshop.util.validator;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.domain.Producer;
import com.cz.cvut.fel.instumentalshop.repository.UserRepository;

public interface UserValidator {

    void validateUserCreationRequest(UserRepository userRepository, String username);

    void validateProducerDeletionRequest(Producer producer, String username);

    void validateCustomerDeletionRequest(Customer customer);

}
