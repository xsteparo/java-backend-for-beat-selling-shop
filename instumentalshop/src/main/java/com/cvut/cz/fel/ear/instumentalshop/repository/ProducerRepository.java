package com.cvut.cz.fel.ear.instumentalshop.repository;

import com.cvut.cz.fel.ear.instumentalshop.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {

    Optional<Producer> findProducerByUsername(String username);

    Set<Producer> findByUsernameIn(Set<String> usernames);

}
