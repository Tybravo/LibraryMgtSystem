package com.app.librarymgtsystem.exceptions;

public class EmailCharNotIncludedException extends RuntimeException {
    public EmailCharNotIncludedException(String emailCharNotIncluded) {
        super(emailCharNotIncluded);
    }
}
