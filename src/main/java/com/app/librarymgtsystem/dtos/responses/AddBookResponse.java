package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AddBookResponse {
    private String id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;

    private String addBookMsg;
    private LocalDate rentalDate;
}
