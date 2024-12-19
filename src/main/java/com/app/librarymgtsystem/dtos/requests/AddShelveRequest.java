package com.app.librarymgtsystem.dtos.requests;

import lombok.Data;
import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;

import java.util.List;

@Data
public class AddShelveRequest {
    private String bookId;
    private String memberId;
    private List<Book> bookIds;
    private List<Member> memberIds;
}
