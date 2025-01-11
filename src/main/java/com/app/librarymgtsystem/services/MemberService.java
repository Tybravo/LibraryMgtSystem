package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LogoutRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.LoginResponse;
import com.app.librarymgtsystem.dtos.responses.LogoutResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    Member findMemberByEmail(String emailAddy);

    AddMemberResponse registerMember(AddMemberRequest addMemberRequest);

    void emailAlreadyExists(AddMemberRequest addMemberRequest);

    void emailCannotBeEmpty(AddMemberRequest addMemberRequest);

    Member alreadyInSession(LoginRequest loginRequest);

    LoginResponse loginEmail(LoginRequest loginRequest);

    LoginResponse loginPassword(LoginRequest loginRequest, HttpServletRequest request);

    Member loginMember(LoginRequest loginRequest, HttpServletRequest request);

    LogoutResponse logoutMember(HttpServletRequest request);

}
