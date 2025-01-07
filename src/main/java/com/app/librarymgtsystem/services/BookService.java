package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.dtos.responses.*;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.requests.UpdateBookRequest;

import java.util.Optional;

public interface BookService {

    boolean findMemberSession();

    boolean findMemberAccessLevel(int accessLevel);

    AddBookResponse addBook(AddBookRequest addBookRequest);

    AddBookResponse addBookWithShelve(AddBookRequest addBookRequest, AddShelveRequest addShelveRequest);

    Optional<AddBookRequest> findBookById(String findBook);

    AddShelveResponse addShelveWithBookId(AddShelveRequest addShelveRequest);

    boolean bookAlreadyExist(AddBookRequest addBookRequest);

    boolean isbnAlreadyExist(AddBookRequest addBookRequest);

    Book findBookByTitle(String title);

    Book findBookByAuthor(String author);

    Book findBookByIsbn(String isbn);

    void bookCannotBeEmpty(AddBookRequest addEmptyBook);

    UpdateBookResponse updateBookByTitle(UpdateBookRequest updateBookRequest, String title);

    ViewBookResponse viewBookByAll(int page, int size);

    ViewBookResponse viewBookByAllForMembers(int page, int size);

    ViewBookResponse viewBookByTitle(String title);

    ViewBookResponse viewBookByTitleForMembers(String title);

    DeleteBookResponse deleteBookByTitle(String title);

}



