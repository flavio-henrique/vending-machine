package com.xpto.vendingmachine.persistence.repository;

import com.xpto.vendingmachine.persistence.model.TokenRevoke;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRevokeRepository extends CrudRepository<TokenRevoke, String> {}