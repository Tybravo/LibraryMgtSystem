package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.dtos.requests.AddRackRequest;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import org.springframework.stereotype.Service;

@Service
public interface RackService {

    boolean findMemberSession();

    boolean findMemberAccessLevel(int accessLevel);

    AddRackResponse addToRack(AddRackRequest addRackRequest, String title);

}
