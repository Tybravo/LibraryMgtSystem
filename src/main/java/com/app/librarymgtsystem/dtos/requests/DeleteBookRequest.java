package com.app.librarymgtsystem.dtos.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeleteBookRequest {
    private String id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;
    private String bookLink;
    private String bookCurrency;
    private BigDecimal bookPrice;
    private int bookQuantity;

    private boolean sessionStatus;
    private int accessLevel;
    private String viewBookMsg;
    private LocalDateTime creationDate = LocalDateTime.now();

}
