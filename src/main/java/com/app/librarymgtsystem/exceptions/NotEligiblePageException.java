package com.app.librarymgtsystem.exceptions;

public class NotEligiblePageException extends RuntimeException {

    public NotEligiblePageException(String notEligiblePage) {
        super(notEligiblePage);
    }
}
