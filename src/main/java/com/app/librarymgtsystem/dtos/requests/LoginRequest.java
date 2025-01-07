package com.app.librarymgtsystem.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LoginRequest {
//    @JsonIgnore
//    private String id;

    private String email;
    private String password;

//    @JsonIgnore
//    private int accessLevel;
//    @JsonIgnore
//    private Boolean sessionStatus = false;
//    @JsonIgnore
//    private String sessionToken;
//    @JsonIgnore
//    private String sessionEmail;

}
