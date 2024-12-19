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

@Service
public class BookServiceImpl implements BookService {

@Autowired
    private BookRepository bookRepository;
@Autowired
    private MemberRepository memberRepository;



//    @Override
//    public boolean findMemberSession(Boolean sessionStatus) {
//        return memberRepository.findBySessionStatus(true).isSessionStatus();
//    }
@Override
public boolean findMemberSession(Boolean sessionStatus) {
    Member member = memberRepository.findBySessionStatus(true);
    if (member == null) {
        throw new NotInSessionException("Not in session or currently logged out!");
    }
    return member.isSessionStatus();
}



    @Override
    public Boolean findMemberAccessLevel(int accessLevel) {
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
        if(findMemberSession(null)){
            throw new NotInSessionException("Not in session, you are currently logged out!");
            }
        if(findMemberSession(true) && !findMemberAccessLevel(20)){
            throw new NotEligiblePageException("Not eligible to access this page!");
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

                AddBookResponse addBookResponse = new AddBookResponse();
                addBookResponse.setAddBookMsg("Book added successfully");
                addBookResponse.setBookTitle(book.getTitle());
                addBookResponse.setBookAuthor(book.getAuthor());
                addBookResponse.setBookIsbn(book.getIsbn());
                addBookResponse.setBookDescription(book.getDescription());
            } else {
                throw new NotInSessionException("Membership account not found!");
            }
        }
        return addBookRequest;
    }




    }

