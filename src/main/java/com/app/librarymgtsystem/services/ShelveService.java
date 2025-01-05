package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.dtos.requests.UpdateShelveRequest;
import com.app.librarymgtsystem.dtos.responses.UpdateShelveResponse;
import com.app.librarymgtsystem.dtos.responses.ViewShelveResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ShelveService {

    boolean findMemberSession();

    List<Shelve> viewShelveByCategory(ShelveType category);

    List<ViewShelveResponse> viewShelveByCategoryForMembers(ShelveType category);

    UpdateShelveResponse updateShelveByBookTitle(UpdateShelveRequest updateShelveRequest, String title);

    UpdateShelveResponse setBookAvailableInShelve(UpdateShelveRequest updateShelveRequest, String title);

}
