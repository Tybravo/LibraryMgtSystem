package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;

@Data
public class LoginResponse {
    private String id;
    private String email;
    private String password;
    private String regMsg;
    private String logMsg;
    private int accessLevel;

    private String sessionToken;
    private String sessionEmail;
    private Boolean sessionStatus;
}

