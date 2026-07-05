package com.grapevine.purchase.purchase.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePurchaseRequest {

    private Long supplierId;
    private Long bankAccountId;
    private Long warehouseId;
    private List<PurchaseItemRequest> items;
}