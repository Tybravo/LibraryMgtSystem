package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.dtos.requests.AddBookRequest;

public interface BookService {
    boolean findMemberSession(Boolean sessionStatus);

    Boolean findMemberAccessLevel(int accessLevel);

    AddBookRequest addBook(AddBookRequest addBookRequest);

    String getMemberEmail();
}

