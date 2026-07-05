package com.grapevine.purchase.cash;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_movements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    private String description;

    private BigDecimal amount;

    @Column(name = "receipt_url", columnDefinition = "TEXT")
    private String receiptUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementStatus status;

    @Column(name = "affects_balance", nullable = false)
    private Boolean affectsBalance;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "cash_register_id")
    private CashRegister cashRegister;
}