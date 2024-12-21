package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository  extends MongoRepository<Book, String> {

}
