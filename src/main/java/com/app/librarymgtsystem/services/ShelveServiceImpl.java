package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.UpdateShelveRequest;
import com.app.librarymgtsystem.dtos.responses.UpdateShelveResponse;
import com.app.librarymgtsystem.dtos.responses.ViewShelveResponse;
import com.app.librarymgtsystem.exceptions.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ShelveServiceImpl implements ShelveService {

    @Autowired
    private ShelveRepository shelveRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookServiceImpl bookServiceImpl;


    @Override
    public String getSessionEmail(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            throw new NotInSessionException("Not in session or currently logged out!");
        }
        return (String) session.getAttribute("userEmail");
    }


    @Override
    public boolean findMemberSession(HttpServletRequest request) {
        String sessionEmail = getSessionEmail(request);
        if (sessionEmail == null) {
            throw new NotInSessionException("Not in session or currently logged out!");
        }
        Optional<Member> optionalMember = memberRepository.findBySessionEmail(sessionEmail);
        if (optionalMember.isEmpty()) {
            throw new EmailNotFoundException("Member email not found");
        }
        Member member = optionalMember.get();
        if (!member.isSessionStatus()) {
            throw new NotInSessionException("Membership account is out of session");
        }
        return true;
    }


    public boolean findMemberAccessLevel(int accessLevel) {
        return memberRepository.findByAccessLevel(accessLevel) != null;
    }

    public Book findBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }


    @Override
    public List<Shelve> viewShelveByCategory(ShelveType category, HttpServletRequest request) {
        if (!findMemberSession(request)) {
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(request) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(request) && findMemberAccessLevel(20)) {
            List<Shelve> shelves = shelveRepository.findByCategory(category);
            for (Shelve shelve : shelves) {
                if (shelve.getBookId() != null) {
                    Optional<Book> book = bookRepository.findById(shelve.getBookId());
                    book.ifPresent(shelve::setBook);
                }
            }
            return shelves;
        }
        return Collections.emptyList();
    }


    @Override
    public List<ViewShelveResponse> viewShelveByCategoryForMembers(ShelveType category, HttpServletRequest request) {
        if (!findMemberSession(request)) {
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(request) && !findMemberAccessLevel(10)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        List<Shelve> shelves = shelveRepository.findByCategory(category);
        if (shelves.isEmpty()) {
            return Collections.emptyList();
        }
        List<ViewShelveResponse> shelveDetailsList = new ArrayList<>();

        for (Shelve shelve : shelves) {
            if (shelve.getBookId() != null) {
                Optional<Book> bookOptional = bookRepository.findById(shelve.getBookId());
                if (bookOptional.isPresent()) {
                    Book book = bookOptional.get();

                    ViewShelveResponse responseDetail = new ViewShelveResponse();
                    responseDetail.setBookTitle(book.getTitle());
                    responseDetail.setBookDescription(book.getDescription());
                    responseDetail.setBookGenre(shelve.getGenre());
                    responseDetail.setAvailable(shelve.isAvailable());
                    responseDetail.setBorrowed(shelve.isBorrowed());

                    shelveDetailsList.add(responseDetail);
                }
            }
        }
        return shelveDetailsList;
    }


    @Override
    public UpdateShelveResponse updateShelveByBookTitle(UpdateShelveRequest updateShelveRequest, String title, HttpServletRequest request) {
        UpdateShelveResponse updateShelveResponse = new UpdateShelveResponse();
        if (!findMemberSession(request)) {
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(request) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(request) && findMemberAccessLevel(20)) {

            Book foundBook = findBookByTitle(updateShelveRequest.getCurrentBookTitle());
            if (foundBook == null) {
                throw new BookNotFoundException("Book with the title '" + updateShelveRequest.getCurrentBookTitle() + "' not found");
            }
            String book_Id = foundBook.getId();

            Optional<Shelve> foundBookId = shelveRepository.findByBookId(book_Id);
            if (foundBookId.isEmpty()){
                throw new BookNotFoundException("Book not found");
            }
            Shelve foundShelve = foundBookId.get();
            String bookId = foundShelve.getBookId();

            if (book_Id.equals(bookId) && updateShelveRequest.getCurrentBookTitle() != null && updateShelveRequest.getBookGenre() != null && updateShelveRequest.getBookCategory() != null) {
                foundShelve.setCategory(updateShelveRequest.getBookCategory());
                foundShelve.setGenre(updateShelveRequest.getBookGenre());

                if (bookServiceImpl.findShelveByBookTitleAvailable(title).isAvailable()) {
                    shelveRepository.save(foundShelve);
                    updateShelveResponse.setUpdateShelveMsg("Shelve updated successfully");
                    updateShelveResponse.setBookTitle(foundBook.getTitle());
                    updateShelveResponse.setBookCategory(foundShelve.getCategory());
                    updateShelveResponse.setBookGenre(foundShelve.getGenre());
                    updateShelveResponse.setAvailable(foundShelve.isAvailable());
                }
            }
        }
        return updateShelveResponse;
    }


    @Override
    public UpdateShelveResponse setBookAvailableInShelve(UpdateShelveRequest updateShelveRequest, String title, HttpServletRequest request) {
        UpdateShelveResponse updateBookAvail = new UpdateShelveResponse();
        if (!findMemberSession(request)) {
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(request) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(request) && findMemberAccessLevel(20)) {

            Book foundBook = findBookByTitle(updateShelveRequest.getCurrentBookTitle());
            if (foundBook == null) {
                throw new BookNotFoundException("Book with the title '" + updateShelveRequest.getCurrentBookTitle() + "' not found");
            }
            String book_Id = foundBook.getId();

            Optional<Shelve> foundBookId = shelveRepository.findByBookId(book_Id);
            if (foundBookId.isEmpty()) {
                throw new BookNotFoundException("Book not found");
            }
            Shelve foundShelve = foundBookId.get();
            String bookId = foundShelve.getBookId();
            boolean bookAvailable = foundShelve.isAvailable();

            if (bookId.equals(book_Id) && bookAvailable) {
                throw new BooKAvailabilitySetAlreadyException("Stop! The Book is already set to be available");
            }
            if (! bookAvailable && bookId.equals(book_Id)) {
                foundShelve.setAvailable(true);
                shelveRepository.save(foundShelve);

                updateBookAvail.setUpdateShelveMsg("Yes! Book is now available");
                updateBookAvail.setAvailable(foundShelve.isAvailable());
            }
        }
            return updateBookAvail;
    }


    @Override
    public UpdateShelveResponse setBookUnavailableInShelve(UpdateShelveRequest updateShelveRequest, String title, HttpServletRequest request) {
        UpdateShelveResponse updateBookUnavail = new UpdateShelveResponse();
        if (!findMemberSession(request)) {
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession(request) && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession(request) && findMemberAccessLevel(20)) {

            Book foundBook = findBookByTitle(updateShelveRequest.getCurrentBookTitle());
            if (foundBook == null) {
                throw new BookNotFoundException("Book with the title '" + updateShelveRequest.getCurrentBookTitle() + "' not found");
            }
            String book_Id = foundBook.getId();

            Optional<Shelve> foundBookId = shelveRepository.findByBookId(book_Id);
            if (foundBookId.isEmpty()) {
                throw new BookNotFoundException("Book not found");
            }
            Shelve foundShelve = foundBookId.get();
            String bookId = foundShelve.getBookId();
            boolean bookAvailable = foundShelve.isAvailable();

            if (bookId.equals(book_Id) && ! bookAvailable) {
                throw new BooKAvailabilitySetAlreadyException("Stop! The Book is already set to be unavailable");
            }
            if (bookAvailable && bookId.equals(book_Id)) {
                foundShelve.setAvailable(false);
                shelveRepository.save(foundShelve);

                updateBookUnavail.setUpdateShelveMsg("No! Book is not available");
                updateBookUnavail.setAvailable(foundShelve.isAvailable());
            }
        }
        return updateBookUnavail;
    }



}
