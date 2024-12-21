package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.responses.AddShelveResponse;

import java.util.Optional;

public interface BookService {
    boolean findMemberSession(Boolean sessionStatus);

    boolean findMemberAccessLevel(int accessLevel);

    AddBookRequest addBook(AddBookRequest addBookRequest);

    String getMemberEmail();

    Optional<AddBookRequest> findBookId(String findBook);

    AddShelveResponse addShelveBookId(AddShelveRequest addShelveRequest);
}

