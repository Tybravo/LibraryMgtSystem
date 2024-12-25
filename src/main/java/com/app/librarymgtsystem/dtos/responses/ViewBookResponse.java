package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewBookResponse {
    private String id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;

    private boolean sessionStatus;
    private int accessLevel;
    private String viewBookMsg;
    private LocalDateTime creationDate = LocalDateTime.now();
}
