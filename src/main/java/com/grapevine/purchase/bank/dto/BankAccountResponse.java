package com.grapevine.purchase.bank.dto;

import com.grapevine.purchase.bank.AccountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BankAccountResponse {

    private Long id;
    private String accountName;
    private String bank;
    private String accountNumber;
    private AccountType type;
    private String currency;
    private BigDecimal balance;
    private Boolean active;
}