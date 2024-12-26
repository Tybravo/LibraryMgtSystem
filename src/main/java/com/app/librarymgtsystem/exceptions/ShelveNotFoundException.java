package com.app.librarymgtsystem.exceptions;

public class ShelveNotFoundException extends RuntimeException {
    public ShelveNotFoundException(String shelveNotFoundException) {
        super(shelveNotFoundException);
    }
}
