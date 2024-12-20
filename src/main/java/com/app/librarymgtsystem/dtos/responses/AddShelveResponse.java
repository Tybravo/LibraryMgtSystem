package com.app.librarymgtsystem.dtos.responses;

import com.app.librarymgtsystem.data.models.ShelveType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddShelveResponse {
    private String id;
    private String bookId;
    private ShelveType category;
    private String genre;
    private boolean available = true;
    private boolean borrowed = false;
    private String addShelveMsg;
    private LocalDateTime creationDate = LocalDateTime.now();

    private List<String> bookIds;
    private List<String> memberIds;
}

