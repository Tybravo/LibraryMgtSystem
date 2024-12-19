package com.app.librarymgtsystem.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Member {
    @Id
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String statusMsg;
    private String regMsg;
    private String logMsg;
    private int accessLevel;
    private boolean sessionStatus = false;
    private LocalDateTime creationDate = LocalDateTime.now();
}
