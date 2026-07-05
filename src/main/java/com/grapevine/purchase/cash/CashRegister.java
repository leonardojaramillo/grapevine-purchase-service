package com.grapevine.purchase.cash;

import com.grapevine.purchase.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cash_registers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal openingAmount;

    private BigDecimal closingAmount;

    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    private CashRegisterStatus status;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "cashRegister",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<CashMovement> movements = new ArrayList<>();
}