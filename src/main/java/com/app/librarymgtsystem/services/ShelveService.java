package com.app.librarymgtsystem.services;

import com.app.librarymgtsystem.data.models.Shelve;
import com.app.librarymgtsystem.data.models.ShelveType;
import org.springframework.stereotype.Service;

@Service
public interface ShelveService {

    Shelve findShelveByCategory(ShelveType category);
}
