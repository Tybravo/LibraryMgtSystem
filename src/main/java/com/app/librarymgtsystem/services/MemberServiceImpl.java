package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.LoginResponse;
import com.app.librarymgtsystem.dtos.responses.LogoutResponse;
import com.app.librarymgtsystem.exceptions.*;
import com.app.librarymgtsystem.security.LoggedInUserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private final SessionService sessionService;

    @Autowired
    public MemberServiceImpl(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    @Override
    public Member findMemberByEmail(String emailAddy) {
        Optional<Member> optionalMember = memberRepository.findByEmail(emailAddy);
        return optionalMember.orElse(null);
    }

    @Override
    public void emailCannotHaveSpace(AddMemberRequest addMemberRequest) {
        String email = addMemberRequest.getEmail();
        if (email != null && email.contains(" ")) {
            throw new EmailCannotHaveSpacesException("Email cannot have spaces");
        }
    }

    @Override
    public void passwordCannotHaveSpace(AddMemberRequest addMemberRequest) {
        String password = addMemberRequest.getPassword();
        if (password != null && password.contains(" ")) {
            throw new PasswordCannotHaveSpacesException("Password cannot have spaces");
        }
    }

    @Override
    public void passwordCannotLessThan7(AddMemberRequest addMemberRequest) {
        if(addMemberRequest.getPassword().length() < 7) {
            throw new PasswordCannotLessThan7Exception("Password cannot be less than seven");
        }
    }

    @Override
    public void emailCharNotIncluded(AddMemberRequest addMemberRequest) {
        if(!addMemberRequest.getEmail().contains("@") || !addMemberRequest.getEmail().contains(".") || !addMemberRequest.getEmail().contains("com")) {
            throw new EmailCharNotIncludedException("You forgot to include a character in Email");
        }
    }

    @Override
    public void emailCannotBeEmpty(AddMemberRequest addMemberRequest) {
        if (addMemberRequest.getEmail() == null || addMemberRequest.getEmail().isEmpty() ||
        addMemberRequest.getPassword() == null || addMemberRequest.getPassword().isEmpty() ||
        addMemberRequest.getFullName() == null || addMemberRequest.getFullName().isEmpty() ||
        addMemberRequest.getPhoneNumber() == null || addMemberRequest.getPhoneNumber().isEmpty() ||
        addMemberRequest.getAddress() == null || addMemberRequest.getAddress().isEmpty() ){
            throw new EmailCannotBeEmptyException("Registration Fields cannot be empty");
        }
    }

    @Override
    public void emailAlreadyExists(AddMemberRequest addMemberRequest) {
        Member getMember = findMemberByEmail(addMemberRequest.getEmail());
        if (getMember != null && getMember.getEmail().equals(addMemberRequest.getEmail())) {
            throw new EmailExistException("Email is already taken");
        }
    }

    @Override
    public AddMemberResponse registerMember(AddMemberRequest addMemberRequest) {
        emailCannotBeEmpty(addMemberRequest);
        emailCharNotIncluded(addMemberRequest);
        emailCannotHaveSpace(addMemberRequest);
        passwordCannotHaveSpace(addMemberRequest);
        passwordCannotLessThan7(addMemberRequest);

        Member getMember = findMemberByEmail(addMemberRequest.getEmail());
        AddMemberResponse regResponse = new AddMemberResponse();

        if (getMember == null && addMemberRequest.getEmail() != null) {
            Member member = new Member();
            member.setFullName(addMemberRequest.getFullName());
            member.setEmail(addMemberRequest.getEmail());
            member.setPassword(addMemberRequest.getPassword());
            member.setPhoneNumber(addMemberRequest.getPhoneNumber());
            member.setAddress(addMemberRequest.getAddress());
            member.setAccessLevel(10);
            Member savedMember = memberRepository.save(member);

            regResponse.setRegMsg("Registration successful");
            regResponse.setId(savedMember.getId());
            regResponse.setFullName(savedMember.getFullName());
            regResponse.setEmail(savedMember.getEmail());
            regResponse.setPhoneNumber(savedMember.getPhoneNumber());
            regResponse.setAddress(savedMember.getAddress());
            regResponse.setAccessLevel(savedMember.getAccessLevel());
            regResponse.setCreationDate(savedMember.getCreationDate());
            regResponse.setSessionStatus(addMemberRequest.isSessionStatus());
        } else {
            emailAlreadyExists(addMemberRequest);
            regResponse.setRegMsg("Email is already taken");
        }
        return regResponse;
    }

    @Override
    public LoginResponse loginEmail(LoginRequest loginRequest) {
        Member foundMemberEmail = findMemberByEmail(loginRequest.getEmail());
      if(foundMemberEmail != null && (foundMemberEmail.getEmail().equals(loginRequest.getEmail()) )){
          LoginResponse regResponse = new LoginResponse();
            regResponse.setId(foundMemberEmail.getId());
            regResponse.setEmail(foundMemberEmail.getEmail());
            regResponse.setSessionStatus(foundMemberEmail.isSessionStatus());
            regResponse.setLogMsg("Email Login successful");
            regResponse.setEmail(foundMemberEmail.getEmail());
          return regResponse;
        } else{
            throw new LoginEmailException("Cannot find email");
        }
    }

    @Override
    public LoginResponse loginPassword(LoginRequest loginRequest, HttpServletRequest request) {
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new LoginPasswordException("Password cannot be empty");
        }
        Member getMemberStatus = findMemberByEmail(loginRequest.getEmail());
        if (getMemberStatus != null && getMemberStatus.isSessionStatus()) {
            throw new LoginMemberException("Stop! You are already in session");
        }
        loginEmail(loginRequest);
        Member foundMemberPassword = findMemberByEmail(loginRequest.getEmail());
        if (foundMemberPassword == null) {
            throw new LoginPasswordException("No account found with this email");
        }
        if (!foundMemberPassword.getPassword().equals(loginRequest.getPassword())) {
            throw new LoginPasswordException("You have entered a wrong password");
        }

        String sessionToken = UUID.randomUUID().toString();
        foundMemberPassword.setSessionStatus(true);
        foundMemberPassword.setSessionToken(sessionToken);
        foundMemberPassword.setSessionEmail(foundMemberPassword.getEmail());
        memberRepository.save(foundMemberPassword);

        LoggedInUserContext.setSessionToken(sessionToken);
        LoggedInUserContext.setSessionEmail(foundMemberPassword.getEmail());

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(30 * 60); // Set session timeout (30 minutes)
        session.setAttribute("userEmail", foundMemberPassword.getEmail());
        String sessionEmail = (String) session.getAttribute("userEmail");

        LoginResponse regResponse = new LoginResponse();
        regResponse.setId(foundMemberPassword.getId());
        regResponse.setEmail(foundMemberPassword.getEmail());
        regResponse.setSessionStatus(foundMemberPassword.isSessionStatus());
        regResponse.setAccessLevel(foundMemberPassword.getAccessLevel());
        regResponse.setSessionEmail(sessionEmail);
        regResponse.setLogMsg("Correct password! Member Login successful");
        return regResponse;
    }

    @Override
    public void alreadyInSession(LoginRequest loginRequest) {
        Member getMemberStatus = findMemberByEmail(loginRequest.getEmail());
        if (getMemberStatus != null && getMemberStatus.isSessionStatus()) {
            throw new LoginMemberException("Stop! You are already in session");
        }
    }

    @Override
    public Member loginMember(LoginRequest loginRequest, HttpServletRequest request) {
        Member getMemberStatus = findMemberByEmail(loginRequest.getEmail());
        if (getMemberStatus != null && getMemberStatus.isSessionStatus()) {
            throw new LoginMemberException("Stop! You are already in session");
        }
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            throw new LoginMemberException("Email or Password cannot be empty");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new LoginMemberException("Email or Password cannot be empty");
        }
        Member foundMember = findMemberByEmail(loginRequest.getEmail());
        if (foundMember == null) {
            throw new LoginMemberNotFoundException("Member not found with the provided email");
        }
        if (!foundMember.getPassword().equals(loginRequest.getPassword())) {
            throw new LoginMemberException("Wrong email or password entered");
        }
        String sessionToken = UUID.randomUUID().toString();
        LoggedInUserContext.setSessionToken(sessionToken);
        LoggedInUserContext.setSessionEmail(foundMember.getEmail());

        foundMember.setSessionStatus(true);
        foundMember.setSessionEmail(foundMember.getEmail());

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(30 * 60); // Set session timeout (30 minutes)
        session.setAttribute("userEmail", foundMember.getEmail());

        memberRepository.save(foundMember);
        foundMember.setId(foundMember.getId());
        foundMember.setLogMsg("Member Login successful");
        return foundMember;
    }

    @Override
    public LogoutResponse logoutMember(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userEmail") == null) {
                throw new LogoutMemberException("No active session found for the user");
            }
            String sessionEmail = (String) session.getAttribute("userEmail");
            Member foundMember = findMemberByEmail(sessionEmail);
            if (foundMember == null) {
                throw new LogoutMemberException("Member does not exist");
            }
            if (!foundMember.isSessionStatus()) {
                throw new LogoutMemberException("You are currently out of session");
            }
            // Update session status and persist changes
            foundMember.setSessionStatus(false);
            memberRepository.save(foundMember);

            // Invalidate the session
            session.invalidate();

            // Remove JSESSIONID cookie by setting Max-Age to 0
            Cookie jsessionCookie = new Cookie("JSESSIONID", null);
            jsessionCookie.setPath("/");
            jsessionCookie.setHttpOnly(true);
            jsessionCookie.setMaxAge(0); // Expire the cookie
            response.addCookie(jsessionCookie);

            // Prepare logout response
            LogoutResponse logoutResponse = new LogoutResponse();
            logoutResponse.setEmail(sessionEmail);
            logoutResponse.setLogoutMsg("Logged out successfully");

            return logoutResponse;
        } finally {
            // Ensure the user context is cleared
            LoggedInUserContext.clear();
        }
    }








//    private void validateEmailFormat(String email) {
//        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
//        if (!email.matches(emailRegex)) {
//            throw new IllegalArgumentException("Invalid email format");
//        }
//    }


}

