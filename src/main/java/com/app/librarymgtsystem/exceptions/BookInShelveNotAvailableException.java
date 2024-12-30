package com.app.librarymgtsystem.exceptions;

public class BookInShelveNotAvailableException extends RuntimeException {

    public BookInShelveNotAvailableException(String shelveNotAvailableException) {
        super(shelveNotAvailableException);
    }
}
