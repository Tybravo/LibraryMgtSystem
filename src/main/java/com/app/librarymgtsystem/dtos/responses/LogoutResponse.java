package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;

@Data
public class LogoutResponse {
    private String id;
    private String token;
    private String email;

    private String LogoutMsg;
}
