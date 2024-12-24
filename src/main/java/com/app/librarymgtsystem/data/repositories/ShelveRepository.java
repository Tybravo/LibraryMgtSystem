package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelveRepository extends MongoRepository<Shelve, String> {

    Optional<Shelve> findByBookId(String bookId);
    Optional<Shelve>findShelveByCategory(ShelveType category);
}