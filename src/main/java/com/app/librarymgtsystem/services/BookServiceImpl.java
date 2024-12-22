package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
import com.app.librarymgtsystem.dtos.responses.AddShelveResponse;
import com.app.librarymgtsystem.exceptions.BookCannotBeEmptyException;
import com.app.librarymgtsystem.exceptions.BookExistException;
import com.app.librarymgtsystem.exceptions.NotEligiblePageException;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

@Autowired
    private BookRepository bookRepository;
@Autowired
    private MemberRepository memberRepository;
@Autowired
    private ShelveRepository shelveRepository;


    @Override
    public boolean findMemberSession(Boolean sessionStatus) {
    Member member = memberRepository.findBySessionStatus(true);
    if (member == null) {
        throw new NotInSessionException("Not in session or currently logged out!");
    }
    return member.isSessionStatus();
    }

    public String getMemberEmail() {
        Member currentMember = memberRepository.findBySessionStatus(true);
        if (currentMember != null) {
            return currentMember.getEmail();
        } else {
            throw new NotInSessionException("Membership account not found");
        }
    }

    @Override
    public boolean findMemberAccessLevel(int accessLevel) {
        return memberRepository.findByAccessLevel(accessLevel) != null;
    }

    @Override
    public Book findBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public Book findBookByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public void bookCannotBeEmpty(AddBookRequest addEmptyBook) {
        if(addEmptyBook.getBookTitle() == null || addEmptyBook.getBookTitle().isEmpty() ||
                addEmptyBook.getBookAuthor() == null || addEmptyBook.getBookAuthor().isEmpty() ||
                    addEmptyBook.getBookIsbn() == null || addEmptyBook.getBookIsbn().isEmpty() ||
                        addEmptyBook.getBookDescription() == null || addEmptyBook.getBookDescription().isEmpty()){
                            throw new BookCannotBeEmptyException("Book detail cannot be empty!");
        }
    }

    @Override
    public boolean bookAlreadyExist(AddBookRequest addBookRequest) {
        Book pullBook = findBookByTitle(addBookRequest.getBookTitle());
        Book pullAuthor = findBookByAuthor(addBookRequest.getBookAuthor());
        if( pullBook != null && pullBook.getTitle().equals(addBookRequest.getBookTitle()) &&
                pullAuthor != null && pullAuthor.getAuthor().equals(addBookRequest.getBookAuthor())){
            throw new BookExistException("Book already exist! Adjust title or author");
        }
        return false;
    }

    @Override
    public boolean isbnAlreadyExist(AddBookRequest addBookRequest) {
        Book pullIsbn = findBookByIsbn(addBookRequest.getBookIsbn());
        if(pullIsbn != null && pullIsbn.getIsbn().equals(addBookRequest.getBookIsbn())){
            throw new BookExistException("ISBN already exist!");
        }
        return false;
    }


    @Override
    public AddBookResponse addBook(AddBookRequest addBookRequest) {
        AddBookResponse addBookResponse = new AddBookResponse();
        if(findMemberSession(true) && !findMemberAccessLevel(20)){
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if(findMemberSession(true) && findMemberAccessLevel(20)){
            String memberEmail = getMemberEmail();
            bookCannotBeEmpty(addBookRequest);

            if(bookAlreadyExist(addBookRequest)) {
                throw new BookExistException("Book already exist! Adjust title or author");
            }
                if(isbnAlreadyExist(addBookRequest)) {
                    throw new BookExistException("ISBN already exist");
                }
            if (memberEmail != null) {
                Book book = new Book();
                book.setTitle(addBookRequest.getBookTitle());
                book.setAuthor(addBookRequest.getBookAuthor());
                book.setIsbn(addBookRequest.getBookIsbn());
                book.setDescription(addBookRequest.getBookDescription());
                bookRepository.save(book);
                addBookRequest.setId(book.getId());

                addBookResponse.setAddBookMsg("Book added successfully");
                addBookResponse.setId(book.getId());
                addBookResponse.setBookTitle(book.getTitle());
                addBookResponse.setBookAuthor(book.getAuthor());
                addBookResponse.setBookIsbn(book.getIsbn());
                addBookResponse.setBookDescription(book.getDescription());
                addBookResponse.setCreationDate(book.getCreationDate());
            }
        }
        return addBookResponse;
    }

    @Override
    public Optional<AddBookRequest> findBookId(String findBook) {
        Optional<Book> getBook = bookRepository.findById(findBook);
        return getBook.map(book -> {
            AddBookRequest addBookRequest = new AddBookRequest();
            addBookRequest.setId(book.getId());
            addBookRequest.setBookTitle(book.getTitle());
            return addBookRequest;
        });
    }

    @Override
    public AddShelveResponse addShelveWithBookId(AddShelveRequest addShelveRequest) {
        Shelve shelve = new Shelve();
        shelve.setCategory(addShelveRequest.getCategory());
        shelve.setBookId(addShelveRequest.getBookId());
        shelve.setGenre(addShelveRequest.getGenre());
        shelve.setAvailable(true);
        shelve.setBorrowed(false);
        shelveRepository.save(shelve);

        AddShelveResponse addShelveResponse = new AddShelveResponse();
        addShelveResponse.setAddShelveMsg("Book added to shelve successfully");
        return addShelveResponse;
    }



}

