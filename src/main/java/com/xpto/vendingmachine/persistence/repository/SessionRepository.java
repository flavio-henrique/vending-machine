package com.xpto.vendingmachine.persistence.repository;

import com.xpto.vendingmachine.persistence.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {}