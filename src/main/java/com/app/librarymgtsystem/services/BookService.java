package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.dtos.responses.ViewBookResponse;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.requests.UpdateBookRequest;
import com.app.librarymgtsystem.dtos.responses.UpdateBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddShelveResponse;

import java.util.Optional;

public interface BookService {
    boolean findMemberSession(Boolean sessionStatus);

    boolean findMemberAccessLevel(int accessLevel);

    AddBookResponse addBook(AddBookRequest addBookRequest);

    String getMemberEmail();

    Optional<AddBookRequest> findBookById(String findBook);

    AddShelveResponse addShelveWithBookId(AddShelveRequest addShelveRequest);

    boolean bookAlreadyExist(AddBookRequest addBookRequest);

    boolean isbnAlreadyExist(AddBookRequest addBookRequest);

    Book findBookByTitle(String title);

    Book findBookByAuthor(String author);

    Book findBookByIsbn(String isbn);

    void bookCannotBeEmpty(AddBookRequest addEmptyBook);

    UpdateBookResponse updateBookByTitle(UpdateBookRequest updateBookRequest);

    ViewBookResponse viewBookByAll(int page, int size);

    ViewBookResponse viewBookByTitle(String title);

    void deleteBookByTitle(String title);

    Shelve findShelveByCategory(ShelveType category);


}



