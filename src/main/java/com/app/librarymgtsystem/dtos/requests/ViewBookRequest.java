package com.app.librarymgtsystem.dtos.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewBookRequest {
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