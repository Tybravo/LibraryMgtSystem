package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.*;
import com.app.librarymgtsystem.dtos.responses.*;
import com.app.librarymgtsystem.exceptions.*;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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
    @Autowired
    private ServletRequest request;


    @BeforeEach
    void eraseAll() {
        shelveRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        MemberServiceImpl.LoggedInUserContext.clear();
    }


    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_View_Shelve_Of_Books_By_Category() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

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
                shelveService.viewShelveByCategory(ShelveType.FICTION, request));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
        }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_View_Shelve_Of_Books_By_Category_Using_Wrong_Access_Level() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        ViewShelveResponse viewShelveResponse = new ViewShelveResponse();
        viewShelveResponse.setSessionStatus(true);
        ShelveType shelveType = ShelveType.FICTION;

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                shelveService.viewShelveByCategory(shelveType, request));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_View_Shelve_Of_Books_By_Category() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertTrue(getResponse.isSessionStatus());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest1 = new AddBookRequest();
        addBookRequest1.setBookTitle("Be Intentional A");
        addBookRequest1.setBookAuthor("Author A");
        addBookRequest1.setBookIsbn("978-0-14-028329-5");
        addBookRequest1.setBookDescription("Carefully thought out in advance A");
        addBookRequest1.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest1.setBookCurrency("USD");
        addBookRequest1.setBookPrice(453478896747837883.00);
        addBookRequest1.setBookQuantity(14);
        addBookRequest1.setSessionStatus(true);
        addBookRequest1.setAccessLevel(20);
        AddBookResponse savedBookRequest1 = bookService.addBook(addBookRequest1, request);
        assertEquals(savedBookRequest1.getBookTitle(), addBookRequest1.getBookTitle());

        AddBookRequest addBookRequest2 = new AddBookRequest();
        addBookRequest2.setBookTitle("Be Intentional B");
        addBookRequest2.setBookAuthor("Author A");
        addBookRequest2.setBookIsbn("978-0-14-028329-8");
        addBookRequest2.setBookDescription("Carefully thought out in advance B");
        addBookRequest2.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest2.setBookCurrency("USD");
        addBookRequest2.setBookPrice(453478896747837883.00);
        addBookRequest2.setBookQuantity(14);
        addBookRequest2.setSessionStatus(true);
        addBookRequest2.setAccessLevel(20);
        AddBookResponse savedBookRequest2 = bookService.addBook(addBookRequest2, request);
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

        List<Shelve> shelves = shelveService.viewShelveByCategory(ShelveType.FICTION, request);

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

    @Test
    public void test_That_Member_Not_In_Session_Cannot_View_Shelve_Of_Books_By_Category_For_Members() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse memberResponse = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", memberResponse.getRegMsg());
        assertEquals("twinebravo@gmail.com", memberResponse.getEmail());

        ViewShelveResponse viewShelveResponse = new ViewShelveResponse();
        viewShelveResponse.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                shelveService.viewShelveByCategoryForMembers(ShelveType.CHILDREN, request));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_View_Shelve_Of_Books_By_Category_Using_Wrong_Access_Level_For_Members() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(40);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        String sessionEmailMember = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailMember);

        ViewShelveResponse viewShelveResponse = new ViewShelveResponse();
        viewShelveResponse.setSessionStatus(true);
        ShelveType shelveType = ShelveType.COOKBOOKS;

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                shelveService.viewShelveByCategoryForMembers(shelveType, request));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Can_View_Shelve_Of_Books_By_Category_For_Members() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertTrue(getResponse.isSessionStatus());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest book1 = new AddBookRequest();
        book1.setBookTitle("Be Intentional C");
        book1.setBookAuthor("Author C");
        book1.setBookIsbn("978-0-13-468599-1");
        book1.setBookDescription("Carefully thought out in advance C.");
        book1.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        book1.setBookCurrency("USD");
        book1.setBookPrice(453478896747837883.00);
        book1.setBookQuantity(14);
        book1.setSessionStatus(true);
        book1.setAccessLevel(20);
        AddBookResponse savedBook1 = bookService.addBook(book1, request);
        assertEquals(book1.getBookTitle(), savedBook1.getBookTitle());

        AddBookRequest book2 = new AddBookRequest();
        book2.setBookTitle("Be Intentional D");
        book2.setBookAuthor("Author D");
        book2.setBookIsbn("978-0-13-235088-4");
        book2.setBookDescription("Carefully thought out in advance D");
        book2.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        book2.setBookCurrency("USD");
        book2.setBookPrice(453478896747837883.00);
        book2.setBookQuantity(14);
        book2.setSessionStatus(true);
        book2.setAccessLevel(20);
        AddBookResponse savedBook2 = bookService.addBook(book2, request);
        assertEquals(book2.getBookTitle(), savedBook2.getBookTitle());

        Shelve shelve1 = new Shelve();
        shelve1.setBookId(savedBook1.getId());
        shelve1.setCategory(ShelveType.CHILDREN);
        shelve1.setGenre("Genre C");
        shelve1.setAvailable(true);
        shelve1.setBorrowed(false);
        shelveRepository.save(shelve1);

        Shelve shelve2 = new Shelve();
        shelve2.setBookId(savedBook2.getId());
        shelve2.setCategory(ShelveType.CHILDREN);
        shelve2.setGenre("Genre D");
        shelve2.setAvailable(true);
        shelve2.setBorrowed(false);
        shelveRepository.save(shelve2);

        Member addMemberRequest1 = new Member();
        addMemberRequest1.setFullName("Ade Bravo");
        addMemberRequest1.setEmail("twinebravo@gmail.com");
        addMemberRequest1.setPassword("tybravo");
        addMemberRequest1.setPhoneNumber("08027663871");
        addMemberRequest1.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest1.setAccessLevel(10);
        addMemberRequest1.setSessionStatus(false);
        memberRepository.save(addMemberRequest1);
        AddMemberResponse response1 = new AddMemberResponse();
        response1.setRegMsg("Registration successful");
        assertEquals("Registration successful", response1.getRegMsg());

        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setEmail("twinebravo@gmail.com");
        loginRequest1.setPassword("tybravo");
        //loginRequest1.setSessionStatus(true);
        Member getResponse1 = memberService.loginMember(loginRequest1, request);
        assertEquals("twinebravo@gmail.com", getResponse1.getEmail());
        assertEquals("tybravo", getResponse1.getPassword());

        List<ViewShelveResponse> shelves = shelveService.viewShelveByCategoryForMembers(ShelveType.CHILDREN, request);
        assertNotNull(shelves);
        assertEquals(2, shelves.size());

        for (ViewShelveResponse response2 : shelves) {
            assertNotNull(response2.getBookTitle());
            assertNotNull(response2.getBookDescription());
            assertNotNull(response2.getBookGenre());
            assertTrue(response2.isAvailable());
            assertFalse(response2.isBorrowed());
            }
    }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_Update_Shelve_Of_Books() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setSessionStatus(false);
        String title = updateShelveRequest.getCurrentBookTitle();

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                shelveService.updateShelveByBookTitle(updateShelveRequest, title, request));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Shelve_Of_Books_Using_Wrong_Access_Level() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setSessionStatus(false);
        String title = updateShelveRequest.getCurrentBookTitle();

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                shelveService.updateShelveByBookTitle(updateShelveRequest, title, request));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Shelve_Of_Books_If_Input_Book_Title_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G7");
        updateShelveRequest.setBookTitle("Be Intentional G1");
        updateShelveRequest.setSessionStatus(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                shelveService.updateShelveByBookTitle(updateShelveRequest, title, request));
        assertEquals("Book with the title '" + updateShelveRequest.getCurrentBookTitle() + "' not found", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Shelve_Of_Books_If_BookId_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G");
        updateShelveRequest.setSessionStatus(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        Shelve addShelve = new Shelve();
        addShelve.setBookId(null);
        addShelve.setCategory(ShelveType.COOKBOOKS);
        addShelve.setGenre("Genre G1");
        addShelve.setAvailable(true);
        addShelve.setBorrowed(false);
        shelveRepository.save(addShelve);

        Optional<Shelve> findBookId = shelveRepository.findByBookId(savedBookRequest.getId());
        assertFalse(findBookId.isPresent());
        updateShelveRequest.setBookId(addShelve.getBookId());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                shelveService.updateShelveByBookTitle(updateShelveRequest, title, request));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Shelve_Of_Books_By_Book_Title_If_Is_Not_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional GG");
        addBookRequest.setBookAuthor("Author Two GG");
        addBookRequest.setBookIsbn("rw63829wz-GG");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose GG");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.COMICS);
        addShelveRequest.setBookGenre("Genre GG");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest, request);
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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle(("Be Intentional GG"));
        updateShelveRequest.setBookGenre("Genre GGG");
        updateShelveRequest.setBookCategory(ShelveType.EDUCATION);
        String title = updateShelveRequest.getCurrentBookTitle();

        BookInShelveNotAvailableException exception = assertThrows(BookInShelveNotAvailableException.class, () ->
                shelveService.updateShelveByBookTitle(updateShelveRequest, title, request));
        assertEquals("Book is currently not available in the shelve", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_Update_Shelve_Of_Books_By_Book_Title_If_Is_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional GG");
        addBookRequest.setBookAuthor("Author Two GG");
        addBookRequest.setBookIsbn("rw63829wz-GG");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose GG");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.COMICS);
        addShelveRequest.setBookGenre("Genre GG");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest,  request);
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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle(("Be Intentional GG"));
        updateShelveRequest.setBookGenre("Genre GGG");
        updateShelveRequest.setBookCategory(ShelveType.EDUCATION);
        String title = updateShelveRequest.getCurrentBookTitle();

        UpdateShelveResponse updatedShelve = shelveService.updateShelveByBookTitle(updateShelveRequest, title, request);
        assertEquals(updatedShelve.getUpdateShelveMsg(), "Shelve updated successfully");
        assertEquals(updatedShelve.getBookCategory(), ShelveType.EDUCATION);
        assertEquals(updatedShelve.getBookGenre(), "Genre GGG");
    }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setAvailable(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                shelveService.setBookAvailableInShelve(updateShelveRequest, title, request));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Available_Using_Wrong_Access_Level() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setAvailable(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                shelveService.setBookAvailableInShelve(updateShelveRequest, title, request));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Available_If_Input_Book_Title_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G7");
        updateShelveRequest.setAvailable(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                shelveService.setBookAvailableInShelve(updateShelveRequest, title, request));
        assertEquals("Book with the title '" + updateShelveRequest.getCurrentBookTitle() + "' not found", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Available_If_BookId_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G");
        updateShelveRequest.setSessionStatus(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        Shelve addShelve = new Shelve();
        addShelve.setBookId(null);
        addShelve.setCategory(ShelveType.COOKBOOKS);
        addShelve.setGenre("Genre G1");
        addShelve.setAvailable(true);
        addShelve.setBorrowed(false);
        shelveRepository.save(addShelve);

        Optional<Shelve> findBookId = shelveRepository.findByBookId(savedBookRequest.getId());
        assertFalse(findBookId.isPresent());
        updateShelveRequest.setBookId(addShelve.getBookId());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                shelveService.setBookAvailableInShelve(updateShelveRequest, title, request));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Available_If_Is_Already_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G");
        updateShelveRequest.setSessionStatus(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        Shelve addShelve = new Shelve();
        addShelve.setBookId(savedBookRequest.getId());
        addShelve.setCategory(ShelveType.COOKBOOKS);
        addShelve.setGenre("Genre G1");
        addShelve.setAvailable(true);
        addShelve.setBorrowed(false);
        shelveRepository.save(addShelve);

        Optional<Shelve> findBookId = shelveRepository.findByBookId(savedBookRequest.getId());
        assertTrue(findBookId.isPresent());
        assertTrue(findBookId.get().isAvailable());
        updateShelveRequest.setAvailable(true);

        BooKAvailabilitySetAlreadyException exception = assertThrows(BooKAvailabilitySetAlreadyException.class, () ->
                shelveService.setBookAvailableInShelve(updateShelveRequest, title, request));
        assertEquals("Stop! The Book is already set to be available", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_Set_Book_Inside_Shelve_To_Be_Available() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional GG");
        addBookRequest.setBookAuthor("Author Two GG");
        addBookRequest.setBookIsbn("rw63829wz-GG");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose GG");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.COMICS);
        addShelveRequest.setBookGenre("Genre GG");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest, request);
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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle(("Be Intentional GG"));
        updateShelveRequest.setAvailable(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        UpdateShelveResponse updatedShelve = shelveService.setBookAvailableInShelve(updateShelveRequest, title, request);
        assertEquals(updatedShelve.getUpdateShelveMsg(), "Yes! Book is now available");

        Optional<Shelve> updatedAvailable = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(updatedAvailable.isPresent());
        assertEquals(updatedAvailable.get().getCategory(), ShelveType.COMICS);
        assertEquals(updatedAvailable.get().getGenre(), "Genre GG");
        assertTrue(updatedAvailable.get().isAvailable());
    }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Unavailable() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setAvailable(false);
        String title = updateShelveRequest.getCurrentBookTitle();

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                shelveService.setBookUnavailableInShelve(updateShelveRequest, title, request));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_UnAvailable_Using_Wrong_Access_Level() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setAvailable(false);
        String title = updateShelveRequest.getCurrentBookTitle();

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                shelveService.setBookUnavailableInShelve(updateShelveRequest, title, request));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Unavailable_If_Input_Book_Title_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G7");
        updateShelveRequest.setAvailable(false);
        String title = updateShelveRequest.getCurrentBookTitle();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                shelveService.setBookUnavailableInShelve(updateShelveRequest, title, request));
        assertEquals("Book with the title '" + updateShelveRequest.getCurrentBookTitle() + "' not found", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Unavailable_If_BookId_Not_found(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G");
        updateShelveRequest.setSessionStatus(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        Shelve addShelve = new Shelve();
        addShelve.setBookId(null);
        addShelve.setCategory(ShelveType.COOKBOOKS);
        addShelve.setGenre("Genre G1");
        addShelve.setAvailable(true);
        addShelve.setBorrowed(false);
        shelveRepository.save(addShelve);

        Optional<Shelve> findBookId = shelveRepository.findByBookId(savedBookRequest.getId());
        assertFalse(findBookId.isPresent());
        updateShelveRequest.setBookId(addShelve.getBookId());
        updateShelveRequest.setAvailable(false);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                shelveService.setBookAvailableInShelve(updateShelveRequest, title, request));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    public void  test_That_Librarian_Inside_Session_Cannot_Set_Book_Inside_Shelve_To_Be_Unavailable_If_Is_Already_Unavailable() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest, request);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle("Be Intentional G");
        updateShelveRequest.setSessionStatus(true);
        String title = updateShelveRequest.getCurrentBookTitle();

        Shelve addShelve = new Shelve();
        addShelve.setBookId(savedBookRequest.getId());
        addShelve.setCategory(ShelveType.COOKBOOKS);
        addShelve.setGenre("Genre G1");
        addShelve.setAvailable(false);
        addShelve.setBorrowed(false);
        shelveRepository.save(addShelve);

        Optional<Shelve> findBookId = shelveRepository.findByBookId(savedBookRequest.getId());
        assertTrue(findBookId.isPresent());
        assertFalse(findBookId.get().isAvailable());
        updateShelveRequest.setAvailable(false);

        BooKAvailabilitySetAlreadyException exception = assertThrows(BooKAvailabilitySetAlreadyException.class, () ->
                shelveService.setBookUnavailableInShelve(updateShelveRequest, title, request));
        assertEquals("Stop! The Book is already set to be unavailable", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_Set_Book_Inside_Shelve_To_Be_Unavailable() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

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
        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setBookTitle("Be Intentional GG");
        addBookRequest.setBookAuthor("Author Two GG");
        addBookRequest.setBookIsbn("rw63829wz-GG");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose GG");
        addBookRequest.setBookLink(("https://open.spotify.com/track/7LZkMeX1k8PXQJ0SVYn1A5"));
        addBookRequest.setBookCurrency("USD");
        addBookRequest.setBookPrice(453478896747837883.00);
        addBookRequest.setBookQuantity(14);

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.COMICS);
        addShelveRequest.setBookGenre("Genre GG");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest, request);
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

        UpdateShelveRequest updateShelveRequest = new UpdateShelveRequest();
        updateShelveRequest.setCurrentBookTitle(("Be Intentional GG"));
        updateShelveRequest.setAvailable(false);
        String title = updateShelveRequest.getCurrentBookTitle();

        UpdateShelveResponse updatedShelve = shelveService.setBookUnavailableInShelve(updateShelveRequest, title, request);
        assertEquals(updatedShelve.getUpdateShelveMsg(), "No! Book is not available");

        Optional<Shelve> updatedAvailable = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(updatedAvailable.isPresent());
        assertEquals(updatedAvailable.get().getCategory(), ShelveType.COMICS);
        assertEquals(updatedAvailable.get().getGenre(), "Genre GG");
        assertFalse(updatedAvailable.get().isAvailable());
    }


}