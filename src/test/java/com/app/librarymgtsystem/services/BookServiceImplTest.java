package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.AddShelveResponse;
import com.app.librarymgtsystem.exceptions.NotEligiblePageException;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


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
            bookRepository.deleteAll();
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
        public void test_That_Librarian_Not_In_Session_Cannot_Add_Book() {
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

    @Test
    public void test_That_Librarian_In_Session_Cannot_Add_Book_For_Wrong_Access_Level() {
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

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");
        loginRequest.setSessionStatus(true);

        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        AddBookRequest addbookRequest = new AddBookRequest();
        addbookRequest.setSessionStatus(true);
        addbookRequest.setAccessLevel(getResponse.getAccessLevel());

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.addBook(addbookRequest));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_In_Session_Can_Add_Book_For_Right_Access_Level() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(20);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("durayg2000@yahoo.com");
        loginRequest.setPassword("greatness");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional");
        addBookRequest.setBookAuthor("Author One");
        addBookRequest.setBookIsbn("rw63829wz");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(getResponse.getAccessLevel());
        AddBookRequest bookRequest = bookService.addBook(addBookRequest);
        assertEquals(bookRequest.getBookTitle(), addBookRequest.getBookTitle());

        AddBookResponse bookResponse = new AddBookResponse();
        bookResponse.setAddBookMsg("Book added successfully");
        assertEquals("Book added successfully", bookResponse.getAddBookMsg());
        }

    @Test
    public void test_That_Book_Can_Be_Found_By_Id() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(20);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("durayg2000@yahoo.com");
        loginRequest.setPassword("greatness");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional");
        addBookRequest.setBookAuthor("Author One");
        addBookRequest.setBookIsbn("rw63829wz");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookRequest savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());
        assertNotNull(savedBookRequest.getId(), "Book Id is generated");

        AddBookResponse bookResponse = new AddBookResponse();
        bookResponse.setAddBookMsg("Book added successfully");
        assertEquals("Book added successfully", bookResponse.getAddBookMsg());

        Optional<AddBookRequest> findBookRequest = bookService.findBookId(savedBookRequest.getId());
        assertTrue(findBookRequest.isPresent(), "Book should be found");
        assertEquals(savedBookRequest.getId(), findBookRequest.get().getId());
        }

    @Test
    public void test_That_Book_Id_And_FICTION_Category_Can_Be_Saved_In_Shelve() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(20);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("durayg2000@yahoo.com");
        loginRequest.setPassword("greatness");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional");
        addBookRequest.setBookAuthor("Author One");
        addBookRequest.setBookIsbn("rw63829wz");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookRequest savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());
        assertNotNull(savedBookRequest.getId(), "Book Id is generated");

        AddBookResponse bookResponse = new AddBookResponse();
        bookResponse.setAddBookMsg("Book added successfully");
        assertEquals("Book added successfully", bookResponse.getAddBookMsg());

        Optional<AddBookRequest> findBookRequest = bookService.findBookId(savedBookRequest.getId());
        assertTrue(findBookRequest.isPresent(), "Book should be found");
        assertEquals(savedBookRequest.getId(), findBookRequest.get().getId());

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setCategory(ShelveType.FICTION);
        addShelveRequest.setBookId(savedBookRequest.getId());
        addShelveRequest.setGenre("Genre One");
        addShelveRequest.setAvailable(true);
        addShelveRequest.setBorrowed(false);
        AddShelveResponse shelveResponse = bookService.addShelveBookId(addShelveRequest);
        assertEquals("Book added to shelve successfully", shelveResponse.getAddShelveMsg());


    }


}