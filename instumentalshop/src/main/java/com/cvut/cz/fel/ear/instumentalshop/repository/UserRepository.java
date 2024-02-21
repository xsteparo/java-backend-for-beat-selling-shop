package com.cvut.cz.fel.ear.instumentalshop.repository;

import com.cvut.cz.fel.ear.instumentalshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String email);

    boolean existsByUsername(String username);
}
