package com.grapevine.purchase.purchaserequest;

import com.grapevine.purchase.purchaserequest.dto.CreatePurchaseRequestDto;
import com.grapevine.purchase.purchaserequest.dto.PurchaseRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICA', 'SOFTWARE_ENGINEER')")
    public List<PurchaseRequestResponse> findAll() {
        return purchaseRequestService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LOGISTICA', 'SOFTWARE_ENGINEER')")
    public PurchaseRequestResponse create(@RequestBody CreatePurchaseRequestDto dto) {
        return purchaseRequestService.create(dto);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseRequestResponse approve(@PathVariable Long id) {
        return purchaseRequestService.updateStatus(id, PurchaseRequestStatus.APPROVED);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseRequestResponse reject(@PathVariable Long id) {
        return purchaseRequestService.updateStatus(id, PurchaseRequestStatus.REJECTED);
    }

    @PatchMapping("/{id}/purchase-created")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public PurchaseRequestResponse markPurchaseCreated(@PathVariable Long id) {
        return purchaseRequestService.markPurchaseCreated(id);
    }
}
