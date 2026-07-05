package com.grapevine.purchase.cash.dto;

import com.grapevine.purchase.cash.MovementStatus;
import com.grapevine.purchase.cash.MovementType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CashMovementResponse {

    private Long id;
    private MovementType type;
    private String description;
    private BigDecimal amount;
    private String receiptUrl;
    private MovementStatus status;
    private Boolean affectsBalance;
    private LocalDateTime createdAt;
}