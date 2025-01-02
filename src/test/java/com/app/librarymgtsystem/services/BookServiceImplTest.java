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
        @Autowired
        ShelveRepository shelveRepository;

    @BeforeEach
        void eraseAll() {
            memberRepository.deleteAll();
            bookRepository.deleteAll();
            MemberServiceImpl.LoggedInUserContext.clear();
    }


    @Test
    public void test_That_Empty_Session_Email_Cannot_Get_Member_Into_Session() {
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

        String nonExistSessionEmail = "twinebravo@gmail.com";
        MemberServiceImpl.LoggedInUserContext.setSessionEmail(nonExistSessionEmail);
        MemberServiceImpl.LoggedInUserContext.clear();

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.findMemberSession());
        assertEquals("Not in session or currently logged out!", exception.getMessage());
        }

    @Test
    public void test_That_Email_Not_Found_Cannot_Get_Member_Into_Session() {
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

        String nonExistEmail = "bravo@gmail.com";
        MemberServiceImpl.LoggedInUserContext.setSessionEmail(nonExistEmail);

        EmailNotFoundException exception = assertThrows(EmailNotFoundException.class, () ->
                bookService.findMemberSession());
        assertEquals("Member email not found", exception.getMessage());
    }

    @Test
    public void test_That_Member_Is_Not_Yet_Inside_Session_Or_Is_Logged_Out() {
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

        memberRepository.deleteAll();
        String pullSessionEmail = "twinebravo@gmail.com";
        MemberServiceImpl.LoggedInUserContext.setSessionEmail(pullSessionEmail);
        Member memberSession = new Member();
        memberSession.setFullName("Ade Bravo");
        memberSession.setEmail("twinebravo@gmail.com");
        memberSession.setPassword("tybravo");
        memberSession.setPhoneNumber("07032819318");
        memberSession.setAddress("No. 34, Sabo, Yaba, Lagos.");
        memberSession.setAccessLevel(10);
        memberSession.setSessionEmail("twinebravo@gmail.com");
        memberSession.setSessionToken("6763gge-736hd-632g4-fyu3562-f56hr3");
        memberSession.setSessionStatus(false);
        memberRepository.save(memberSession);
        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.findMemberSession());
        assertEquals("Membership account is out of session", exception.getMessage());
    }

    @Test
    public void test_That_Member_Not_In_Session_Cannot_Add_Book() {
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
    public void test_That_Librarian_Not_In_Session_Cannot_Add_Book_With_Shelve() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        AddBookRequest addbookRequest = new AddBookRequest();
        addbookRequest.setSessionStatus(false);
        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.addBookWithShelve(addbookRequest, addShelveRequest));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Title_Author_ISBN_Of_Book_Cannot_Be_Empty(){
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
        AddBookResponse bookResponse = bookService.addBook(addBookRequest);
        assertEquals(bookResponse.getBookTitle(), addBookRequest.getBookTitle());

        AddBookRequest addEmptyBook = new AddBookRequest();
        addEmptyBook.setBookTitle(null);
        addEmptyBook.setBookAuthor(null);
        addEmptyBook.setBookIsbn(null);

        BookCannotBeEmptyException exception = assertThrows(BookCannotBeEmptyException.class, () ->
                bookService.bookCannotBeEmpty(addEmptyBook));
        assertEquals("Book detail cannot be empty!", exception.getMessage());
        BookCannotBeEmptyException exception2 = assertThrows(BookCannotBeEmptyException.class, () ->
                bookService.addBook(addEmptyBook));
        assertEquals("Book detail cannot be empty!", exception2.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_Add_Book_Using_Wrong_Access_Level() {
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
    public void test_That_Member_Inside_Session_Cannot_Add_Book_With_Shelve_Using_Wrong_Access_Level() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("durayg2000@yahoo.com");
        loginRequest.setPassword("greatness");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());

        AddBookRequest addbookRequest = new AddBookRequest();
        addbookRequest.setSessionStatus(true);
        addbookRequest.setAccessLevel(getResponse.getAccessLevel());
        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setSessionStatus(true);

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.addBookWithShelve(addbookRequest, addShelveRequest));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_Add_Book_Using_Right_Access_Level() {
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
        addBookRequest.setBookTitle("Be Intentional A");
        addBookRequest.setBookAuthor("Author One A");
        addBookRequest.setBookIsbn("rw63829wz-A");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(getResponse.getAccessLevel());

        AddBookResponse bookResponse = bookService.addBook(addBookRequest);
        assertEquals(bookResponse.getBookTitle(), addBookRequest.getBookTitle());
        assertEquals("Book added successfully", bookResponse.getAddBookMsg());
        }

    @Test
    public void test_That_Librarian_Cannot_Add_Book_With_Same_Book_Title_And_Author(){
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
        addBookRequest.setBookTitle("Be Intentional B");
        addBookRequest.setBookAuthor("Author One B");
        addBookRequest.setBookIsbn("rw63829wz-B");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(getResponse.getAccessLevel());
        AddBookResponse bookRequest = bookService.addBook(addBookRequest);
        assertEquals(bookRequest.getBookTitle(), addBookRequest.getBookTitle());

        AddBookRequest addBookAgain = new AddBookRequest();
        addBookAgain.setBookTitle("Be Intentional B");
        addBookAgain.setBookAuthor("Author One B");

        BookExistException exception = assertThrows(BookExistException.class, () ->
                bookService.bookAlreadyExist(addBookAgain));
        assertEquals("Book already exist! Adjust title or author", exception.getMessage());
        BookExistException exception1 = assertThrows(BookExistException.class, () ->
                bookService.addBook(addBookAgain));
        assertEquals("Book already exist! Adjust title or author", exception1.getMessage());
        }

    @Test
    public void test_That_Librarian_Cannot_Add_Book_With_Same_ISBN(){
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
        addBookRequest.setBookTitle("Be Intentional C");
        addBookRequest.setBookAuthor("Author One C");
        addBookRequest.setBookIsbn("rw63829wz-C");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(getResponse.getAccessLevel());
        AddBookResponse bookRequest = bookService.addBook(addBookRequest);
        assertEquals(bookRequest.getBookTitle(), addBookRequest.getBookTitle());

        AddBookRequest addIsbnBookAgain = new AddBookRequest();
        addIsbnBookAgain.setBookIsbn("rw63829wz-C");

        BookExistException exception = assertThrows(BookExistException.class, () ->
                bookService.addBook(addIsbnBookAgain));
        assertEquals("ISBN already exist!", exception.getMessage());
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
        addBookRequest.setBookTitle("Be Intentional D");
        addBookRequest.setBookAuthor("Author One D");
        addBookRequest.setBookIsbn("rw63829wz-D");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        assertNotNull(savedBookRequest.getId(), "Generated Book Id is gotten");
        assertEquals("Book added successfully", savedBookRequest.getAddBookMsg());

        Optional<AddBookRequest> findBookRequest = bookService.findBookById(savedBookRequest.getId());
        assertTrue(findBookRequest.isPresent(), "Book should be found");
        assertEquals(savedBookRequest.getId(), findBookRequest.get().getId());
        }

    @Test
    public void test_That_Book_Id_And_FICTION_Category_Can_Be_Saved_Into_Book_Shelve_Using_addBook_method() {
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
        addBookRequest.setBookTitle("Be Intentional E");
        addBookRequest.setBookAuthor("Author One E");
        addBookRequest.setBookIsbn("rw63829wz-E");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        assertNotNull(savedBookRequest.getId(), "Generated Book Id is gotten");
        assertEquals("Book added successfully", savedBookRequest.getAddBookMsg());

        Optional<AddBookRequest> findBookRequest = bookService.findBookById(savedBookRequest.getId());
        assertTrue(findBookRequest.isPresent(), "Book can be found");
        assertEquals(savedBookRequest.getId(), findBookRequest.get().getId());

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.FICTION);
        addShelveRequest.setBookId(savedBookRequest.getId());
        addShelveRequest.setBookGenre("Genre One");
        addShelveRequest.setAvailable(true);
        addShelveRequest.setBorrowed(false);
        AddShelveResponse shelveResponse = bookService.addShelveWithBookId(addShelveRequest);
        assertEquals("Book added to shelve successfully", shelveResponse.getAddShelveMsg());

        Optional<Shelve> shelve =shelveRepository.findByBookId(savedBookRequest.getId());
        assertTrue(shelve.isPresent(), "Shelve for this book can be found");
        assertEquals(ShelveType.FICTION, shelve.get().getCategory());
        assertTrue(shelve.get().isAvailable());
        assertFalse(shelve.get().isBorrowed());
        }

    @Test
    public void test_That_Book_Id_And_POETRY_Category_Can_Be_Saved_Into_Shelve(){
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
        addBookRequest.setBookTitle("Be Intentional F");
        addBookRequest.setBookAuthor("Author Two F");
        addBookRequest.setBookIsbn("rw63829wz-F");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        assertNotNull(savedBookRequest.getId(), "Generated Book Id is gotten");
        assertEquals("Book added successfully", savedBookRequest.getAddBookMsg());

        Optional<AddBookRequest> findBookRequest = bookService.findBookById(savedBookRequest.getId());
        assertTrue(findBookRequest.isPresent(), "Book can be found");
        assertEquals(savedBookRequest.getId(), findBookRequest.get().getId());

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.POETRY);
        addShelveRequest.setBookId(savedBookRequest.getId());
        addShelveRequest.setBookGenre("Genre One");
        addShelveRequest.setAvailable(true);
        addShelveRequest.setBorrowed(false);
        AddShelveResponse shelveResponse = bookService.addShelveWithBookId(addShelveRequest);
        assertEquals("Book added to shelve successfully", shelveResponse.getAddShelveMsg());

        Optional<Shelve> shelve =shelveRepository.findByBookId(savedBookRequest.getId());
        assertTrue(shelve.isPresent(), "Shelve for this book can be found");
        assertEquals(ShelveType.POETRY, shelve.get().getCategory());
        assertTrue(shelve.get().isAvailable());
        assertFalse(shelve.get().isBorrowed());
        }

    @Test
    public void test_That_Book_Id_And_EDUCATION_Category_Can_Be_Saved_Into_Book_Shelve_Using_addBookWithShelve_method() {
        Member member = new Member();
        member.setFullName("Librarian Learned");
        member.setEmail("durayg2000@yahoo.com");
        member.setPassword("greatness");
        member.setPhoneNumber("08027663871");
        member.setAddress("No. 34, Sabo, Yaba, Lagos.");
        member.setAccessLevel(20);
        member.setSessionStatus(true);
        memberRepository.save(member);
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
        addBookRequest.setBookTitle("Be Intentional FF");
        addBookRequest.setBookAuthor("Author Two FF");
        addBookRequest.setBookIsbn("rw63829wz-FF");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose FF");

        AddShelveRequest addShelveRequest = new AddShelveRequest();
        addShelveRequest.setBookCategory(ShelveType.EDUCATION);
        addShelveRequest.setBookGenre("Genre FF");

        AddBookResponse addBookResponse = bookService.addBookWithShelve(addBookRequest, addShelveRequest);
        assertNotNull(addBookResponse.getId(), "Book ID should not be null");
        assertEquals("Be Intentional FF", addBookResponse.getBookTitle());
        assertEquals("Author Two FF", addBookResponse.getBookAuthor());
        assertEquals("rw63829wz-FF", addBookResponse.getBookIsbn());
        assertEquals("Characterized by conscious design or purpose FF", addBookResponse.getBookDescription());

        Optional<Book> savedBook = bookRepository.findById(addBookResponse.getId());
        assertTrue(savedBook.isPresent(), "Saved book should be found in the repository");

        Optional<Shelve> savedShelve = shelveRepository.findByBookId(addBookResponse.getId());
        assertTrue(savedShelve.isPresent());
        assertEquals(addBookResponse.getId(), savedShelve.get().getBookId());
        assertEquals(ShelveType.EDUCATION, savedShelve.get().getCategory());
        assertEquals("Genre FF", savedShelve.get().getGenre());
        assertTrue(savedShelve.get().isAvailable());
        assertFalse(savedShelve.get().isBorrowed());
    }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_Update_Book() {
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

        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setSessionStatus(false);
        String title = updateBookRequest.getCurrentBookTitle();

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.updateBookByTitle(updateBookRequest, title));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
        }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Book_Using_Wrong_Access_Level() {
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

        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setSessionStatus(true);
        updateBookRequest.setAccessLevel(getResponse.getAccessLevel());
        String title = updateBookRequest.getCurrentBookTitle();

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.updateBookByTitle(updateBookRequest, title));
        assertEquals("You're not eligible to access this page", exception.getMessage());
        }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Book_If_Input_Book_Title_Not_found(){
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
        addBookRequest.setBookTitle("Be Intentional G");
        addBookRequest.setBookAuthor("Author Two G");
        addBookRequest.setBookIsbn("rw63829wz-G");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setCurrentBookTitle("Be Intentional G7");
        updateBookRequest.setBookTitle("Be Intentional G1");
        updateBookRequest.setBookAuthor("Author Two G1");
        updateBookRequest.setBookIsbn("rw63829wz-G1");
        updateBookRequest.setBookDescription("Updated Characterized by conscious design or purpose");
        updateBookRequest.setSessionStatus(true);
        updateBookRequest.setAccessLevel(20);
        String title = updateBookRequest.getCurrentBookTitle();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.updateBookByTitle(updateBookRequest, title));
        assertEquals("Book with the title '" + updateBookRequest.getCurrentBookTitle() + "' not found", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_Add_Book_Again_Using_Right_Access_Level() {
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
        addBookRequest.setBookTitle("Be Intentional H");
        addBookRequest.setBookAuthor("Author One H");
        addBookRequest.setBookIsbn("rw63829wz-H");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(getResponse.getAccessLevel());

        AddBookResponse bookResponse = bookService.addBook(addBookRequest);
        assertEquals(bookResponse.getBookTitle(), addBookRequest.getBookTitle());
        assertEquals("Book added successfully", bookResponse.getAddBookMsg());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Update_Book_By_Book_Title_If_Is_Not_Available() {
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

        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setCurrentBookTitle("Be Intentional GG");
        updateBookRequest.setBookTitle("Be Intentional G2");
        updateBookRequest.setBookAuthor("Author Two G2");
        updateBookRequest.setBookIsbn("rw63829wz-G2");
        updateBookRequest.setBookDescription("Updated Characterized by conscious design or purpose G2");
        updateBookRequest.setSessionStatus(true);
        updateBookRequest.setAccessLevel(20);
        String title = updateBookRequest.getCurrentBookTitle();

        BookInShelveNotAvailableException exception = assertThrows(BookInShelveNotAvailableException.class, () ->
                bookService.updateBookByTitle(updateBookRequest, title));
        assertEquals("Book is currently not available in the shelve", exception.getMessage());
        }

        @Test
    public void test_That_Librarian_Inside_Session_Can_Update_Book_By_Book_Title_If_Is_Available() {
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
        assertEquals(addBookResponse.getId(), savedShelve.get().getBookId());
        assertEquals(ShelveType.COMICS, savedShelve.get().getCategory());
        assertEquals("Genre GG", savedShelve.get().getGenre());
        assertTrue(savedShelve.get().isAvailable());
        assertFalse(savedShelve.get().isBorrowed());

        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        updateBookRequest.setCurrentBookTitle("Be Intentional GG");
        updateBookRequest.setBookTitle("Be Intentional G2");
        updateBookRequest.setBookAuthor("Author Two G2");
        updateBookRequest.setBookIsbn("rw63829wz-G2");
        updateBookRequest.setBookDescription("Updated Characterized by conscious design or purpose G2");
        updateBookRequest.setSessionStatus(true);
        updateBookRequest.setAccessLevel(20);
        String title = updateBookRequest.getCurrentBookTitle();

        UpdateBookResponse updateBookResponse = bookService.updateBookByTitle(updateBookRequest, title);
        assertEquals("Book updated successfully", updateBookResponse.getUpdateBookMsg());
        assertEquals("Be Intentional G2", updateBookResponse.getBookTitle());
        assertEquals("Author Two G2", updateBookResponse.getBookAuthor());
        assertEquals("rw63829wz-G2", updateBookResponse.getBookIsbn());
        assertEquals("Updated Characterized by conscious design or purpose G2", updateBookResponse.getBookDescription());

        Book updatedBook = bookRepository.findByTitle("Be Intentional G2");
        assertEquals("Be Intentional G2", updatedBook.getTitle());
        assertEquals("Author Two G2", updatedBook.getAuthor());
        assertEquals("rw63829wz-G2", updatedBook.getIsbn());
        assertEquals("Updated Characterized by conscious design or purpose G2", updatedBook.getDescription());
        }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_View_All_Books_For_Librarian() {
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

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.viewBookByAll(0,10));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Member_Not_In_Session_Cannot_View_All_Books_For_Member() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.viewBookByAllForMembers(0,10));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_View_All_Books_For_Librarian_Using_Wrong_Access_Level() {
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

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(true);
        viewBookResponse.setAccessLevel(getResponse.getAccessLevel());

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.viewBookByAll(0, 10));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_View_All_Books_For_Member_Using_Wrong_Access_Level() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(30);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(true);
        viewBookResponse.setAccessLevel(getResponse.getAccessLevel());

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.viewBookByAllForMembers(0, 10));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_View_All_Book_For_Librarian_Inside_Empty_Book_Repo(){
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

        bookRepository.deleteAll();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.viewBookByAll(0,10));
        assertEquals("Book shelve is currently empty", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_View_All_Book_For_Member_Inside_Empty_Book_Repo(){
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        bookRepository.deleteAll();

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.viewBookByAllForMembers(0,10));
        assertEquals("Book shelve is currently empty", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_View_All_Books_For_Librarian() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(20);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("durayg2000@yahoo.com");
        loginRequest.setPassword("greatness");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());
        assertTrue(getResponse.isSessionStatus());

        for (int addUpBook = 1; addUpBook <= 10; addUpBook++) {
            AddBookRequest addBookRequest = new AddBookRequest();
            addBookRequest.setBookTitle("Be Intentional " + addUpBook);
            addBookRequest.setBookAuthor("Author " + addUpBook);
            addBookRequest.setBookIsbn("ISBN-" + addUpBook);
            addBookRequest.setBookDescription("Characterized by conscious design or purpose " + addUpBook);
            addBookRequest.setSessionStatus(true);
            addBookRequest.setAccessLevel(20);
            AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
            assertEquals("Be Intentional " + addUpBook, savedBookRequest.getBookTitle());
        }

        int page = 0;
        int size = 4;
        ViewBookResponse viewBookResponse = bookService.viewBookByAll(page, size);

        assertEquals("Books retrieved successfully", viewBookResponse.getViewBookMsg());
        assertEquals(4, viewBookResponse.getBooks().size()); // 4 books on the first page
        assertEquals(0, viewBookResponse.getCurrentPage());
        assertEquals(3, viewBookResponse.getTotalPages()); // 10 books total, 3 pages (4, 4, and 2)
        assertEquals(10, viewBookResponse.getTotalBooks());
        assertEquals(4, viewBookResponse.getPageSize());

        for (int viewUpBook = 0; viewUpBook < viewBookResponse.getBooks().size(); viewUpBook++) {
            Book book = viewBookResponse.getBooks().get(viewUpBook);
            assertEquals("Be Intentional " + (viewUpBook + 1), book.getTitle());
            assertEquals("Author " + (viewUpBook + 1), book.getAuthor());
            assertEquals("ISBN-" + (viewUpBook+ 1), book.getIsbn());
            assertEquals("Characterized by conscious design or purpose " + (viewUpBook + 1), book.getDescription());
        }
    }

    @Test
    public void test_That_Member_Inside_Session_Can_View_All_Books_For_Member() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Librarian Learned");
        addMemberRequest.setEmail("durayg2000@yahoo.com");
        addMemberRequest.setPassword("greatness");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(20);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("durayg2000@yahoo.com");
        loginRequest.setPassword("greatness");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("durayg2000@yahoo.com", getResponse.getEmail());
        assertEquals("greatness", getResponse.getPassword());
        assertTrue(getResponse.isSessionStatus());

        for (int addUpBook = 1; addUpBook <= 10; addUpBook++) {
            AddBookRequest addBookRequest = new AddBookRequest();
            addBookRequest.setBookTitle("Be Intentional " + addUpBook);
            addBookRequest.setBookAuthor("Author " + addUpBook);
            addBookRequest.setBookIsbn("ISBN-" + addUpBook);
            addBookRequest.setBookDescription("Characterized by conscious design or purpose " + addUpBook);
            addBookRequest.setSessionStatus(true);
            addBookRequest.setAccessLevel(20);
            AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
            assertEquals("Be Intentional " + addUpBook, savedBookRequest.getBookTitle());
        }

        memberRepository.deleteAll();
        Member addMemberRequest1 = new Member();
        addMemberRequest1.setFullName("Ade Bravo");
        addMemberRequest1.setEmail("twinebravo@gmail.com");
        addMemberRequest1.setPassword("tybravo");
        addMemberRequest1.setPhoneNumber("08027663871");
        addMemberRequest1.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest1.setAccessLevel(10);
        addMemberRequest1.setSessionStatus(false);
        memberRepository.save(addMemberRequest1);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setEmail("twinebravo@gmail.com");
        loginRequest1.setPassword("tybravo");
        loginRequest1.setSessionStatus(true);
        Member getResponse1 = memberService.loginMember(loginRequest1);
        assertEquals("twinebravo@gmail.com", getResponse1.getEmail());
        assertEquals("tybravo", getResponse1.getPassword());

        int page = 0;
        int size = 4;
        ViewBookResponse viewBookResponse = bookService.viewBookByAllForMembers(page, size);

        assertEquals("Books retrieved successfully", viewBookResponse.getViewBookMsg());
        assertEquals(4, viewBookResponse.getBooks().size()); // 4 books on the first page
        assertEquals(0, viewBookResponse.getCurrentPage());
        assertEquals(3, viewBookResponse.getTotalPages()); // 10 books total, 3 pages (4, 4, and 2)
        assertEquals(10, viewBookResponse.getTotalBooks());
        assertEquals(4, viewBookResponse.getPageSize());

        for (int viewUpBook = 0; viewUpBook < viewBookResponse.getBooks().size(); viewUpBook++) {
            Book book = viewBookResponse.getBooks().get(viewUpBook);
            assertEquals("Be Intentional " + (viewUpBook + 1), book.getTitle());
            assertEquals("Author " + (viewUpBook + 1), book.getAuthor());
            assertEquals("ISBN-" + (viewUpBook+ 1), book.getIsbn());
            assertEquals("Characterized by conscious design or purpose " + (viewUpBook + 1), book.getDescription());
        }
    }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_View_Book_By_Title() {
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

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.viewBookByTitle("Be Intentional 2"));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Member_Not_In_Session_Cannot_View_Book_By_Title_For_Members() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(false);
        String title = "Be Intentional";

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.viewBookByTitleForMembers(title));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_View_Book_By_Title_Using_Wrong_Access_Level() {
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

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(true);
        viewBookResponse.setAccessLevel(getResponse.getAccessLevel());

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.viewBookByTitle("Be Intentional 2"));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_View_Book_By_Title_Using_Wrong_Access_Level_For_Member() {
        Member addMemberRequest = new Member();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("08027663871");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(30);
        addMemberRequest.setSessionStatus(false);
        memberRepository.save(addMemberRequest);
        AddMemberResponse response = new AddMemberResponse();
        response.setRegMsg("Registration successful");
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");
        loginRequest.setSessionStatus(true);
        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(true);
        viewBookResponse.setAccessLevel(getResponse.getAccessLevel());

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.viewBookByTitleForMembers("Be Intentional 2"));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_View_Non_Existing_Book_By_Title_Inside_Book_Repo(){
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
        addBookRequest.setBookTitle("Be Intentional J");
        addBookRequest.setBookAuthor("Author Two J");
        addBookRequest.setBookIsbn("rw63829wz-J");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose J");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        String findTitleOfBook = "Be Intentional 11";
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.viewBookByTitle(findTitleOfBook));
        assertEquals("Book with title '" + findTitleOfBook + "' cannot be found.", exception.getMessage());
    }

    @Test
    public void test_That_Member_Inside_Session_Cannot_View_Non_Existing_Book_By_Title_Inside_Book_Repo_For_Members(){
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
        addBookRequest.setBookTitle("Be Intentional J");
        addBookRequest.setBookAuthor("Author Two J");
        addBookRequest.setBookIsbn("rw63829wz-J");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose J");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

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
        loginRequest1.setSessionStatus(true);
        Member getResponse1 = memberService.loginMember(loginRequest1);
        assertEquals("twinebravo@gmail.com", getResponse1.getEmail());
        assertEquals("tybravo", getResponse1.getPassword());

        String findTitleOfBook = "Be Intentional 11";
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.viewBookByTitleForMembers(findTitleOfBook));
        assertEquals("Book with title '" + findTitleOfBook + "' cannot be found.", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Find_BookId_Inside_Shelve_Repository(){
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
        addBookRequest.setBookTitle("Be Intentional J");
        addBookRequest.setBookAuthor("Author Two J");
        addBookRequest.setBookIsbn("rw63829wz-J");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose J");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());
        assertNotNull(savedBookRequest.getId(), "Generated Book Id is gotten");
        assertEquals("Book added successfully", savedBookRequest.getAddBookMsg());

        Optional<AddBookRequest> findBookRequest = bookService.findBookById(savedBookRequest.getId());
        assertTrue(findBookRequest.isPresent(), "Book can be found");
        assertEquals(savedBookRequest.getId(), findBookRequest.get().getId());
        assertEquals(savedBookRequest.getBookTitle(), findBookRequest.get().getBookTitle());

        Optional<Book> pullBookRequest = bookRepository.findById(savedBookRequest.getId());
        assertTrue(pullBookRequest.isPresent(), "Book can be found");

        ShelveNotFoundException exception = assertThrows(ShelveNotFoundException.class, () ->
                bookService.viewBookByTitle("Be Intentional J"));
        assertEquals("Shelve entry for the book entered not found.", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_View_Book_By_Title() {
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
        addBookRequest.setBookTitle("Be Intentional 11");
        addBookRequest.setBookAuthor("Author 11");
        addBookRequest.setBookIsbn("rw63829wz-11");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose 11");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        Shelve shelve = new Shelve();
        shelve.setBookId(savedBookRequest.getId());
        shelve.setCategory(ShelveType.FICTION);
        shelve.setGenre("Genre 11");
        shelve.setAvailable(true);
        shelve.setBorrowed(false);
        shelveRepository.save(shelve);

        ViewBookResponse viewBookResponse = bookService.viewBookByTitle("Be Intentional 11");
        assertEquals("Be Intentional 11", viewBookResponse.getBookTitle());
        assertEquals("Author 11", viewBookResponse.getBookAuthor());
        assertEquals("rw63829wz-11", viewBookResponse.getBookIsbn());
        assertEquals("Characterized by conscious design or purpose 11", viewBookResponse.getBookDescription());
        assertNotNull(viewBookResponse.getCreationDate());

        assertEquals(ShelveType.FICTION, viewBookResponse.getBookCategory());
        assertEquals("Genre 11", viewBookResponse.getBookGenre());
        assertTrue(viewBookResponse.isAvailable());
        assertFalse(viewBookResponse.isBorrowed());
    }

    @Test
    public void test_That_Member_Inside_Session_Can_View_Book_By_Title_For_Members() {
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
        addBookRequest.setBookTitle("Be Intentional 11");
        addBookRequest.setBookAuthor("Author 11");
        addBookRequest.setBookIsbn("rw63829wz-11");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose 11");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        Shelve shelve = new Shelve();
        shelve.setBookId(savedBookRequest.getId());
        shelve.setCategory(ShelveType.FICTION);
        shelve.setGenre("Genre 11");
        shelve.setAvailable(true);
        shelve.setBorrowed(false);
        shelveRepository.save(shelve);

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
        loginRequest1.setSessionStatus(true);
        Member getResponse1 = memberService.loginMember(loginRequest1);
        assertEquals("twinebravo@gmail.com", getResponse1.getEmail());
        assertEquals("tybravo", getResponse1.getPassword());

        ViewBookResponse viewBookResponse = bookService.viewBookByTitleForMembers("Be Intentional 11");
        assertEquals("Be Intentional 11", viewBookResponse.getBookTitle());
        assertEquals("Author 11", viewBookResponse.getBookAuthor());
        assertEquals("Characterized by conscious design or purpose 11", viewBookResponse.getBookDescription());
        assertNotNull(viewBookResponse.getCreationDate());

        assertEquals(ShelveType.FICTION, viewBookResponse.getBookCategory());
        assertEquals("Genre 11", viewBookResponse.getBookGenre());
        assertTrue(viewBookResponse.isAvailable());
        assertFalse(viewBookResponse.isBorrowed());
    }

    @Test
    public void test_That_Librarian_Not_In_Session_Cannot_Delete_Book_By_Title() {
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

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(false);

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.deleteBookByTitle("Be Intentional 2"));
        assertEquals("Not in session or currently logged out!", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Delete_Book_By_Title_Using_Wrong_Access_Level() {
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

        ViewBookResponse viewBookResponse = new ViewBookResponse();
        viewBookResponse.setSessionStatus(true);
        viewBookResponse.setAccessLevel(getResponse.getAccessLevel());

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.deleteBookByTitle("Be Intentional 2"));
        assertEquals("You're not eligible to access this page", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Delete_Non_Existing_Book_By_Title_Inside_Book_Repo(){
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
        addBookRequest.setBookTitle("Be Intentional J");
        addBookRequest.setBookAuthor("Author Two J");
        addBookRequest.setBookIsbn("rw63829wz-J");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose J");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        String findTitleOfBook = "Be Intentional 11";
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.viewBookByTitle(findTitleOfBook));
        assertEquals("Book with title '" + findTitleOfBook + "' cannot be found.", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Cannot_Delete_Book_By_Title_If_Is_Not_Set_Available() {
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
        addBookRequest.setBookTitle("Be Intentional 11");
        addBookRequest.setBookAuthor("Author 11");
        addBookRequest.setBookIsbn("rw63829wz-11");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose 11");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        AddBookRequest addBookRequest1 = new AddBookRequest();
        addBookRequest1.setBookTitle("Be Intentional 22");
        addBookRequest1.setBookAuthor("Author 22");
        addBookRequest1.setBookIsbn("rw63829wz-22");
        addBookRequest1.setBookDescription("Characterized by conscious design or purpose 22");
        addBookRequest1.setSessionStatus(true);
        addBookRequest1.setAccessLevel(20);
        AddBookResponse savedBookRequest1 = bookService.addBook(addBookRequest1);
        assertEquals(savedBookRequest1.getBookTitle(), addBookRequest1.getBookTitle());

        Shelve shelve = new Shelve();
        shelve.setBookId(savedBookRequest.getId());
        shelve.setCategory(ShelveType.FICTION);
        shelve.setGenre("Genre 11");
        shelve.setAvailable(false);
        shelve.setBorrowed(false);
        shelveRepository.save(shelve);
        assertEquals("Genre 11", shelve.getGenre());

        Shelve shelve1 = new Shelve();
        shelve1.setBookId(savedBookRequest1.getId());
        shelve1.setCategory(ShelveType.EDUCATION);
        shelve1.setGenre("Genre 22");
        shelve1.setAvailable(true);
        shelve1.setBorrowed(false);
        shelveRepository.save(shelve1);
        assertEquals("Genre 22", shelve1.getGenre());

        BookInShelveNotAvailableException exception = assertThrows(BookInShelveNotAvailableException.class, () ->
                bookService.deleteBookByTitle("Be Intentional 11"));
        assertEquals("Book is currently not available in the shelve", exception.getMessage());
    }

    @Test
    public void test_That_Librarian_Inside_Session_Can_Delete_Book_By_Title_If_Is_Set_Available() {
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
        addBookRequest.setBookTitle("Be Intentional 11");
        addBookRequest.setBookAuthor("Author 11");
        addBookRequest.setBookIsbn("rw63829wz-11");
        addBookRequest.setBookDescription("Characterized by conscious design or purpose 11");
        addBookRequest.setSessionStatus(true);
        addBookRequest.setAccessLevel(20);
        AddBookResponse savedBookRequest = bookService.addBook(addBookRequest);
        assertEquals(savedBookRequest.getBookTitle(), addBookRequest.getBookTitle());

        AddBookRequest addBookRequest1 = new AddBookRequest();
        addBookRequest1.setBookTitle("Be Intentional 22");
        addBookRequest1.setBookAuthor("Author 22");
        addBookRequest1.setBookIsbn("rw63829wz-22");
        addBookRequest1.setBookDescription("Characterized by conscious design or purpose 22");
        addBookRequest1.setSessionStatus(true);
        addBookRequest1.setAccessLevel(20);
        AddBookResponse savedBookRequest1 = bookService.addBook(addBookRequest1);
        assertEquals(savedBookRequest1.getBookTitle(), addBookRequest1.getBookTitle());

        Shelve shelve = new Shelve();
        shelve.setBookId(savedBookRequest.getId());
        shelve.setCategory(ShelveType.FICTION);
        shelve.setGenre("Genre 11");
        shelve.setAvailable(true);
        shelve.setBorrowed(false);
        shelveRepository.save(shelve);
        assertEquals("Genre 11", shelve.getGenre());

        Shelve shelve1 = new Shelve();
        shelve1.setBookId(savedBookRequest1.getId());
        shelve1.setCategory(ShelveType.EDUCATION);
        shelve1.setGenre("Genre 22");
        shelve1.setAvailable(true);
        shelve1.setBorrowed(false);
        shelveRepository.save(shelve1);
        assertEquals("Genre 22", shelve1.getGenre());

        DeleteBookResponse deletedBookResponse = bookService.deleteBookByTitle("Be Intentional 11");
        assertNotNull(deletedBookResponse);
        assertEquals("Book deleted successfully", deletedBookResponse.getDeleteBookMsg());

        Optional<Book> deletedBook = bookRepository.findById(savedBookRequest.getId());
        assertFalse(deletedBook.isPresent());
        Optional<Shelve> shelves = shelveRepository.findByBookId(savedBookRequest.getId());
        assertTrue(shelves.isEmpty());
    }

}