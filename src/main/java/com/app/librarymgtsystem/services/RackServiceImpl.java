package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddRackRequest;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import com.app.librarymgtsystem.exceptions.EmailNotFoundException;
import com.app.librarymgtsystem.exceptions.NotEligiblePageException;
import com.app.librarymgtsystem.exceptions.NotInSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RackServiceImpl implements RackService {

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


    @Override
    public boolean findMemberAccessLevel(int accessLevel) {
        return memberRepository.findByAccessLevel(accessLevel) != null;
    }


    @Override
    public AddRackResponse addToRack(AddRackRequest addRackRequest) {
        if(!findMemberSession()) {
            throw new NotInSessionException("Not in session or currently logged out!");
        }
        if(findMemberSession() && ! findMemberAccessLevel(20)){
            throw new NotEligiblePageException("You're not eligible to access this page");
        }
        if(findMemberSession() && findMemberAccessLevel(20)){
            Optional<Book> optionalBook = bookRepository.findById(addRackRequest.getBookId());
            if(optionalBook.isPresent()) {
                Book foundBook = optionalBook.get();
                AddRackResponse addRackResponse = new AddRackResponse();
                
            }
        }
        return null;
    }


}
