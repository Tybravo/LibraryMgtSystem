package com.app.librarymgtsystem.exceptions;

public class EmailExistException extends RuntimeException {

    public EmailExistException(String emailExist) {
        super(emailExist);

    }
}

