package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class   AddMemberResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private int accessLevel;
    private boolean sessionStatus = false;
    private String sessionToken;
    private String sessionEmail;

    private LocalDateTime creationDate = LocalDateTime.now();
    private String regMsg ;
}
