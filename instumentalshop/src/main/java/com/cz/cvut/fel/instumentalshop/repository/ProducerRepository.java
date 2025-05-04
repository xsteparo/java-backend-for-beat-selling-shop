package com.cz.cvut.fel.instumentalshop.repository;

import com.cz.cvut.fel.instumentalshop.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {

    Optional<Producer> findProducerByUsername(String username);

    Set<Producer> findByUsernameIn(Set<String> usernames);

    @Query("SELECT p FROM Producer p ORDER BY p.rating DESC")
    List<Producer> findTopProducers(Pageable pageable);

}
