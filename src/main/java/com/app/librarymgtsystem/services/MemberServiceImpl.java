package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.LoginResponse;
import com.app.librarymgtsystem.dtos.responses.LogoutResponse;
import com.app.librarymgtsystem.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    MemberRepository memberRepository;


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
        addMemberRequest.getAddress() == null || addMemberRequest.getAddress().isEmpty() ||
        addMemberRequest.getPassword().length() < 7 ) {
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
          return regResponse;
        } else{
            throw new LoginEmailException("Cannot find email");
        }
    }

    @Override
    public LoginResponse loginPassword(LoginRequest loginRequest, HttpServletRequest request) {
        String sessionToken = UUID.randomUUID().toString();
            Member foundMemberPassword = findMemberByEmail(loginRequest.getEmail());
            if (foundMemberPassword != null && (foundMemberPassword.getPassword().equals(loginRequest.getPassword()))) {
                foundMemberPassword.setSessionStatus(true);
                foundMemberPassword.setSessionToken(sessionToken);
                foundMemberPassword.setSessionEmail(foundMemberPassword.getEmail());
                memberRepository.save(foundMemberPassword);

                LoggedInUserContext.setSessionToken(sessionToken);
                LoggedInUserContext.setSessionEmail(foundMemberPassword.getEmail());
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", foundMemberPassword.getEmail());

                LoginResponse regResponse = new LoginResponse();
                regResponse.setId(foundMemberPassword.getId());
                regResponse.setEmail(foundMemberPassword.getEmail());
                regResponse.setSessionStatus(foundMemberPassword.isSessionStatus());
                regResponse.setLogMsg("Correct password! Member Login successful");
                 return regResponse;
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new LoginPasswordException("Password cannot be empty");
            } else {
                throw new LoginPasswordException("You have entered a wrong password or missing email");
            }
    }

    @Override
    public Member alreadyInSession(LoginRequest loginRequest) {
        Member getMemberStatus = findMemberByEmail(loginRequest.getEmail());
        if(getMemberStatus != null && !getMemberStatus.isSessionStatus()){
            throw new NotInSessionException("Not in session! You accidentally miss your way");
        }
        if (getMemberStatus != null) {
            if (loginRequest.getEmail().equals(getMemberStatus.getEmail()) &&
                    loginRequest.getPassword().equals(getMemberStatus.getPassword()) ) {
                getMemberStatus.setSessionStatus(true);
                memberRepository.save(getMemberStatus);
                getMemberStatus.setStatusMsg("Already in session");
            }
        }
            return getMemberStatus;
    }


    @Override
    public Member loginMember(LoginRequest loginRequest, HttpServletRequest request) {
        String sessionToken = UUID.randomUUID().toString();
        Member foundMember = findMemberByEmail(loginRequest.getEmail());

        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new LoginMemberException("Email or Password cannot be empty");
        }
        if (foundMember != null && foundMember.getEmail().equals(loginRequest.getEmail()) &&
                loginRequest.getPassword().equals(foundMember.getPassword())) {
            foundMember.setSessionStatus(true);
            foundMember.setSessionToken(sessionToken);
            foundMember.setSessionEmail(foundMember.getEmail());
            memberRepository.save(foundMember);

            LoggedInUserContext.setSessionToken(sessionToken);
            LoggedInUserContext.setSessionEmail(foundMember.getEmail());
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", foundMember.getEmail());
            foundMember.setId(foundMember.getId());
            foundMember.setLogMsg("Member Login successful");
            return foundMember;
        } else {
            throw new LoginMemberException("Wrong email or password entered");
        }
    }

    @Override
    public LogoutResponse logoutMember(HttpServletRequest request) {
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
            foundMember.setSessionStatus(false);
            memberRepository.save(foundMember);

            session.invalidate();

            LogoutResponse outSession = new LogoutResponse();
            outSession.setEmail(sessionEmail);
            outSession.setLogoutMsg("Logged out successfully");
            return outSession;
        } finally {
            LoggedInUserContext.clear();
        }
    }


    public static class LoggedInUserContext {
        private static final ThreadLocal<String> sessionEmail = new ThreadLocal<>();
        private static final ThreadLocal<String> sessionToken = new ThreadLocal<>();

        public static void setSessionEmail(String email) {
            sessionEmail.set(email);
        }
        public static String getSessionEmail() {
            return sessionEmail.get();
        }
        public static void setSessionToken(String token) {
            sessionToken.set(token);
        }
        public static String getSessionToken() {
            return sessionToken.get();
        }
        public static void clear() {
            sessionEmail.remove();
            sessionToken.remove();
        }
    }



//    private void validateEmailFormat(String email) {
//        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
//        if (!email.matches(emailRegex)) {
//            throw new IllegalArgumentException("Invalid email format");
//        }
//    }


}

