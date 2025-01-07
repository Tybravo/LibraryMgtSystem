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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
        AddMemberRequest request = new AddMemberRequest();
        request.setEmail(null);
        EmailCannotBeEmptyException exception = assertThrows(EmailCannotBeEmptyException.class, () ->
                memberService.emailCannotBeEmpty(new AddMemberRequest()));
        assertEquals("Registration Fields cannot be empty", exception.getMessage());
    }

    @Test
    public void test_That_Registration_Email_Must_Include_Email_Characters() {
        AddMemberRequest request = new AddMemberRequest();
        request.setFullName("Ade Bravo");
        request.setEmail("twinebravo@gmail");
        request.setPassword("tybravo");
        request.setPhoneNumber("07032819318");
        request.setAddress("No. 34, Sabo, Yaba, Lagos.");

        EmailCannotBeEmptyException exception = assertThrows(EmailCannotBeEmptyException.class, () ->
                memberService.emailCannotBeEmpty(request));
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
    public void test_That_User_Cannot_Login_With_Wrong_Password() {
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
        getRequest.setPassword("bravo");
        //getRequest.setSessionStatus(false);
        LoginPasswordException exception = assertThrows(LoginPasswordException.class, () ->
                memberService.loginPassword(getRequest));
        assertEquals("You have entered a wrong password or missing email", exception.getMessage());
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
        LoginResponse getResponse = memberService.loginPassword(loginRequest);
        assertEquals("Email Login successful", getResponse1.getLogMsg());
        assertEquals("Correct password! Member Login successful", getResponse.getLogMsg());

    }

    @Test
    public void test_That_Login_Email_Password_Cannot_Be_Empty() {
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
                memberService.loginMember(loginRequest));
        assertEquals("Email or Password cannot be empty", exception.getMessage());
    }

    @Test
    public void test_That_Email_And_Password_Are_Correct() {
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

        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());
        LoginResponse getResponse2= new LoginResponse();
        getResponse2.setRegMsg("Member Login successful");
        assertEquals("Member Login successful", getResponse2.getRegMsg());
    }

    @Test
    public void test_That_Already_Login_Session_Is_Not_Validated() {
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

        NotInSessionException exception = assertThrows(NotInSessionException.class, () ->
                memberService.alreadyInSession(loginRequest));
        assertEquals("Not in session! You accidentally miss your way", exception.getMessage());
    }

    @Test
    public void test_That_Already_Login_Session_Is_Validated() {
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

        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());

        Member getResponse2 = memberService.alreadyInSession(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse2.getEmail());
        assertTrue(getResponse2.isSessionStatus(), String.valueOf(true));
    }


    @Test
    public void test_That_Login_Session_Is_Currently_Running_And_Cannot_Login_Again() {
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

        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());
    }

    @Test
    public void test_That_Login_Session_With_Email_And_Password_Are_Valid() {
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

        Member getResponse = memberService.loginMember(loginRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("tybravo", getResponse.getPassword());
        assertTrue(getResponse.isSessionStatus(), String.valueOf(true));
    }

    @Test
    public void test_That_Logout_Session_Is_Executed() {
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
        memberService.loginMember(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setEmail("twinebravo@gmail.com");
        logoutRequest.setSessionStatus(false);

        LogoutResponse getResponse = memberService.logoutMember(logoutRequest);
        assertEquals("twinebravo@gmail.com", getResponse.getEmail());
        assertEquals("Logged out successfully", getResponse.getLogoutMsg());
    }

}
