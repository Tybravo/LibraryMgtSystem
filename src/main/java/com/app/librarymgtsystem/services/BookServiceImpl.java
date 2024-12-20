package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddBookRequest;
import com.app.librarymgtsystem.dtos.responses.AddBookResponse;
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



    @Override
    public boolean findMemberSession(Boolean sessionStatus) {
    Member member = memberRepository.findBySessionStatus(true);
    if (member == null) {
        throw new NotInSessionException("Not in session or currently logged out!");
    }
    return member.isSessionStatus();
    }

    @Override
    public boolean findMemberAccessLevel(int accessLevel) {
        return memberRepository.findByAccessLevel(accessLevel) != null;
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
    public AddBookRequest addBook(AddBookRequest addBookRequest) {
        if(findMemberSession(true) && !findMemberAccessLevel(20)){
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if(findMemberSession(true) && findMemberAccessLevel(20)){
            String memberEmail = getMemberEmail();
            if (memberEmail != null) {
                Book book = new Book();
                book.setTitle(addBookRequest.getBookTitle());
                book.setAuthor(addBookRequest.getBookAuthor());
                book.setIsbn(addBookRequest.getBookIsbn());
                book.setDescription(addBookRequest.getBookDescription());
                bookRepository.save(book);
                addBookRequest.setId(book.getId());

                AddBookResponse addBookResponse = new AddBookResponse();
                addBookResponse.setAddBookMsg("Book added successfully");
                addBookResponse.setId(book.getId());
                addBookResponse.setBookTitle(book.getTitle());
                addBookResponse.setBookAuthor(book.getAuthor());
                addBookResponse.setBookIsbn(book.getIsbn());
                addBookResponse.setBookDescription(book.getDescription());
                addBookResponse.setCreationDate(book.getCreationDate());
            } else {
                throw new NotInSessionException("Membership account not found!");
            }
        }
        return addBookRequest;
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


    }

