package com.app.librarymgtsystem.dtos.responses;

import com.app.librarymgtsystem.data.models.Book;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeleteBookResponse {
    private String id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;

    private boolean sessionStatus;
    private int accessLevel;
    private String viewBookMsg;
    private LocalDateTime creationDate = LocalDateTime.now();

    private int currentPage;
    private int totalPages;
    private long totalBooks;
    private int pageSize;
    private List<Book> books;

}