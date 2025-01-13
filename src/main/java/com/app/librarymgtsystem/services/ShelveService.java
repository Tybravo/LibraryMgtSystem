package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import com.app.librarymgtsystem.dtos.requests.UpdateShelveRequest;
import com.app.librarymgtsystem.dtos.responses.UpdateShelveResponse;
import com.app.librarymgtsystem.dtos.responses.ViewShelveResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShelveService {

    String getSessionEmail(HttpServletRequest request);

    boolean findMemberSession(HttpServletRequest request);

    List<Shelve> viewShelveByCategory(ShelveType category, HttpServletRequest request);

    List<ViewShelveResponse> viewShelveByCategoryForMembers(ShelveType category, HttpServletRequest request);

    UpdateShelveResponse updateShelveByBookTitle(UpdateShelveRequest updateShelveRequest, String title, HttpServletRequest request);

    UpdateShelveResponse setBookAvailableInShelve(UpdateShelveRequest updateShelveRequest, String title, HttpServletRequest request);

    UpdateShelveResponse setBookUnavailableInShelve(UpdateShelveRequest updateShelveRequest, String title, HttpServletRequest request);

}
