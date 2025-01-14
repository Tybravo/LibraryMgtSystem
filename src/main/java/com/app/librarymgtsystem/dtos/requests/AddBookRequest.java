package com.app.librarymgtsystem.dtos.requests;

import com.app.librarymgtsystem.config.BigDecimalSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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

    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bookPrice = BigDecimal.ZERO; // Default to 0


    private int bookQuantity;


//    public BigDecimal getBookPrice() {
//        return bookPrice;
//    }
//
//    public void setBookPrice(BigDecimal bookPrice) {
//        this.bookPrice = (bookPrice != null ? bookPrice : BigDecimal.ZERO);
//    }





//    @JsonIgnore
//    private boolean sessionStatus;
//    @JsonIgnore
//    private int accessLevel;
//    @JsonIgnore
//    private LocalDateTime creationDate = LocalDateTime.now();
}
