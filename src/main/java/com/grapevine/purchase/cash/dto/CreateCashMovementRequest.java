package com.grapevine.purchase.cash.dto;

import com.grapevine.purchase.cash.MovementType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCashMovementRequest {

    private MovementType type;
    private String description;
    private BigDecimal amount;
    private String receiptUrl;
}