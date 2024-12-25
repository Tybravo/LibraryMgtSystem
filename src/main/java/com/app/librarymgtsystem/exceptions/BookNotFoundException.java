package com.app.librarymgtsystem.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String bookNotFoundException) {
        super(bookNotFoundException);
    }
}
