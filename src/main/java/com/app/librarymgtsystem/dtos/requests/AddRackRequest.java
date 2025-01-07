package com.app.librarymgtsystem.dtos.requests;

import com.app.librarymgtsystem.data.models.RackChoice;
import com.app.librarymgtsystem.data.models.RackCopy;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AddRackRequest {
    private int id;
    private String memberId;
    private String bookId;
    private String rackCurrency;
    private BigDecimal rackAmount;
    private boolean rackAuthorize;
    private int rackNumberOfCopy;

    private RackChoice rackChoice;
    private RackCopy rackCopy;
}
