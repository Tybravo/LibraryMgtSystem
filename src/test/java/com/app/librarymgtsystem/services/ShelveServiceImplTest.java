package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.ViewShelveResponse;
import com.app.librarymgtsystem.exceptions.NotEligiblePageException;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ShelveServiceImplTest {

    @Autowired
    ShelveRepository shelveRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ShelveService shelveService;
    @Autowired
    BookService bookService;
    @Autowired
    MemberService memberService;


    @BeforeEach
    void eraseAll() {
        shelveRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        MemberServiceImpl.LoggedInUserContext.clear();
    }


    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_View_Shelve_Of_Books_By_Category() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        ViewShelveResponse viewShelveResponse = new ViewShelveResponse();
        viewShelveResponse.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                shelveService.viewShelveByCategory(ShelveType.FICTION));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
        }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_View_Shelve_Of_Books_By_Category_Using_Wrong_Access_Level() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
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

        ViewShelveResponse viewShelveResponse = new ViewShelveResponse();
        viewShelveResponse.setSessionStatus(true);
        ShelveType shelveType = ShelveType.FICTION;

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                shelveService.viewShelveByCategory(shelveType));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_View_Shelve_Of_Books_By_Category() {
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
        assertTrue(getResponse.isSessionStatus());

        AddBookRequest addBookRequest1 = new AddBookRequest();
        addBookRequest1.setBookTitle("Be Intentional A");
        addBookRequest1.setBookAuthor("Author A");
        addBookRequest1.setBookIsbn("978-0-14-028329-5");
        addBookRequest1.setBookDescription("Carefully thought out in advance A");
        addBookRequest1.setSessionStatus(true);
        addBookRequest1.setAccessLevel(20);
        AddBookResponse savedBookRequest1 = bookService.addBook(addBookRequest1);
        assertEquals(savedBookRequest1.getBookTitle(), addBookRequest1.getBookTitle());

        AddBookRequest addBookRequest2 = new AddBookRequest();
        addBookRequest2.setBookTitle("Be Intentional B");
        addBookRequest2.setBookAuthor("Author A");
        addBookRequest2.setBookIsbn("978-0-14-028329-8");
        addBookRequest2.setBookDescription("Carefully thought out in advance B");
        addBookRequest2.setSessionStatus(true);
        addBookRequest2.setAccessLevel(20);
        AddBookResponse savedBookRequest2 = bookService.addBook(addBookRequest2);
        assertEquals(savedBookRequest2.getBookTitle(), addBookRequest2.getBookTitle());

        Shelve shelve1 = new Shelve();
        shelve1.setBookId(savedBookRequest1.getId());
        shelve1.setCategory(ShelveType.FICTION);
        shelve1.setGenre("Genre A");
        shelve1.setAvailable(true);
        shelve1.setBorrowed(false);
        shelveRepository.save(shelve1);

        Shelve savedShelve1 = shelveRepository.findById(shelve1.getId()).orElse(null);
        assertNotNull(savedShelve1);
        assertNotNull(savedShelve1.getBookId());
        System.out.println("Book ID in Shelve 1: " + savedShelve1.getBookId());

        Shelve shelve2 = new Shelve();
        shelve2.setBookId(savedBookRequest2.getId());
        shelve2.setCategory(ShelveType.FICTION);
        shelve2.setGenre("Genre B");
        shelve2.setAvailable(true);
        shelve2.setBorrowed(false);
        shelveRepository.save(shelve2);

        List<Shelve> shelves = shelveService.viewShelveByCategory(ShelveType.FICTION);

        assertNotNull(shelves);
        assertEquals(2, shelves.size());
        for (Shelve shelve : shelves) {
            assertEquals(ShelveType.FICTION, shelve.getCategory());
            assertNotNull(shelve.getBook());
            assertTrue(shelve.isAvailable());
            assertFalse(shelve.isBorrowed());
            assertNotNull(shelve.getBook().getTitle());
            assertNotNull(shelve.getBook().getAuthor());
        }
    }



}