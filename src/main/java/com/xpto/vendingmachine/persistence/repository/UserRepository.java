package com.xpto.vendingmachine.persistence.repository;

import com.xpto.vendingmachine.persistence.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAuth, String> {
    Optional<UserAuth> searchByUsername(String username);

    UserAuth findByUsername(String username);
}