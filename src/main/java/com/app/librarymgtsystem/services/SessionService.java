package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.security.LoggedInUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    private final MemberRepository memberRepository;
    private final LoggedInUserContext loggedInUserContext;

    @Autowired
    public SessionService(MemberRepository memberRepository, LoggedInUserContext loggedInUserContext) {
        this.memberRepository = memberRepository;
        this.loggedInUserContext = loggedInUserContext;
    }

    public Optional<Member> findMemberBySessionEmail() {
        String sessionEmail = LoggedInUserContext.getSessionEmail();
        return memberRepository.findBySessionEmail(sessionEmail);
    }
}
