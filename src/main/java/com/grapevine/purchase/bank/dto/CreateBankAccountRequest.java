package com.grapevine.purchase.bank.dto;

import com.grapevine.purchase.bank.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateBankAccountRequest {

    private String accountName;
    private String bank;
    private String accountNumber;
    private AccountType type;
    private String currency;
    private BigDecimal balance;
}