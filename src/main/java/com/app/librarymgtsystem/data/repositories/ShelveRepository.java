package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Shelve;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ShelveRepository extends MongoRepository<Shelve, String> {
}