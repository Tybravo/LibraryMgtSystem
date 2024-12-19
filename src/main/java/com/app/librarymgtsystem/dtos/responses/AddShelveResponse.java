package com.app.librarymgtsystem.dtos.responses;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddShelveResponse {
    private String id;
    private String bookId;
    private String memberId;
    private LocalDate createdAt;

    private List<String> bookIds;
    private List<String> memberIds;
}

