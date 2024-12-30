package com.app.librarymgtsystem.exceptions;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException(String emailNotFoundException) {
        super(emailNotFoundException);
    }
}
