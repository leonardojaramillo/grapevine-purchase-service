package com.grapevine.purchase.bank;

import com.grapevine.purchase.bank.dto.BankAccountResponse;
import com.grapevine.purchase.bank.dto.CreateBankAccountRequest;
import com.grapevine.purchase.bank.dto.UpdateBankAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccountResponse> findAll() {
        return bankAccountService.findAll();
    }

    @PostMapping
    public BankAccountResponse create(@RequestBody CreateBankAccountRequest request) {
        return bankAccountService.create(request);
    }

    @PutMapping("/{id}")
    public BankAccountResponse update(@PathVariable Long id,
                                      @RequestBody UpdateBankAccountRequest request) {
        return bankAccountService.update(id, request);
    }

    @PatchMapping("/{id}/toggle-active")
    public BankAccountResponse toggleActive(@PathVariable Long id) {
        return bankAccountService.toggleActive(id);
    }
}