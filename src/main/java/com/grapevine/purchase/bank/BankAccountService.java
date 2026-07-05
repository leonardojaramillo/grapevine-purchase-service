package com.grapevine.purchase.bank;

import com.grapevine.purchase.bank.dto.BankAccountResponse;
import com.grapevine.purchase.bank.dto.CreateBankAccountRequest;
import com.grapevine.purchase.bank.dto.UpdateBankAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public List<BankAccountResponse> findAll() {
        return bankAccountRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BankAccountResponse create(CreateBankAccountRequest request) {
        BankAccount account = BankAccount.builder()
                .accountName(request.getAccountName())
                .bank(request.getBank())
                .accountNumber(request.getAccountNumber())
                .type(request.getType())
                .currency(request.getCurrency())
                .balance(request.getBalance())
                .active(true)
                .build();

        BankAccount saved = bankAccountRepository.save(account);


        return toResponse(saved);
    }

    public BankAccountResponse update(Long id, UpdateBankAccountRequest request) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada"));

        if (request.getAccountName()   != null) account.setAccountName(request.getAccountName());
        if (request.getBank()          != null) account.setBank(request.getBank());
        if (request.getAccountNumber() != null) account.setAccountNumber(request.getAccountNumber());
        if (request.getType()          != null) account.setType(request.getType());
        if (request.getCurrency()      != null) account.setCurrency(request.getCurrency());

        BankAccount saved = bankAccountRepository.save(account);


        return toResponse(saved);
    }

    public BankAccountResponse toggleActive(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada"));
        account.setActive(!account.getActive());
        BankAccount saved = bankAccountRepository.save(account);


        return toResponse(saved);
    }

    private BankAccountResponse toResponse(BankAccount a) {
        return BankAccountResponse.builder()
                .id(a.getId())
                .accountName(a.getAccountName())
                .bank(a.getBank())
                .accountNumber(a.getAccountNumber())
                .type(a.getType())
                .currency(a.getCurrency())
                .balance(a.getBalance())
                .active(a.getActive())
                .build();
    }
}