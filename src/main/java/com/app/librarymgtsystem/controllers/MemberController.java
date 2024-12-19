package com.app.librarymgtsystem.controllers;

import com.app.librarymgtsystem.dtos.requests.AddMemberRequest;
import com.app.librarymgtsystem.dtos.requests.LoginRequest;
import com.app.librarymgtsystem.dtos.requests.LogoutRequest;
import com.app.librarymgtsystem.dtos.responses.AddMemberResponse;
import com.app.librarymgtsystem.exceptions.*;
import com.app.librarymgtsystem.exceptions.*;
import com.app.librarymgtsystem.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AddMemberRequest addMemberRequest) {
        try {
            AddMemberResponse response = memberService.registerMember(addMemberRequest);
            return ResponseEntity.ok(response);
        } catch (EmailCannotBeEmptyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EmailExistException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }catch (Exception e) {
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
    public ResponseEntity<?> loginMyPassword(@RequestBody LoginRequest loginRequest) {
        try {
            memberService.loginPassword(loginRequest);
            return ResponseEntity.ok(memberService.loginPassword(loginRequest));
        } catch (LoginPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/already-in-session")
    public ResponseEntity<?> alreadyInSession(@RequestBody LoginRequest loginRequest) {
        try {
            memberService.alreadyInSession(loginRequest);
            return ResponseEntity.ok(memberService.alreadyInSession(loginRequest));
        } catch (NotInSessionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/login-member")
    public ResponseEntity<?> loginMember(@RequestBody LoginRequest loginRequest) {
        try {
            memberService.loginMember(loginRequest);
            return ResponseEntity.ok(memberService.loginMember(loginRequest));
        } catch (LoginMemberException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutMember(@RequestBody LogoutRequest logoutRequest) {
        try {
            memberService.logoutMember(logoutRequest);
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
