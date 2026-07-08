package com.grapevine.purchase.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String accountName;
    private String bank;
    private String accountNumber;
    private String type;
    private String currency;
    private BigDecimal balance;
    private Boolean active;
}