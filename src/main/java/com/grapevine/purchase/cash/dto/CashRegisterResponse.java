package com.grapevine.purchase.cash.dto;

import com.grapevine.purchase.cash.CashRegisterStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CashRegisterResponse {

    private Long id;

    private BigDecimal openingAmount;

    private BigDecimal closingAmount;

    private BigDecimal currentBalance;

    private CashRegisterStatus status;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    private List<CashMovementResponse> movements;
}