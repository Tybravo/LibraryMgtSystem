package com.app.librarymgtsystem.dtos.responses;

import com.app.librarymgtsystem.data.models.RackChoice;
import com.app.librarymgtsystem.data.models.RackCopy;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AddRackResponse {
    private String id;
    private String memberId;
    private String bookId;
    private String rackCurrency;
    private BigDecimal rackAmount;
    private boolean rackAuthorize;
    private int rackNumberOfCopy;
    private String addRackMsg;
    private boolean sessionStatus;

    private RackChoice rackChoice;
    private RackCopy rackCopy;
    private LocalDateTime creationDate = LocalDateTime.now();
}
