package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AddBookResponse {
    private String id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;

    private String addBookMsg;
    private LocalDateTime creationDate = LocalDateTime.now();
}
