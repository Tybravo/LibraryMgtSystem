package com.app.librarymgtsystem.exceptions;

public class LoginMemberNotFoundException extends RuntimeException {
    public LoginMemberNotFoundException(String LoginMemberNotFoundE) {
        super(LoginMemberNotFoundE);
    }
}
