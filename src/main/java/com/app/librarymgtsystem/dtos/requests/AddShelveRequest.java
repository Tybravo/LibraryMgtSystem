package com.app.librarymgtsystem.dtos.requests;

import com.app.librarymgtsystem.data.models.ShelveType;
import lombok.Data;
import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddShelveRequest {
    private String id;
    private String bookId;
    private ShelveType category;
    private String genre;
    private boolean available = true;
    private boolean borrowed = false;
    private String addShelveMsg;
    private LocalDateTime creationDate = LocalDateTime.now();

    private List<Book> bookIds;
    private List<Member> memberIds;
}
