package com.app.librarymgtsystem.controllers;

import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.ViewBookResponse;
import com.app.librarymgtsystem.exceptions.*;
import com.app.librarymgtsystem.services.BookService;
import com.app.librarymgtsystem.services.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Controller
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ShelveRepository shelveRepository;


    @PostMapping("/addBook")
    public ResponseEntity<?> addBook(@RequestBody AddBookRequest addBookRequest, HttpServletRequest request) {
        try {
            AddBookResponse response = bookService.addBook(addBookRequest, request);
            return ResponseEntity.ok(response);
        } catch (NotInSessionException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NotEligiblePageException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (BookCannotBeEmptyException | BookExistException | IsbnExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }


    @GetMapping("/view-all")
    public ResponseEntity<?> viewBookByAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            ViewBookResponse response = bookService.viewBookByAll(page, size, request);
            return ResponseEntity.ok(response);
        } catch (EmailNotFoundException | NotEligiblePageException | BookNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }


    
}



