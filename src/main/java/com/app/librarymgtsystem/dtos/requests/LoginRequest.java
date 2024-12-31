package com.app.librarymgtsystem.dtos.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String id;
    private String email;
    private String password;
    private int accessLevel;
    private Boolean sessionStatus = false;
    private String sessionToken;
    private String sessionEmail;


}
