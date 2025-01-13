package com.app.librarymgtsystem.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AddBookRequest {

    //private String id;

    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;
    private String bookLink;
    private String bookCurrency;
    //private BigDecimal bookPrice;
    private int bookQuantity;

//    @JsonIgnore
//    private boolean sessionStatus;
//    @JsonIgnore
//    private int accessLevel;
//    @JsonIgnore
//    private LocalDateTime creationDate = LocalDateTime.now();
}
