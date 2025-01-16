package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;

public class TestSerialization {
    public static void main(String[] args) throws Exception {
        AddBookRequest request = new AddBookRequest();
        request.setBookTitle("Effective Java");
        request.setBookAuthor("Joshua Bloch");
        request.setBookIsbn("9780134685991");
       // request.setBookPrice(BigDecimal.ZERO);
        request.setBookPrice(Double.parseDouble(String.valueOf(55.77)));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        System.out.println(json);
    }
}
