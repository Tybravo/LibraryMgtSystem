package com.app.librarymgtsystem.exceptions;

public class BookExistException extends RuntimeException {

    public BookExistException(String bookExistException) {
        super(bookExistException);
    }
}
