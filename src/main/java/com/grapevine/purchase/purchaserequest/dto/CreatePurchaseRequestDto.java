package com.grapevine.purchase.purchaserequest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePurchaseRequestDto {

    private Long productId;
    private Integer quantity;
    private String justification;
}