package com.grapevine.purchase.client;

import com.grapevine.purchase.client.dto.BankAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "finance-service")
public interface FinanceClient {

    @GetMapping("/api/bank-accounts/{id}")
    BankAccountResponse getBankAccount(@PathVariable("id") Long id);

    @PatchMapping("/api/bank-accounts/{id}/deduct-balance")
    BankAccountResponse deductBalance(@PathVariable("id") Long id, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/api/cash/automatic-expense")
    void recordAutomaticExpense(@RequestParam("amount") BigDecimal amount, @RequestParam("description") String description);
}