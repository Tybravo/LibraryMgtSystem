package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.dtos.responses.*;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.requests.UpdateBookRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface BookService {

    String getSessionEmail(HttpServletRequest request);

    boolean findMemberSession(HttpServletRequest request);

    boolean findMemberAccessLevel(int accessLevel);

    AddBookResponse addBook(AddBookRequest addBookRequest, HttpServletRequest request);

    AddBookResponse addBookWithShelve(AddBookRequest addBookRequest, AddShelveRequest addShelveRequest, HttpServletRequest request);

    Optional<Book> findBookById(String title);

    AddShelveResponse addShelveWithBookId(AddShelveRequest addShelveRequest);

    boolean bookAlreadyExist(AddBookRequest addBookRequest);

    //boolean authorAlreadyExist(AddBookRequest addBookRequest);

    boolean isbnAlreadyExist(AddBookRequest addBookRequest);

    Book findBookByTitle(String title);

    Book findBookByAuthor(String author);

    Book findBookByIsbn(String isbn);

    void bookCannotBeEmpty(AddBookRequest addBookRequest);

    UpdateBookResponse updateBookByTitle(UpdateBookRequest updateBookRequest, String title, HttpServletRequest request);

    ViewBookResponse viewBookByAll(int page, int size, HttpServletRequest request);

    ViewBookResponse viewBookByAllForMembers(int page, int size, HttpServletRequest request);

    ViewBookResponse viewBookByTitle(String title, HttpServletRequest request);

    ViewBookResponse viewBookByTitleForMembers(String title, HttpServletRequest request);

    DeleteBookResponse deleteBookByTitle(String title, HttpServletRequest request);

}



