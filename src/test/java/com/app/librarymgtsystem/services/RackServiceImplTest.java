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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        Member addMemberRequest= new Member();
        addMemberRequest.setFullName("Michael Bravo");
        addMemberRequest.setEmail("michaelbravo@gmail.com");
        addMemberRequest.setPassword("consistency");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response2 = new AddMemberResponse();
        response2.setRegMsg("Registration successful");
        assertEquals("Registration successful", response2.getRegMsg());

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setSessionStatus(false);
        String title = "Be Intentional A";
        String sessionEmail = addMemberRequest.getEmail();

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                rackService.addToRack(addRackRequest, title, request));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_Add_Book_To_Rack_If_Picked_Book_Title_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);

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
        Member getResponse1 = memberService.loginMember(loginRequest1, request);
        assertEquals("durayg2000@yahoo.com", getResponse1.getEmail());
        assertEquals("greatness", getResponse1.getPassword());

        String sessionEmailLibrarian = getResponse1.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);
        System.out.println("Session email: " + session.getAttribute("userEmail"));

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("michaelbravo@gmail.com", getResponse.getEmail());
        assertEquals("consistency", getResponse.getPassword());

        String sessionEmailMember = getResponse.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailMember);
        System.out.println("Session email: " + session.getAttribute("userEmail"));

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setMemberId(savedMemberResponse.getId());
        addRackRequest.setBookId(savedBookResponse.getId());
        addRackRequest.setRackChoice(RackChoice.PURCHASE);
        addRackRequest.setRackAmount(BigDecimal.valueOf(562_827_261_2873_184_045L) );
        addRackRequest.setSessionStatus(true);
        String title = updateBookRequest.getCurrentBookTitle();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                rackService.addToRack(addRackRequest, title, request));
        assertEquals("Book with title '" +title + "' cannot be found.", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_Add_Book_To_Rack_If_Is_Not_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);

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
        Member getResponse1 = memberService.loginMember(loginRequest1, request);
        assertEquals("durayg2000@yahoo.com", getResponse1.getEmail());
        assertEquals("greatness", getResponse1.getPassword());

        String sessionEmailLibrarian = getResponse1.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("michaelbravo@gmail.com", getResponse.getEmail());
        assertEquals("consistency", getResponse.getPassword());

        String sessionEmailMember = getResponse1.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailMember);

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setCurrentBookTitle(("Be Intentional GG"));
        addRackRequest.setMemberId(savedMemberResponse.getId());
        addRackRequest.setBookId(addBookResponse.getId());
        addRackRequest.setRackChoice(RackChoice.PURCHASE);
        addRackRequest.setRackAmount(BigDecimal.valueOf(562_827_261_2873_184_045L) );
        addRackRequest.setSessionStatus(true);
        String title = addRackRequest.getCurrentBookTitle();

        BookInShelveNotAvailableException exception = assertThrows(BookInShelveNotAvailableException.class, () ->
                rackService.addToRack(addRackRequest, title, request));
        assertEquals("Book is currently not available in the shelve", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Can_Add_Book_To_Rack_If_Is_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);

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
        Member getResponse1 = memberService.loginMember(loginRequest1, request);
        assertEquals("durayg2000@yahoo.com", getResponse1.getEmail());
        assertEquals("greatness", getResponse1.getPassword());

        String sessionEmailLibrarian = getResponse1.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

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
        assertNotNull(savedMemberResponse.getId());
        System.out.println("Member ID going to Rack " +savedMemberResponse.getId());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("michaelbravo@gmail.com");
        loginRequest.setPassword("consistency");
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("michaelbravo@gmail.com", getResponse.getEmail());
        assertEquals("consistency", getResponse.getPassword());

        String sessionEmailMember = getResponse.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailMember);

        AddRackRequest addRackRequest = new AddRackRequest();
        addRackRequest.setCurrentBookTitle(("Be Intentional GG"));
        addRackRequest.setMemberId(savedMemberResponse.getId());
        addRackRequest.setBookId(addBookResponse.getId());
        addRackRequest.setRackChoice(RackChoice.PURCHASE);
        addRackRequest.setRackCopy(RackCopy.SOFT);
        addRackRequest.setRackAmount(BigDecimal.valueOf(562_827_261_2873_184_045L) );
        addRackRequest.setSessionStatus(true);
        String title = addRackRequest.getCurrentBookTitle();

        AddRackResponse addedRack = rackService.addToRack(addRackRequest, title, request);
        assertEquals(addedRack.getAddRackMsg(), "Book is added to rack successfully");
        System.out.println("Book ID coming from Book " +addBookResponse.getId());
        System.out.println("Book ID going to Rack " +addedRack.getBookId());
        assertEquals(addedRack.getBookId(), addBookResponse.getId());
    }

}