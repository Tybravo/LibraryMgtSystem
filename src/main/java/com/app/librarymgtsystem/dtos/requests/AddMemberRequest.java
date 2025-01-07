package com.app.librarymgtsystem.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AddMemberRequest {

    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;

    @JsonIgnore
    private String statusMsg;
    @JsonIgnore
    private String regMsg;
    @JsonIgnore
    private int accessLevel;
    @JsonIgnore
    private boolean sessionStatus = false;
    @JsonIgnore
    private String sessionToken;
    @JsonIgnore
    private String sessionEmail;
    @JsonIgnore
    private LocalDateTime creationDate = LocalDateTime.now();
}

