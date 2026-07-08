package com.grapevine.purchase.purchase;

import com.grapevine.purchase.purchase.dto.CreatePurchaseRequest;
import com.grapevine.purchase.purchase.dto.PayPurchaseRequest;
import com.grapevine.purchase.purchase.dto.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse create(@RequestBody CreatePurchaseRequest request) {
        System.out.println(">>> Llegó al controller de compras");
        return purchaseService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public List<PurchaseResponse> findAll() {
        return purchaseService.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse update(@PathVariable Long id, @RequestBody CreatePurchaseRequest request) {
        return purchaseService.update(id, request);
    }

    @PatchMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse send(@PathVariable Long id) {
        return purchaseService.updateStatus(id, PurchaseStatus.SENT);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse confirm(@PathVariable Long id) {
        return purchaseService.updateStatus(id, PurchaseStatus.CONFIRMED);
    }

    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse receive(@PathVariable Long id) {
        return purchaseService.updateStatus(id, PurchaseStatus.RECEIVED);
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse pay(@PathVariable Long id, @RequestBody PayPurchaseRequest request) {
        return purchaseService.pay(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseResponse cancel(@PathVariable Long id) {
        return purchaseService.updateStatus(id, PurchaseStatus.CANCELLED);
    }
}