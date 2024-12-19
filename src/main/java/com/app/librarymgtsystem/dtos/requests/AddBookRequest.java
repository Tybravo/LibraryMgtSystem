package com.app.librarymgtsystem.dtos.requests;

import lombok.Data;

@Data
public class AddBookRequest {
    private String id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;

    private boolean sessionStatus;
}
