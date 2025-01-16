package com.app.librarymgtsystem.dtos.requests;



import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddBookRequest {

    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookDescription;
    private String bookLink;
    private String bookCurrency;
    private int bookQuantity;

    private Double bookPrice = 0.0;


    public BigDecimal getBookPriceAsBigDecimal() {
        return bookPrice != null ? BigDecimal.valueOf(bookPrice) : BigDecimal.ZERO;
    }

    @JsonIgnore
    private String id;

    @JsonIgnore
    private boolean sessionStatus;

    @JsonIgnore
    private int accessLevel;

}









//import com.app.librarymgtsystem.config.BigDecimalSerializer;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import lombok.Data;
//import java.math.BigDecimal;
//
//@Data
//public class AddBookRequest {
//
//    //private String id;
//
//    private String bookTitle;
//    private String bookAuthor;
//    private String bookIsbn;
//    private String bookDescription;
//    private String bookLink;
//    private String bookCurrency;
//
//  @JsonSerialize(using = BigDecimalSerializer.class)
//    private BigDecimal bookPrice = BigDecimal.ZERO;
//
//
//    //private Double bookPrice;
//    private int bookQuantity;
//
//    private BigDecimal bookPrice = BigDecimal.ZERO;
//
//    // Optional setter for accepting Double and converting to BigDecimal
//    public void setBookPrice(Double price) {
//        this.bookPrice = price != null ? BigDecimal.valueOf(price) : BigDecimal.ZERO;
//    }
//
//
//
//    public BigDecimal getBookPrice() {
//        return bookPrice;
//    }
//
//    public void setBookPrice(BigDecimal bookPrice) {
////        this.bookPrice = (bookPrice != null ? bookPrice : BigDecimal.ZERO);
//        if(bookPrice == null){
//            this.bookPrice = BigDecimal.ZERO;
//        }
//    }
//
//
//
//
//
//    @JsonIgnore
//    private boolean sessionStatus;
//    @JsonIgnore
//    private int accessLevel;
//    @JsonIgnore
//    private LocalDateTime creationDate = LocalDateTime.now();
//}
