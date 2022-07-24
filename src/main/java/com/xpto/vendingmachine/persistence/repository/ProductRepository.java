package com.xpto.vendingmachine.persistence.repository;


import com.xpto.vendingmachine.persistence.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findById(Long id);
    List<Product> findAllByUserAuthUsername(String username);
    Optional<Product> findByIdAndUserAuthUsername(Long id, String username);
}