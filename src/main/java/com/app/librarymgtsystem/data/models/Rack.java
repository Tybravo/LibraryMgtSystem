package com.app.librarymgtsystem.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document
public class Rack {
    @Id
    private int id;
    private String memberId;
    private String bookId;
    private String currency;
    private BigDecimal amount;
    private boolean authorize;
    private int numberOfCopy;

    private RackChoice rackChoice;
    private RackCopy rackCopy;
    private LocalDateTime creationDate = LocalDateTime.now();
}
