package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.responses.ViewBookResponse;
import com.app.librarymgtsystem.exceptions.EmailNotFoundException;
import com.app.librarymgtsystem.exceptions.NotEligiblePageException;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Override
    public boolean findMemberSession() {
        String sessionEmail = MemberServiceImpl.LoggedInUserContext.getSessionEmail();
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


    @Override
    public List<Shelve> viewShelveByCategory(ShelveType category) {
        if (!findMemberSession()) {
            throw new EmailNotFoundException("Member email not found");
        }
        if (findMemberSession() && !findMemberAccessLevel(20)) {
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if (findMemberSession() && findMemberAccessLevel(20)) {
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




}
