package com.app.librarymgtsystem.dtos.requests;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.ShelveType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ViewShelveRequest {
    private String id;
    private String bookId;
    private String bookTitle;
    private String description;
    private ShelveType bookCategory;
    private String bookGenre;
    private boolean available = true;
    private boolean borrowed = false;

    private boolean sessionStatus;
    private String addShelveMsg;
    private LocalDateTime creationDate = LocalDateTime.now();

    private List<Book> bookIds;
    private List<Member> memberIds;
}
