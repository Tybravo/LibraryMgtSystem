package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Shelve;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelveRepository extends MongoRepository<Shelve, String> {

}