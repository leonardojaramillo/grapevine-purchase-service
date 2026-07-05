package com.grapevine.purchase.cash;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CashMovementRepository
        extends JpaRepository<CashMovement, Long> {
}