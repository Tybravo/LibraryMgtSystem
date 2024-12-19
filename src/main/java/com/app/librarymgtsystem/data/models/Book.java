package com.app.librarymgtsystem.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String addBookMsg;
    private LocalDateTime creationDate = LocalDateTime.now();
}
