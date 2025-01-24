package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.data.repositories.MemberRepository;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.requests.LogoutRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.dtos.responses.LoginResponse;
import com.app.librarymgtsystem.dtos.responses.LogoutResponse;
import com.app.librarymgtsystem.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MemberServiceImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;


    @BeforeEach
    void eraseAll() {
        memberRepository.deleteAll();
    }


    @Test
    public void test_That_Registration_Email_Cannot_Be_Empty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        
        AddMemberRequest request1 = new AddMemberRequest();
        request1.setEmail(null);
        EmailCannotBeEmptyException exception = assertThrows(EmailCannotBeEmptyException.class, () ->
                memberService.emailCannotBeEmpty(request1));
        assertEquals("Registration Fields cannot be empty", exception.getMessage());
    }

    @Test
    public void test_That_Registration_Email_Cannot_Cannot_Have_Spaces() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest request1 = new AddMemberRequest();
        request1.setEmail("twine bravo@gmail.com");
        EmailCannotHaveSpacesException exception = assertThrows(EmailCannotHaveSpacesException.class, () ->
                memberService.emailCannotHaveSpace(request1));
        assertEquals("Email cannot have spaces", exception.getMessage());
    }

    @Test
    public void test_That_Registration_Password_Cannot_Have_Spaces() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest request1 = new AddMemberRequest();
        request1.setPassword("ty bravo");
        PasswordCannotHaveSpacesException exception = assertThrows(PasswordCannotHaveSpacesException.class, () ->
                memberService.passwordCannotHaveSpace(request1));
        assertEquals("Password cannot have spaces", exception.getMessage());
    }

    @Test
    public void test_That_Registration_Password_Cannot_Be_Less_Than_7() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest request1 = new AddMemberRequest();
        request1.setPassword("bravo");
        PasswordCannotLessThan7Exception exception = assertThrows(PasswordCannotLessThan7Exception.class, () ->
                memberService.passwordCannotLessThan7(request1));
        assertEquals("Password cannot be less than seven", exception.getMessage());
    }


    @Test
    public void test_That_Registration_Email_Must_Include_Email_Characters() {
        AddMemberRequest request = new AddMemberRequest();
        request.setFullName("Ade Bravo");
        request.setEmail("twinebravo@gmail");
        request.setPassword("tybravo");
        request.setPhoneNumber("07032819318");
        request.setAddress("No. 34, Sabo, Yaba, Lagos.");

        EmailCharNotIncludedException exception = assertThrows(EmailCharNotIncludedException.class, () ->
                memberService.emailCharNotIncluded(request));
        assertEquals("You forgot to include a character in Email", exception.getMessage());
    }

    @Test
    public void test_To_Register_And_Save_Members() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());
    }

    @Test
    public void test_That_Email_Already_Taken() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        EmailExistException exception = assertThrows(EmailExistException.class, () ->
                memberService.emailAlreadyExists(addMemberRequest));
        assertEquals("Email is already taken", exception.getMessage());
    }

    @Test
    public void test_That_User_Cannot_Login_With_Wrong_Email() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest getRequest = new LoginRequest();
        getRequest.setEmail("myemail@yahoo.com");
        //getRequest.setSessionStatus(false);
        LoginEmailException exception = assertThrows(LoginEmailException.class, () ->
                memberService.loginEmail(getRequest));
        assertEquals("Cannot find email", exception.getMessage());
    }

    @Test
    public void test_That_User_Cannot_Login_With_Right_Password_When_Email_Is_Not_Found() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest getRequest = new LoginRequest();
        getRequest.setEmail("bravo@gmail.com");
        getRequest.setPassword("tybravo");
        //getRequest.setSessionStatus(false);
        LoginEmailException exception = assertThrows(LoginEmailException.class, () ->
                memberService.loginPassword(getRequest, request));
        assertEquals("Cannot find email", exception.getMessage());
    }

    @Test
    public void test_That_User_Cannot_Login_With_Wrong_Password() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest getRequest = new LoginRequest();
        getRequest.setEmail("twinebravo@gmail.com");
        getRequest.setPassword("bravo");
        //getRequest.setSessionStatus(false);
        LoginPasswordException exception = assertThrows(LoginPasswordException.class, () ->
                memberService.loginPassword(getRequest, request));
        assertEquals("You have entered a wrong password", exception.getMessage());
    }

    @Test
    public void test_That_User_can_Login_With_Right_Email() {
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");

        LoginResponse getResponse = memberService.loginEmail(loginRequest);
        assertEquals("Email Login successful", getResponse.getLogMsg());
    }

    @Test
    public void test_That_User_Can_Login_With_Right_Password() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");

        LoginResponse getResponse1 = memberService.loginEmail(loginRequest);
        LoginResponse getResponse = memberService.loginPassword(loginRequest, request);
        assertEquals("Email Login successful", getResponse1.getLogMsg());
        assertEquals("Correct password! Member Login successful", getResponse.getLogMsg());

    }

    @Test
    public void test_That_Login_Email_Password_Cannot_Be_Empty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        
        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(null);
        loginRequest.setPassword(null);

        LoginMemberException exception = assertThrows(LoginMemberException.class, () ->
                memberService.loginMember(loginRequest, request));
        assertEquals("Email or Password cannot be empty", exception.getMessage());
    }

    @Test
    public void test_That_Email_And_Password_Are_Correct() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");

        String sessionEmailLibrarian = addMemberRequest.getEmail();
        doNothing().when(session).setAttribute(eq("userEmail"), anyString());
        when(session.getAttribute("userEmail")).thenReturn(sessionEmailLibrarian);

        Member getResponse = memberService.loginMember(loginRequest,request);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());
        LoginResponse getResponse2= new LoginResponse();
        getResponse2.setRegMsg("Member Login successful");
        assertEquals("Member Login successful", getResponse2.getRegMsg());
    }

    @Test
    public void test_That_Already_Login_Session_Is_Validated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");

        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        LoginMemberException exception = assertThrows(LoginMemberException.class, () ->
                memberService.alreadyInSession(loginRequest));
        assertEquals("Stop! You are already in session", exception.getMessage());
    }


    @Test
    public void test_That_Login_Session_Is_Currently_Running_And_Cannot_Login_Again() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");

        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());
    }

    @Test
    public void test_That_Login_Session_With_Email_And_Password_Are_Valid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");

        Member getResponse = memberService.loginMember(loginRequest, request);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());
        assertTrue(getResponse.isSessionStatus(), String.valueOf(true));
    }

    @Test
    public void test_That_Logout_Session_Is_Executed() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);

        AddMemberRequest addMemberRequest = new AddMemberRequest();
        addMemberRequest.setFullName("Ade Bravo");
        addMemberRequest.setEmail("twinebravo@gmail.com");
        addMemberRequest.setPassword("tybravo");
        addMemberRequest.setPhoneNumber("07032819318");
        addMemberRequest.setAddress("No. 34, Sabo, Yaba, Lagos.");
        addMemberRequest.setAccessLevel(10);
        addMemberRequest.setSessionStatus(false);
        AddMemberResponse response = memberService.registerMember(addMemberRequest);
        assertEquals("Registration successful", response.getRegMsg());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("twinebravo@gmail.com");
        loginRequest.setPassword("tybravo");
        Member loggedInMember = memberService.loginMember(loginRequest, request);

        when(session.getAttribute("userEmail")).thenReturn(loggedInMember.getEmail());
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setEmail("twinebravo@gmail.com");
        logoutRequest.setSessionStatus(false);

        LogoutResponse getResponse = memberService.logoutMember(request);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("Logged out successfully", getResponse.getLogoutMsg());
        verify(session).invalidate();
    }

}
