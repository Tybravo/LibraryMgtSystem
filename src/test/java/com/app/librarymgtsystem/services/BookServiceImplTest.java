package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.*;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.AddShelveResponse;
import com.app.librarymgtsystem.dtos.responses.UpdateBookResponse;
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
    public void test_That_Book_Id_And_FICTION_Category_Can_Be_Saved_Into_Shelve() {
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
        addShelveRequest.setCategory(ShelveType.FICTION);
        addShelveRequest.setBookId(savedBookRequest.getId());
        addShelveRequest.setGenre("Genre One");
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
        addShelveRequest.setCategory(ShelveType.POETRY);
        addShelveRequest.setBookId(savedBookRequest.getId());
        addShelveRequest.setGenre("Genre One");
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

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                bookService.updateBookByTitle(updateBookRequest));
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

        NotEligiblePageException exception = assertThrows(NotEligiblePageException.class, () ->
                bookService.updateBookByTitle(updateBookRequest));
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

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                bookService.updateBookByTitle(updateBookRequest));
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
    public void test_That_Librarian_Inside_Session_Can_Update_Book_By_Book_Title() {
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
        updateBookRequest.setCurrentBookTitle("Be Intentional G");
        updateBookRequest.setBookTitle("Be Intentional G2");
        updateBookRequest.setBookAuthor("Author Two G2");
        updateBookRequest.setBookIsbn("rw63829wz-G2");
        updateBookRequest.setBookDescription("Updated Characterized by conscious design or purpose");
        updateBookRequest.setSessionStatus(true);
        updateBookRequest.setAccessLevel(20);

        UpdateBookResponse updateBookResponse = bookService.updateBookByTitle(updateBookRequest);
        assertEquals("Book updated successfully", updateBookResponse.getUpdateBookMsg());
        assertEquals("Be Intentional G2", updateBookResponse.getBookTitle());
        assertEquals("Author Two G2", updateBookResponse.getBookAuthor());
        assertEquals("rw63829wz-G2", updateBookResponse.getBookIsbn());
        assertEquals("Updated Characterized by conscious design or purpose", updateBookResponse.getBookDescription());

        Book updatedBook = bookRepository.findByTitle("Be Intentional G2");
        assertEquals("Be Intentional G2", updatedBook.getTitle());
        assertEquals("Author Two G2", updatedBook.getAuthor());
        assertEquals("rw63829wz-G2", updatedBook.getIsbn());
        assertEquals("Updated Characterized by conscious design or purpose", updatedBook.getDescription());
    }

}