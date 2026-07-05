package com.grapevine.purchase.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseDetailRepository
        extends JpaRepository<PurchaseDetail, Long> {
}