package com.cvut.cz.fel.ear.instumentalshop.repository;

import com.cvut.cz.fel.ear.instumentalshop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByUsername(String username);

    Optional<Customer> findByUsername(String username);


}
