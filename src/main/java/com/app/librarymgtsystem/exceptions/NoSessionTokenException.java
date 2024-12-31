package com.app.librarymgtsystem.exceptions;

public class NoSessionTokenException extends RuntimeException {

    public NoSessionTokenException(String noSessionTokenException) {
        super(noSessionTokenException);
    }
}

