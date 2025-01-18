package com.app.librarymgtsystem.controllers;

import com.app.librarymgtsystem.data.models.Member;
import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.requests.LogoutRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.exceptions.*;
import com.app.librarymgtsystem.services.MemberService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api/member")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private ServletRequest request;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AddMemberRequest addMemberRequest) {
        try {
            AddMemberResponse response = memberService.registerMember(addMemberRequest);
            return ResponseEntity.ok(response);
        } catch (EmailCannotBeEmptyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EmailExistException | EmailCannotHaveSpacesException | PasswordCannotHaveSpacesException | EmailCharNotIncludedException e)  {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/login-email")
    public ResponseEntity<?> loginMyEmail(@RequestBody LoginRequest loginRequest) {
        try {
            memberService.loginEmail(loginRequest);
            return ResponseEntity.ok(memberService.loginEmail(loginRequest));
        } catch (LoginEmailException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/login-password")
    public ResponseEntity<?> loginMyPassword(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            memberService.loginPassword(loginRequest, request);
            return ResponseEntity.ok(memberService.loginPassword(loginRequest, request));
        } catch (LoginPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/already-in-session")
    public ResponseEntity<?>alreadyInSession(@RequestBody LoginRequest loginRequest) {
        try {
            memberService.alreadyInSession(loginRequest);
            return (ResponseEntity<?>) ResponseEntity.ok();
        } catch (NotInSessionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/login-member")
    public ResponseEntity<?> loginMember(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Login request received for email: {}", loginRequest.getEmail());
        try {
            Member loginResponse = memberService.loginMember(loginRequest, request);
            return ResponseEntity.ok(loginResponse);
        } catch (LoginMemberException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutMember(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userEmail") == null) {
                return ResponseEntity.badRequest().body("No active session found for the user.");
            }
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setEmail((String) session.getAttribute("userEmail"));
            memberService.logoutMember(request);

            return ResponseEntity.ok("Logout successful!");
        } catch (LogoutMemberException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMember(@RequestParam String email) {
        try {
            return ResponseEntity.ok(memberService.findMemberByEmail(email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }
}
