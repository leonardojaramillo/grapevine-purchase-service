package com.grapevine.purchase.purchase.dto;

import com.grapevine.purchase.purchase.PurchaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PurchaseResponse {

    private Long id;
    private String supplierName;
    private String bankAccountName;
    private String warehouseName;
    private PurchaseStatus status;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private String paymentProofUrl;
    private List<PurchaseItemResponse> items;
}