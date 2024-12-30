package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.requests.AddShelveRequest;
import com.app.librarymgtsystem.dtos.requests.UpdateBookRequest;
import com.app.librarymgtsystem.dtos.responses.*;
import com.app.librarymgtsystem.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShelveRepository shelveRepository;


    @Override
    public boolean findMemberSession(String email) {
        // Step 1: Fetch member by email
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new EmailNotFoundException("Member email not found");
        }
        // Step 2: Check session status
        Member member = optionalMember.get();
        if (!member.isSessionStatus()) {
            throw new NotInSessionException("Membership account not found");
        }
        // Step 3: Proceed with further actions (return true if all checks pass)
        return true;
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
        if (addEmptyBook.getBookTitle() == null || addEmptyBook.getBookTitle().isEmpty() ||
                addEmptyBook.getBookAuthor() == null || addEmptyBook.getBookAuthor().isEmpty() ||
                addEmptyBook.getBookIsbn() == null || addEmptyBook.getBookIsbn().isEmpty() ||
                addEmptyBook.getBookDescription() == null || addEmptyBook.getBookDescription().isEmpty()) {
            throw new BookCannotBeEmptyException("Book detail cannot be empty!");
        }
    }


    @Override
    public boolean bookAlreadyExist(AddBookRequest addBookRequest) {
        Book pullBook = findBookByTitle(addBookRequest.getBookTitle());
        Book pullAuthor = findBookByAuthor(addBookRequest.getBookAuthor());
        if (pullBook != null && pullBook.getTitle().equals(addBookRequest.getBookTitle()) &&
                pullAuthor != null && pullAuthor.getAuthor().equals(addBookRequest.getBookAuthor())) {
            throw new BookExistException("Book already exist! Adjust title or author");
        }
        return false;
    }


    @Override
    public boolean isbnAlreadyExist(AddBookRequest addBookRequest) {
        Book pullIsbn = findBookByIsbn(addBookRequest.getBookIsbn());
        if (pullIsbn != null && pullIsbn.getIsbn().equals(addBookRequest.getBookIsbn())) {
            throw new BookExistException("ISBN already exist!");
        }
        return false;
    }


    @Override
    public AddBookResponse addBook(AddBookRequest addBookRequest, String email) {
        AddBookResponse addBookResponse = new AddBookResponse();
        if (findMemberSession(email) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(String.valueOf(true)) && findMemberAccessLevel(20)) {

            if (bookAlreadyExist(addBookRequest)) {
                throw new BookExistException("Book already exist! Adjust title or author");
            }
            if (isbnAlreadyExist(addBookRequest)) {
                throw new BookExistException("ISBN already exist");
            }
            bookCannotBeEmpty(addBookRequest);
                Book book = new Book();
                book.setTitle(addBookRequest.getBookTitle());
                book.setAuthor(addBookRequest.getBookAuthor());
                book.setIsbn(addBookRequest.getBookIsbn());
                book.setDescription(addBookRequest.getBookDescription());
                bookRepository.save(book);
                addBookRequest.setId(book.getId());
                addBookRequest.setBookTitle(book.getTitle());

                addBookResponse.setAddBookMsg("Book added successfully");
                addBookResponse.setId(book.getId());
                addBookResponse.setBookTitle(book.getTitle());
                addBookResponse.setBookAuthor(book.getAuthor());
                addBookResponse.setBookIsbn(book.getIsbn());
                addBookResponse.setBookDescription(book.getDescription());
                addBookResponse.setCreationDate(book.getCreationDate());
            }
        return addBookResponse;
    }


    @Override
    public AddBookResponse addBookWithShelve(AddBookRequest addBookRequest, AddShelveRequest addShelveRequest, String email) {
        if (!findMemberSession(email) || !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (bookAlreadyExist(addBookRequest)) {
            throw new BookExistException("Book already exists! Adjust title or author.");
        }
        if (isbnAlreadyExist(addBookRequest)) {
            throw new BookExistException("ISBN already exists!");
        }

        bookCannotBeEmpty(addBookRequest);
        Book book = new Book();
            book.setTitle(addBookRequest.getBookTitle());
            book.setAuthor(addBookRequest.getBookAuthor());
            book.setIsbn(addBookRequest.getBookIsbn());
            book.setDescription(addBookRequest.getBookDescription());
            bookRepository.save(book);

            addShelveRequest.setBookId(book.getId());
            Optional<Book> foundBook = bookRepository.findById(addShelveRequest.getBookId());
            if (foundBook.isPresent()) {
                Shelve shelve = new Shelve();
                shelve.setBookId(addShelveRequest.getBookId());
                shelve.setCategory(addShelveRequest.getBookCategory());
                shelve.setGenre(addShelveRequest.getBookGenre());
                shelve.setAvailable(true);
                shelve.setBorrowed(false);
                shelveRepository.save(shelve);


                AddBookResponse addBookResponse = new AddBookResponse();
                addBookResponse.setAddBookMsg("Book added successfully");
                addBookResponse.setId(book.getId());
                addBookResponse.setBookTitle(book.getTitle());
                addBookResponse.setBookAuthor(book.getAuthor());
                addBookResponse.setBookIsbn(book.getIsbn());
                addBookResponse.setBookDescription(book.getDescription());
                addBookResponse.setCreationDate(book.getCreationDate());
                return addBookResponse;
            }
        throw new LoginEmailException("Unexpected error: Member email not found");
    }


    @Override
    public AddShelveResponse addShelveWithBookId(AddShelveRequest addShelveRequest) {
        Optional<Book> book = bookRepository.findById(addShelveRequest.getBookId());
        if (book.isEmpty()) {
            throw new BookNotFoundException("Book not found to store in shelve");
        }
        Shelve shelve = new Shelve();
        shelve.setBookId(addShelveRequest.getBookId());
        shelve.setCategory(addShelveRequest.getBookCategory());
        shelve.setGenre(addShelveRequest.getBookGenre());
        shelve.setAvailable(true);
        shelve.setBorrowed(false);
        shelveRepository.save(shelve);

        AddShelveResponse addShelveResponse = new AddShelveResponse();
        addShelveResponse.setAddShelveMsg("Book added to shelve successfully");
        return addShelveResponse;
    }


    @Override
    public Optional<AddBookRequest> findBookById(String findBook) {
        Optional<Book> getBook = bookRepository.findById(findBook);
        if (getBook.isPresent()) {
            Book book = getBook.get();
            AddBookRequest addBookRequest = new AddBookRequest();
            addBookRequest.setId(book.getId());
            addBookRequest.setBookTitle(book.getTitle());
            return Optional.of(addBookRequest);
        }
        return Optional.empty();
    }


    @Override
    public Shelve findShelveByCategory(ShelveType category) {
        return null;
    }


    @Override
    public UpdateBookResponse updateBookByTitle(UpdateBookRequest updateBookRequest, String email) {
        UpdateBookResponse updateBookResponse = new UpdateBookResponse();

        if (findMemberSession(email) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(email) && findMemberAccessLevel(20)) {

            Book foundbook = findBookByTitle(updateBookRequest.getCurrentBookTitle());
            if (foundbook == null) {
                throw new BookNotFoundException("Book with the title '" + updateBookRequest.getCurrentBookTitle() + "' not found");
            }
            if (updateBookRequest.getBookTitle() != null && updateBookRequest.getBookAuthor() != null && updateBookRequest.getBookIsbn() != null && updateBookRequest.getBookDescription() != null) {
                foundbook.setTitle(updateBookRequest.getBookTitle());
                foundbook.setAuthor(updateBookRequest.getBookAuthor());
                foundbook.setIsbn(updateBookRequest.getBookIsbn());
                foundbook.setDescription(updateBookRequest.getBookDescription());

                bookRepository.save(foundbook);
                updateBookResponse.setUpdateBookMsg("Book updated successfully");
                updateBookResponse.setId(foundbook.getId());
                updateBookResponse.setBookTitle(foundbook.getTitle());
                updateBookResponse.setBookAuthor(foundbook.getAuthor());
                updateBookResponse.setBookIsbn(foundbook.getIsbn());
                updateBookResponse.setBookDescription(foundbook.getDescription());
            }
        }
        return updateBookResponse;
    }

    @Override
    public ViewBookResponse viewBookByAll(int page, int size, String email) {
        ViewBookResponse viewBookResponse = new ViewBookResponse();
        if (!findMemberSession(email)){
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(email) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(email) && findMemberAccessLevel(20)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> booksPage = bookRepository.findAll(pageable);

            if (booksPage.isEmpty()) {
                throw new BookNotFoundException("Book shelve is currently empty");
            }

            List<Book> bookResponses = booksPage.getContent().stream()
                    .map(book -> {
                        Book bookResponse = new Book();
                        bookResponse.setId(book.getId());
                        bookResponse.setTitle(book.getTitle());
                        bookResponse.setAuthor(book.getAuthor());
                        bookResponse.setIsbn(book.getIsbn());
                        bookResponse.setDescription(book.getDescription());
                        bookResponse.setCreationDate(book.getCreationDate());
                        return bookResponse;
                    })
                    .collect(Collectors.toList());

            viewBookResponse.setBooks(bookResponses);
            viewBookResponse.setCurrentPage(booksPage.getNumber());
            viewBookResponse.setTotalPages(booksPage.getTotalPages());
            viewBookResponse.setTotalBooks(booksPage.getTotalElements());
            viewBookResponse.setPageSize(booksPage.getSize());
            viewBookResponse.setBookTitle(bookResponses.getFirst().getTitle());
            viewBookResponse.setViewBookMsg("Books retrieved successfully");
        }
        return viewBookResponse;
    }

    @Override
    public ViewBookResponse viewBookForMembersByAll(int page, int size, String email) {
        ViewBookResponse viewBookResponse = new ViewBookResponse();
        if (!findMemberSession(email)){
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(email) && !findMemberAccessLevel(10)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(email) && findMemberAccessLevel(10)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> booksPage = bookRepository.findAll(pageable);

            if (booksPage.isEmpty()) {
                throw new BookNotFoundException("Book shelve is currently empty");
            }

            List<Book> bookResponses = booksPage.getContent().stream()
                    .map(book -> {
                        Book bookResponse = new Book();
                        bookResponse.setId(book.getId());
                        bookResponse.setTitle(book.getTitle());
                        bookResponse.setAuthor(book.getAuthor());
                        bookResponse.setIsbn(book.getIsbn());
                        bookResponse.setDescription(book.getDescription());
                        bookResponse.setCreationDate(book.getCreationDate());
                        return bookResponse;
                    })
                    .collect(Collectors.toList());

            viewBookResponse.setBooks(bookResponses);
            viewBookResponse.setCurrentPage(booksPage.getNumber());
            viewBookResponse.setTotalPages(booksPage.getTotalPages());
            viewBookResponse.setTotalBooks(booksPage.getTotalElements());
            viewBookResponse.setPageSize(booksPage.getSize());
            viewBookResponse.setBookTitle(bookResponses.getFirst().getTitle());
            viewBookResponse.setViewBookMsg("Books retrieved successfully");
        }
        return viewBookResponse;
    }


    @Override
    public ViewBookResponse viewBookByTitle(String title, String email) {
        ViewBookResponse bookResponse = new ViewBookResponse();
        if (!findMemberSession(email)){
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(email) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(email) && findMemberAccessLevel(20)) {
            Shelve shelve = findShelveByBookTitle(title);
            String bookId = shelve.getBookId();

            Optional<Book> optionalBook = bookRepository.findById(bookId);
            if (optionalBook.isEmpty()) {
                throw new BookNotFoundException("Book cannot be found.");
            }

            Book book = optionalBook.get();
            bookResponse.setBookTitle(book.getTitle());
            bookResponse.setBookAuthor(book.getAuthor());
            bookResponse.setBookIsbn(book.getIsbn());
            bookResponse.setBookDescription(book.getDescription());
            bookResponse.setCreationDate(book.getCreationDate());

            bookResponse.setBookCategory(shelve.getCategory());
            bookResponse.setBookGenre(shelve.getGenre());
            bookResponse.setAvailable(shelve.isAvailable());
            bookResponse.setBorrowed(shelve.isBorrowed());
            return bookResponse;
        }
        return bookResponse;
    }


    @Override
    public DeleteBookResponse deleteBookByTitle(String title, String email) {
        DeleteBookResponse deleteBookResponse = new DeleteBookResponse();
        if (!findMemberSession(email)){
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(email) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(email)) {
            Shelve shelve = findShelveByBookTitle(title);
            String bookId = shelve.getBookId();

            Optional<Book> optionalBook = bookRepository.findById(bookId);
            if (optionalBook.isEmpty()) {
                throw new BookNotFoundException("Book cannot be found.");
            }

            Book book = optionalBook.get();
            shelveRepository.delete(shelve);
            bookRepository.delete(book);
            deleteBookResponse.setDeleteBookMsg("Book deleted successfully");
        }
        return deleteBookResponse;
    }


    private Shelve findShelveByBookTitle(String title) {
        Optional<Book> optionalBook = bookRepository.findByTitleIgnoreCase(title);
        if (optionalBook.isEmpty()) {
            throw new BookNotFoundException("Book with title '" + title + "' cannot be found.");
        }
        Book book = optionalBook.get();
        String bookId = book.getId();
        Optional<Shelve> optionalShelve = shelveRepository.findByBookId(bookId);
        if (optionalShelve.isEmpty()) {
            throw new ShelveNotFoundException("Shelve entry for the book entered not found.");
        }
        return optionalShelve.get();
    }

    private Shelve findShelveByBookTitleAvailable(String title) {
        Optional<Book> optionalBook = bookRepository.findByTitleIgnoreCase(title);
        if (optionalBook.isEmpty()) {
            throw new BookNotFoundException("Book with title '" + title + "' cannot be found.");
        }
        Book book = optionalBook.get();
        String bookId = book.getId();
        Optional<Shelve> optionalShelve = shelveRepository.findByBookId(bookId);
        if (optionalShelve.isEmpty()) {
            throw new ShelveNotFoundException("Shelve entry for the book entered not found.");
        }
        Shelve shelve = optionalShelve.get();
        if (!shelve.isAvailable()) {
            throw new BookInShelveNotAvailableException("Book is currently not available in the shelve");
        }
        return shelve;
    }


}

