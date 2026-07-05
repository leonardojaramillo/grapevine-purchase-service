package com.grapevine.purchase.cash;

import com.grapevine.purchase.bank.BankAccountRepository;
import com.grapevine.purchase.cash.dto.*;
import com.grapevine.purchase.user.Role;
import com.grapevine.purchase.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CashService {

    private final CashRegisterRepository cashRegisterRepository;
    private final CashMovementRepository cashMovementRepository;
    private final BankAccountRepository  bankAccountRepository;

    public CashRegisterResponse open(OpenCashRegisterRequest request) {
        cashRegisterRepository.findByStatus(CashRegisterStatus.OPEN).ifPresent(cash -> {
            throw new RuntimeException("Ya existe una caja abierta");
        });

        CashRegister cashRegister = CashRegister.builder()
                .openingAmount(request.getOpeningAmount())
                .currentBalance(request.getOpeningAmount())
                .status(CashRegisterStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .build();

        CashRegister saved = cashRegisterRepository.save(cashRegister);

        return mapResponse(saved);
    }

    public CashRegisterResponse close(CloseCashRegisterRequest request) {
        CashRegister cashRegister = cashRegisterRepository.findByStatus(CashRegisterStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta"));

        cashRegister.setStatus(CashRegisterStatus.CLOSED);
        cashRegister.setClosedAt(LocalDateTime.now());
        cashRegister.setClosingAmount(request.getClosingAmount());

        if (request.getBankAccountId() != null) {
            bankAccountRepository.findById(request.getBankAccountId()).ifPresent(account -> {
                account.setBalance(account.getBalance().add(cashRegister.getCurrentBalance()));
                bankAccountRepository.save(account);
            });
        }

        CashRegister saved = cashRegisterRepository.save(cashRegister);

        return mapResponse(saved);
    }

    public CashMovementResponse createMovement(CreateCashMovementRequest request) {
        CashRegister cashRegister = cashRegisterRepository.findByStatus(CashRegisterStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta"));

        MovementStatus status = (request.getType() == MovementType.EXPENSE && request.getReceiptUrl() != null && !request.getReceiptUrl().isBlank())
                ? MovementStatus.PENDING
                : MovementStatus.APPROVED;

        if (request.getType() == MovementType.INCOME) {
            cashRegister.setCurrentBalance(cashRegister.getCurrentBalance().add(request.getAmount()));
        } else {
            cashRegister.setCurrentBalance(cashRegister.getCurrentBalance().subtract(request.getAmount()));
        }

        CashMovement movement = CashMovement.builder()
                .type(request.getType())
                .description(request.getDescription())
                .amount(request.getAmount())
                .receiptUrl(request.getReceiptUrl())
                .status(status)
                .affectsBalance(true)
                .createdAt(LocalDateTime.now())
                .cashRegister(cashRegister)
                .build();

        CashMovement saved = cashMovementRepository.save(movement);
        cashRegister.getMovements().add(saved);
        cashRegisterRepository.save(cashRegister);

        return toMovementResponse(saved);
    }

    public void recordAutomaticExpense(BigDecimal amount, String description) {
        Optional<CashRegister> openRegister = cashRegisterRepository.findByStatus(CashRegisterStatus.OPEN);
        if (openRegister.isEmpty()) {
            return;
        }

        CashMovement movement = CashMovement.builder()
                .type(MovementType.EXPENSE)
                .description(description)
                .amount(amount)
                .receiptUrl(null)
                .status(MovementStatus.APPROVED)
                .affectsBalance(false)
                .createdAt(LocalDateTime.now())
                .cashRegister(openRegister.get())
                .build();

        cashMovementRepository.save(movement);
    }

    public CashMovementResponse updateMovementStatus(Long id, MovementStatus status) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SOFTWARE_ENGINEER) {
            throw new RuntimeException("Solo el administrador puede aprobar o rechazar rendiciones");
        }

        CashMovement movement = cashMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        movement.setStatus(status);
        CashMovement saved = cashMovementRepository.save(movement);

        return toMovementResponse(saved);
    }

    public List<CashMovementResponse> getPendingMovements() {
        return cashMovementRepository.findAll().stream()
                .filter(m -> m.getStatus() == MovementStatus.PENDING)
                .map(this::toMovementResponse)
                .toList();
    }

    public CashRegisterResponse getCurrentCashRegister() {
        CashRegister cashRegister = cashRegisterRepository.findByStatus(CashRegisterStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta"));

        return mapResponse(cashRegister);
    }

    private CashRegisterResponse mapResponse(CashRegister cashRegister) {
        List<CashMovementResponse> movements = cashRegister.getMovements().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toMovementResponse)
                .toList();

        return CashRegisterResponse.builder()
                .id(cashRegister.getId())
                .openingAmount(cashRegister.getOpeningAmount())
                .closingAmount(cashRegister.getClosingAmount())
                .currentBalance(cashRegister.getCurrentBalance())
                .status(cashRegister.getStatus())
                .openedAt(cashRegister.getOpenedAt())
                .closedAt(cashRegister.getClosedAt())
                .movements(movements)
                .build();
    }

    private CashMovementResponse toMovementResponse(CashMovement m) {
        return CashMovementResponse.builder()
                .id(m.getId())
                .type(m.getType())
                .description(m.getDescription())
                .amount(m.getAmount())
                .receiptUrl(m.getReceiptUrl())
                .status(m.getStatus())
                .affectsBalance(m.getAffectsBalance())
                .createdAt(m.getCreatedAt())
                .build();
    }
}