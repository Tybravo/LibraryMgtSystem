package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Book;
import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.models.Rack;
import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.repositories.BookRepository;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.data.repositories.RackRepository;
import com.app.librarymgtsystem.data.repositories.ShelveRepository;
import com.app.librarymgtsystem.dtos.requests.AddRackRequest;
import com.app.librarymgtsystem.dtos.requests.UpdateRackRequest;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import com.app.librarymgtsystem.dtos.responses.ViewRackResponse;
import com.app.librarymgtsystem.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RackServiceImpl implements RackService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private RackRepository rackRepository;
    @Autowired
    private ShelveRepository shelveRepository;



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


    @Override
    public boolean findMemberAccessLevel(int accessLevel) {
        return memberRepository.findByAccessLevel(accessLevel) != null;
    }


    @Override
    public boolean findMemberAuthorize(boolean authorize) {
        Optional<Rack> foundAuthorize = rackRepository.findByAuthorize(authorize);
        return foundAuthorize.isPresent();
    }


    @Override
    public AddRackResponse addToRack(AddRackRequest addRackRequest, String title, HttpServletRequest request) {
        String sessionEmail = getSessionEmail(request);
        AddRackResponse addRackResponse = new AddRackResponse();
        if (!findMemberSession(request)) {
            throw new NotInSessionException("Not in session or currently logged out!");
        }
       if (findMemberSession(request)) {
           findShelveByBookTitle(title);
           findShelveByBookTitleAvailable(title);

           Optional<Member> optionalMemberId = memberRepository.findBySessionEmail(sessionEmail);
           if (optionalMemberId.isPresent()) {
               Optional<Member> optionalMember = memberRepository.findById(optionalMemberId.get().getId());
               Optional<Book> optionalBook = bookRepository.findById(addRackRequest.getBookId());
               if (optionalBook.isPresent() && optionalMember.isPresent()) {
                   Book foundBook = optionalBook.get();
                   Member foundMember = optionalMember.get();

                   Rack addRack = new Rack();
                   addRack.setBookId(foundBook.getId());
                   addRack.setMemberId(foundMember.getId());
                   addRack.setCurrency(addRackRequest.getRackCurrency());
                   addRack.setAmount(addRackRequest.getRackAmount());
                   addRack.setNumberOfCopy(addRackRequest.getRackNumberOfCopy());
                   addRack.setRackChoice(addRackRequest.getRackChoice());
                   addRack.setRackCopy(addRackRequest.getRackCopy());
                   addRack.setAuthorize(false);
                   Rack savedRack = rackRepository.save(addRack);

                   addRackResponse.setAddRackMsg("Book is added to rack successfully");
                   addRackResponse.setBookId(savedRack.getBookId());
               }
           }
        }
        return addRackResponse;
    }

@Override
    public ViewRackResponse updateRackByMember(UpdateRackRequest updateRackRequest, HttpServletRequest request) {
        String sessionEmail = getSessionEmail(request);
        Optional<Member> optionalMember = memberRepository.findBySessionEmail(sessionEmail);
        if (optionalMember.isEmpty()) {
            throw new NotInSessionException("Not in session or currently logged out!");
        }
        Member foundMember = optionalMember.get();
        String memberId = foundMember.getId();

        Optional<Rack> optionalRack = rackRepository.findByMemberId(memberId);
        if (optionalRack.isEmpty()) {
            throw new IllegalArgumentException("No rack found for the member");
        }
        Rack foundRack = optionalRack.get();

        if (!foundRack.isAuthorize()) {
            if (updateRackRequest.getRackChoice() != null) {
                foundRack.setRackChoice(updateRackRequest.getRackChoice());
            }
            if (updateRackRequest.getRackCopy() != null) {
                foundRack.setRackCopy(updateRackRequest.getRackCopy());
            }
            if (updateRackRequest.getRackCurrency() != null) {
                foundRack.setCurrency(updateRackRequest.getRackCurrency());
            }
            if (updateRackRequest.getRackAmount() != null) {
                foundRack.setAmount(updateRackRequest.getRackAmount());
            }
            if (updateRackRequest.getRackNumberOfCopy() != 0) {
                foundRack.setNumberOfCopy(updateRackRequest.getRackNumberOfCopy());
            }
            rackRepository.save(foundRack);
        }
        ViewRackResponse viewRackResponse = new ViewRackResponse();
        viewRackResponse.setMemberId(foundMember.getId());
        viewRackResponse.setRackId(foundRack.getId());
        viewRackResponse.setRackChoice(foundRack.getRackChoice());
        viewRackResponse.setRackCopy(foundRack.getRackCopy());
        viewRackResponse.setRackCurrency(foundRack.getCurrency());
        viewRackResponse.setRackAmount(foundRack.getAmount());
        viewRackResponse.setRackNumberOfCopy(foundRack.getNumberOfCopy());
        viewRackResponse.setViewRackMsg("Book in rack is updated successfully");

        return viewRackResponse;
    }



    public void findShelveByBookTitle(String title) {
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
        //return optionalShelve.get();
    }

    public void findShelveByBookTitleAvailable(String title) {
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
        //return shelve;
    }


}