package com.grapevine.purchase.cash;

import com.grapevine.purchase.cash.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public CashRegisterResponse open(@RequestBody OpenCashRegisterRequest request) {
        return cashService.open(request);
    }

    @PostMapping("/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'SOFTWARE_ENGINEER')")
    public CashRegisterResponse close(@RequestBody CloseCashRegisterRequest request) {
        return cashService.close(request);
    }

    @PostMapping("/movement")
    @PreAuthorize("hasAnyRole('CAJERO', 'SOFTWARE_ENGINEER')")
    public CashMovementResponse createMovement(@RequestBody CreateCashMovementRequest request) {
        return cashService.createMovement(request);
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'SOFTWARE_ENGINEER')")
    public CashRegisterResponse getCurrent() {
        return cashService.getCurrentCashRegister();
    }

    @GetMapping("/movements/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'SOFTWARE_ENGINEER')")
    public List<CashMovementResponse> getPending() {
        return cashService.getPendingMovements();
    }

    @PatchMapping("/movements/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public CashMovementResponse approve(@PathVariable Long id) {
        return cashService.updateMovementStatus(id, MovementStatus.APPROVED);
    }

    @PatchMapping("/movements/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOFTWARE_ENGINEER')")
    public CashMovementResponse reject(@PathVariable Long id) {
        return cashService.updateMovementStatus(id, MovementStatus.REJECTED);
    }
}
