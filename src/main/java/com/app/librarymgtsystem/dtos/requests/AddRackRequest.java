package com.app.librarymgtsystem.dtos.requests;

import com.app.librarymgtsystem.data.models.RackChoice;
import com.app.librarymgtsystem.data.models.RackCopy;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddRackRequest {
    private String id;
    private String memberId;
    private String bookId;
    private String rackCurrency;
    private BigDecimal rackAmount;
    private boolean rackAuthorize;
    private int rackNumberOfCopy;
    private boolean sessionStatus;
    private String currentBookTitle;

    private RackChoice rackChoice;
    private RackCopy rackCopy;
}
