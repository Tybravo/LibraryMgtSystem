package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ShelveService {

    boolean findMemberSession();

    List<Shelve> viewShelveByCategory(ShelveType category);

}
