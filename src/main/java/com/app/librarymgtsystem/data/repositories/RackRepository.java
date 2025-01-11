package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Rack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RackRepository  extends MongoRepository<Rack, String> {

    Rack findByAuthorize(boolean authorize);
}
