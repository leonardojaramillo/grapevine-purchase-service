package com.grapevine.purchase.cash.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OpenCashRegisterRequest {

    private BigDecimal openingAmount;
}