package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.dtos.requests.AddRackRequest;
import com.app.librarymgtsystem.dtos.requests.UpdateRackRequest;
import com.app.librarymgtsystem.dtos.requests.ViewBookRequest;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import com.app.librarymgtsystem.dtos.responses.ViewRackResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface RackService {

    String getSessionEmail(HttpServletRequest request);

    boolean findMemberSession(HttpServletRequest request);

    boolean findMemberAccessLevel(int accessLevel);

    boolean findMemberAuthorize(boolean authorize);

    AddRackResponse addToRack(AddRackRequest addRackRequest, String title, HttpServletRequest request);

    ViewRackResponse updateRackByMember(UpdateRackRequest updateRackRequest, HttpServletRequest request);

}
