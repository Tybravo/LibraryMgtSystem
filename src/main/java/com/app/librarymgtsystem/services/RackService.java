package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.dtos.requests.AddRackRequest;
import com.app.librarymgtsystem.dtos.responses.AddRackResponse;
import org.springframework.stereotype.Service;

@Service
public interface RackService {
    AddRackResponse addToRack(AddRackRequest addRackRequest);

    
}
