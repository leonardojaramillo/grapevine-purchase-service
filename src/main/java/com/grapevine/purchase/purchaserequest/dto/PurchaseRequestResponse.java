package com.grapevine.purchase.purchaserequest.dto;

import com.grapevine.purchase.purchaserequest.PurchaseRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseRequestResponse {

    private Long id;
    private String productName;
    private String requestedBy;
    private Integer quantity;
    private String justification;
    private PurchaseRequestStatus status;
    private Boolean purchaseCreated;
    private LocalDateTime createdAt;
}