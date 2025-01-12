package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Rack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RackRepository  extends MongoRepository<Rack, String> {

    Optional<Rack> findByAuthorize(boolean authorize);

    Optional<Rack> findByMemberId(String memberId);
}

