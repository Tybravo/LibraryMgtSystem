package com.app.librarymgtsystem.services;


import com.app.librarymgtsystem.data.models.*;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.RackRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.*;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import com.app.librarymgtsystem.exceptions.BookInShelveNotAvailableException;
import com.app.librarymgtsystem.exceptions.BookNotFoundException;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RackServiceImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    BookService bookService;
    @Autowired
    ShelveRepository shelveRepository;
    @Autowired
    private RackRepository rackRepository;
    @Autowired
    private RackService rackService;
    @Autowired
    private RackServiceImpl rackServiceImpl;

    @BeforeEach
    void eraseAll() {
        memberRepository.deleteAll();
        bookRepository.deleteAll();
        rackRepository.deleteAll();
        MemberServiceImpl.LoggedInUserContext.clear();
    }


    @Test
    public void test_That_Member_Not_In_Session_Cannot_Add_Book_To_Rack() {
        Member addMemberRequest2 = new Member();
        addMemberRequest2.setFullName("Michael Bravo");
        addMemberRequest2.setEmail("michaelbravo@gmail.com");
        addMemberRequest2.setPassword("consistency");
        addMemberRequest2.setPhoneNumber("08027663871");
        addMemberRequest2.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest2.setAccessLevel(10);
        addMemberRequest2.setSessionStatus(false);
        memberRepository.save(addMemberRequest2);
        AddMemberResponse response2 = new AddMemberResponse();
        response2.setRegMsg("Registration successful");
        assertEquals("Registration successful", response2.getRegMsg());

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setSessionStatus(false);
        String title = "Be Intentional A";

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                rackService.addToRack(addRackRequest, title));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_Add_Book_To_Rack_If_Picked_Book_Title_Not_found(){
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Michael Bravo");
        addMemberRequest.setEmail("michaelbravo@gmail.com");
        addMemberRequest.setPassword("consistency");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);

        AddMemberResponse savedMemberResponse = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", savedMemberResponse.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("michaelbravo@gmail.com");
        loginRequest.setPassword("consistency");
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("michaelbravo@gmail.com", getResponse.getEmail());
        assertEquals("consistency", getResponse.getPassword());

        Member addMemberRequest1 = new Member();
        addMemberRequest1.setFullName("Librarian Learned");
        addMemberRequest1.setEmail("durayg2000@yahoo.com");
        addMemberRequest1.setPassword("greatness");
        addMemberRequest1.setPhoneNumber("08027663871");
        addMemberRequest1.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest1.setAccessLevel(20);
        addMemberRequest1.setSessionStatus(false);
        memberRepository.save(addMemberRequest1);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setEmail("durayg2000@yahoo.com");
        loginRequest1.setPassword("greatness");
        Member getResponse1 = memberService.loginMember(loginRequest1);
        assertEquals("durayg2000@yahoo.com", getResponse1.getEmail());
        assertEquals("greatness", getResponse1.getPassword());

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookResponse = bookService.addBook(addBookRequest);
        assertEquals(savedBookResponse.getBookTitle(), addBookRequest.getBookTitle());

        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setCurrentBookTitle("Be Intentional G7");
        updateBookRequest.setBookTitle("Be Intentional G1");

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setMemberId(savedMemberResponse.getId());
        addRackRequest.setBookId(savedBookResponse.getId());
        addRackRequest.setRackChoice(RackChoice.PURCHASE);
        addRackRequest.setRackAmount(BigDecimal.valueOf(562_827_261_2873_184_045L) );
        addRackRequest.setSessionStatus(true);
        String title = updateBookRequest.getCurrentBookTitle();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                rackService.addToRack(addRackRequest, title));
        assertEquals("Book with title '" +title + "' cannot be found.", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_Add_Book_To_Rack_If_Is_Not_Available() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Michael Bravo");
        addMemberRequest.setEmail("michaelbravo@gmail.com");
        addMemberRequest.setPassword("consistency");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);

        AddMemberResponse savedMemberResponse = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", savedMemberResponse.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("michaelbravo@gmail.com");
        loginRequest.setPassword("consistency");
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("michaelbravo@gmail.com", getResponse.getEmail());
        assertEquals("consistency", getResponse.getPassword());

        Member addMemberRequest1 = new Member();
        addMemberRequest1.setFullName("Librarian Learned");
        addMemberRequest1.setEmail("durayg2000@yahoo.com");
        addMemberRequest1.setPassword("greatness");
        addMemberRequest1.setPhoneNumber("08027663871");
        addMemberRequest1.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest1.setAccessLevel(20);
        addMemberRequest1.setSessionStatus(false);
        memberRepository.save(addMemberRequest1);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setEmail("durayg2000@yahoo.com");
        loginRequest1.setPassword("greatness");
        Member getResponse1 = memberService.loginMember(loginRequest1);
        assertEquals("durayg2000@yahoo.com", getResponse1.getEmail());
        assertEquals("greatness", getResponse1.getPassword());

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional GG");
        addBookRequest.setBookAuthor("Author Two GG");
        addBookRequest.setBookIsbn("rw63829wz-GG");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose GG");

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.COMICS);
        addShelveRequest.setBookGenre("Genre GG");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest);
        assertNotNull(addBookResponse.getId(), "Book ID should not be null");
        assertEquals("Be Intentional GG", addBookResponse.getBookTitle());
        assertEquals("Author Two GG", addBookResponse.getBookAuthor());
        assertEquals("rw63829wz-GG", addBookResponse.getBookIsbn());
        assertEquals("Characterized by conscious design or purpose GG", addBookResponse.getBookDescription());

        Optional<Book> savedBook = bookRepository.findById(addBookResponse.getId());
        assertTrue(savedBook.isPresent(), "Saved book should be found in the repository");

        Optional<Shelve> savedShelve = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(savedShelve.isPresent());
        shelveRepository.deleteAll();

        Shelve shelveAvail = new Shelve();
        shelveAvail.setCategory(ShelveType.COMICS);
        shelveAvail.setGenre("Genre GG");
        shelveAvail.setBookId(addBookResponse.getId());
        shelveAvail.setAvailable(false);
        shelveRepository.save(shelveAvail);

        Optional<Shelve> keepShelve = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(keepShelve.isPresent());
        assertEquals(addBookResponse.getId(), keepShelve.get().getBookId());
        assertEquals(ShelveType.COMICS, keepShelve.get().getCategory());
        assertEquals("Genre GG", keepShelve.get().getGenre());
        assertFalse(keepShelve.get().isAvailable());
        assertFalse(keepShelve.get().isBorrowed());

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setCurrentBookTitle(("Be Intentional GG"));
        addRackRequest.setMemberId(savedMemberResponse.getId());
        addRackRequest.setBookId(addBookResponse.getId());
        addRackRequest.setRackChoice(RackChoice.PURCHASE);
        addRackRequest.setRackAmount(BigDecimal.valueOf(562_827_261_2873_184_045L) );
        addRackRequest.setSessionStatus(true);
        String title = addRackRequest.getCurrentBookTitle();

        BookInShelveNotAvailableException exception = assertThrows(BookInShelveNotAvailableException.class, () ->
                rackService.addToRack(addRackRequest, title));
        assertEquals("Book is currently not available in the shelve", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Can_Add_Book_To_Rack_If_Is_Available() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Michael Bravo");
        addMemberRequest.setEmail("michaelbravo@gmail.com");
        addMemberRequest.setPassword("consistency");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);

        AddMemberResponse savedMemberResponse = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", savedMemberResponse.getRegMsg());
        System.out.println(savedMemberResponse.getId());
        assertNotNull(savedMemberResponse.getId());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("michaelbravo@gmail.com");
        loginRequest.setPassword("consistency");
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("michaelbravo@gmail.com", getResponse.getEmail());
        assertEquals("consistency", getResponse.getPassword());

        Member addMemberRequest1 = new Member();
        addMemberRequest1.setFullName("Librarian Learned");
        addMemberRequest1.setEmail("durayg2000@yahoo.com");
        addMemberRequest1.setPassword("greatness");
        addMemberRequest1.setPhoneNumber("08027663871");
        addMemberRequest1.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest1.setAccessLevel(20);
        addMemberRequest1.setSessionStatus(false);
        memberRepository.save(addMemberRequest1);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setEmail("durayg2000@yahoo.com");
        loginRequest1.setPassword("greatness");
        Member getResponse1 = memberService.loginMember(loginRequest1);
        assertEquals("durayg2000@yahoo.com", getResponse1.getEmail());
        assertEquals("greatness", getResponse1.getPassword());

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional GG");
        addBookRequest.setBookAuthor("Author Two GG");
        addBookRequest.setBookIsbn("rw63829wz-GG");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose GG");

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.COMICS);
        addShelveRequest.setBookGenre("Genre GG");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest);
        assertNotNull(addBookResponse.getId(), "Book ID should not be null");
        assertEquals("Be Intentional GG", addBookResponse.getBookTitle());
        assertEquals("Author Two GG", addBookResponse.getBookAuthor());
        assertEquals("rw63829wz-GG", addBookResponse.getBookIsbn());
        assertEquals("Characterized by conscious design or purpose GG", addBookResponse.getBookDescription());

        Optional<Book> savedBook = bookRepository.findById(addBookResponse.getId());
        assertTrue(savedBook.isPresent(), "Saved book should be found in the repository");

        Optional<Shelve> savedShelve = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(savedShelve.isPresent());
        shelveRepository.deleteAll();

        Shelve shelveAvail = new Shelve();
        shelveAvail.setCategory(ShelveType.COMICS);
        shelveAvail.setGenre("Genre GG");
        shelveAvail.setBookId(addBookResponse.getId());
        shelveAvail.setAvailable(true);
        shelveRepository.save(shelveAvail);

        Optional<Shelve> keepShelve = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(keepShelve.isPresent());
        assertEquals(addBookResponse.getId(), keepShelve.get().getBookId());
        assertEquals(ShelveType.COMICS, keepShelve.get().getCategory());
        assertEquals("Genre GG", keepShelve.get().getGenre());
        assertTrue(keepShelve.get().isAvailable());
        assertFalse(keepShelve.get().isBorrowed());

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setCurrentBookTitle(("Be Intentional GG"));
        addRackRequest.setMemberId(savedMemberResponse.getId());
        addRackRequest.setBookId(addBookResponse.getId());
        addRackRequest.setRackChoice(RackChoice.PURCHASE);
        addRackRequest.setRackCopy(RackCopy.SOFT);
        addRackRequest.setRackAmount(BigDecimal.valueOf(562_827_261_2873_184_045L) );
        addRackRequest.setSessionStatus(true);
        String title = addRackRequest.getCurrentBookTitle();

        AddRackResponse addedRack = rackService.addToRack(addRackRequest, title);
        assertEquals(addedRack.getAddRackMsg(), "Book is added to rack successfully");
        System.out.println(addedRack.getBookId());
        System.out.println(addBookResponse.getId());
        assertEquals(addedRack.getBookId(), addBookResponse.getId());
    }


}