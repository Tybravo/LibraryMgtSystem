package com.app.librarymgtsystem.exceptions;

public class BookCannotBeEmptyException extends RuntimeException {

    public BookCannotBeEmptyException(String bookCannotBeEmptyException) {
        super(bookCannotBeEmptyException);
    }
}
