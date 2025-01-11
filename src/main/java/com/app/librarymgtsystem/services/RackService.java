package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.dtos.requests.AddRackRequest;
import com.app.librarymgtsystem.dtos.requests.ViewBookRequest;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import com.app.librarymgtsystem.dtos.responses.ViewRackResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface RackService {

    String getSessionEmail(HttpServletRequest request);

    boolean findMemberSession();

    boolean findMemberAccessLevel(int accessLevel);

    boolean findMemberAuthorize();

    AddRackResponse addToRack(AddRackRequest addRackRequest, String title, String sessionEmail);

    ViewRackResponse viewRack(ViewBookRequest viewBookRequest);

}
