package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.services.BookServiceImpl;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BookServiceImplTest {

        @Autowired
        MemberRepository memberRepository;
        @Autowired
        BookRepository bookRepository;
        @Autowired
        MemberService memberService;
        @Autowired
        BookService bookService;

        @BeforeEach
        void eraseAll() {
            memberRepository.deleteAll();
        }


        @Test
        public void test_That_Member_Not_In_Session_Cannot_Get_Email() {
            Member memberSession = new Member();
            memberSession.setSessionStatus(false);
            NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                    bookService.getMemberEmail());
            assertEquals("Membership account not found", exception.getMessage());
        }

        @Test
        public void test_That_Librarian_Not_In_Session_To_Add_Book() {
            AddMemberRequest addMemberRequest = new AddMemberRequest();
            addMemberRequest.setFullName("Ade Bravo");
            addMemberRequest.setEmail("twinebravo@gmail.com");
            addMemberRequest.setPassword("tybravo");
            addMemberRequest.setPhoneNumber("07032819318");
            addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
            addMemberRequest.setAccessLevel(10);
            addMemberRequest.setSessionStatus(false);
            AddMemberResponse response = memberService.registerMember(addMemberRequest);
            assertEquals("Registration successful", response.getRegMsg());

            AddBookRequest addbookRequest = new AddBookRequest();
            addbookRequest.setSessionStatus(false);

            NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                    bookService.addBook(addbookRequest));
            assertEquals("Not in session or currently logged out!", exception.getMessage());
        }



}