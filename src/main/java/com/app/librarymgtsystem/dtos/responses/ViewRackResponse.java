package com.app.librarymgtsystem.dtos.responses;

import com.app.librarymgtsystem.data.models.RackChoice;
import com.app.librarymgtsystem.data.models.RackCopy;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ViewRackResponse {
    private String id;
    private String memberId;
    private String bookId;
    private String rackCurrency;
    private BigDecimal rackAmount;
    private boolean rackAuthorize;
    private int rackNumberOfCopy;
    private String RackId;
    private String viewRackMsg;

    private RackChoice rackChoice;
    private RackCopy rackCopy;
    private LocalDateTime creationDate = LocalDateTime.now();
}
