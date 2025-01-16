package com.app.librarymgtsystem.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
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
    private String link;
    private String currency;
    //private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal price;
    private int quantity;
    private String addBookMsg;

    private LocalDateTime creationDate = LocalDateTime.now();

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price : BigDecimal.ZERO;
    }
}
