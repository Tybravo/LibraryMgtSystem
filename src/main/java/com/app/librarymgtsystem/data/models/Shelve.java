package com.app.librarymgtsystem.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Shelve {
    @Id
    private String id;
    private String bookId;
    private String memberId;
    private ShelveType category;
    private String genre;
    private LocalDateTime creationDate = LocalDateTime.now();
    private List<Book> books = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    @DBRef
    private Book book;
    @DBRef
    private Member member;
}
