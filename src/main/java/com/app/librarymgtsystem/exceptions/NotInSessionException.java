package com.app.librarymgtsystem.exceptions;

public class NotInSessionException extends RuntimeException {

    public NotInSessionException(String alreadyInSessionException) {
        super(alreadyInSessionException);
    }
}
