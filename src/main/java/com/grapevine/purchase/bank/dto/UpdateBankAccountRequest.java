package com.grapevine.purchase.bank.dto;

import com.grapevine.purchase.bank.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBankAccountRequest {

    private String accountName;
    private String bank;
    private String accountNumber;
    private AccountType type;
    private String currency;
}