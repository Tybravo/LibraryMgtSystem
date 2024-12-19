package com.app.librarymgtsystem.exceptions;

public class EmailCannotBeEmptyException extends RuntimeException {

    public EmailCannotBeEmptyException(String emailCannotBeEmpty) {
        super(emailCannotBeEmpty);
    }
}
