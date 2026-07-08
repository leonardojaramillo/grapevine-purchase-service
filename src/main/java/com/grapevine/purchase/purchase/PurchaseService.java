package com.grapevine.purchase.purchase;

import com.grapevine.purchase.client.FinanceClient;
import com.grapevine.purchase.client.dto.BankAccountResponse;
import com.grapevine.purchase.product.Product;
import com.grapevine.purchase.product.ProductRepository;
import com.grapevine.purchase.purchase.dto.*;
import com.grapevine.purchase.supplier.Supplier;
import com.grapevine.purchase.supplier.SupplierRepository;
import com.grapevine.purchase.warehouse.Warehouse;
import com.grapevine.purchase.warehouse.WarehouseRepository;
import com.grapevine.purchase.warehouse_stock.WarehouseStock;
import com.grapevine.purchase.warehouse_stock.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private static final BigDecimal USD_EXCHANGE_RATE = new BigDecimal("3.41");

    private final PurchaseRepository       purchaseRepository;
    private final ProductRepository        productRepository;
    private final SupplierRepository       supplierRepository;
    private final WarehouseRepository      warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final FinanceClient            financeClient;

    public PurchaseResponse create(CreatePurchaseRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId()).orElseThrow();

        if (request.getBankAccountId() != null) {
            // Valida que la cuenta exista en finance-service antes de asociarla
            financeClient.getBankAccount(request.getBankAccountId());
        }

        Warehouse warehouse = null;
        if (request.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow();
        }

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setBankAccountId(request.getBankAccountId());
        purchase.setWarehouse(warehouse);
        purchase.setStatus(PurchaseStatus.DRAFT);
        purchase.setCreatedAt(LocalDateTime.now());

        List<PurchaseDetail> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            details.add(PurchaseDetail.builder()
                    .purchase(purchase)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subtotal(subtotal)
                    .build());
            total = total.add(subtotal);
        }

        purchase.setDetails(details);
        purchase.setTotal(total);

        return toResponse(purchaseRepository.save(purchase));
    }

    public PurchaseResponse update(Long id, CreatePurchaseRequest request) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (purchase.getStatus() != PurchaseStatus.DRAFT) {
            throw new RuntimeException("Solo se pueden editar órdenes en borrador");
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId()).orElseThrow();
        purchase.setSupplier(supplier);

        purchase.setBankAccountId(request.getBankAccountId());

        if (request.getWarehouseId() != null) {
            warehouseRepository.findById(request.getWarehouseId())
                    .ifPresent(purchase::setWarehouse);
        } else {
            purchase.setWarehouse(null);
        }

        purchase.getDetails().clear();
        purchaseRepository.saveAndFlush(purchase);

        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            purchase.getDetails().add(PurchaseDetail.builder()
                    .purchase(purchase)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subtotal(subtotal)
                    .build());
            total = total.add(subtotal);
        }

        purchase.setTotal(total);

        return toResponse(purchaseRepository.save(purchase));
    }

    public List<PurchaseResponse> findAll() {
        return purchaseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseResponse updateStatus(Long id, PurchaseStatus newStatus) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        validateTransition(purchase.getStatus(), newStatus);

        if (newStatus == PurchaseStatus.PAID) {
            throw new RuntimeException("Usa el endpoint /pay con el comprobante de pago");
        }

        if (newStatus == PurchaseStatus.RECEIVED) {
            receiveIntoWarehouse(purchase);
        }

        purchase.setStatus(newStatus);
        Purchase saved = purchaseRepository.save(purchase);

        return toResponse(saved);
    }

    @Transactional
    public PurchaseResponse pay(Long id, PayPurchaseRequest request) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        validateTransition(purchase.getStatus(), PurchaseStatus.PAID);

        if (request.getPaymentProofUrl() == null || request.getPaymentProofUrl().isBlank()) {
            throw new RuntimeException("Debes adjuntar el comprobante de pago (imagen de transferencia o yape)");
        }

        if (purchase.getBankAccountId() != null) {
            BankAccountResponse account = financeClient.getBankAccount(purchase.getBankAccountId());
            BigDecimal amountToDeduct = convertToAccountCurrency(purchase.getTotal(), account.getCurrency());
            financeClient.deductBalance(purchase.getBankAccountId(), amountToDeduct);
        }

        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setPaymentProofUrl(request.getPaymentProofUrl());
        Purchase saved = purchaseRepository.save(purchase);

        financeClient.recordAutomaticExpense(
                purchase.getTotal(),
                "Pago a " + (purchase.getSupplier() != null ? purchase.getSupplier().getName() : "proveedor")
                        + " — Compra #" + saved.getId() + " (no afecta efectivo, pagado por banco)"
        );

        return toResponse(saved);
    }

    private void receiveIntoWarehouse(Purchase purchase) {
        if (purchase.getWarehouse() == null) {
            throw new RuntimeException("Debes asignar un almacén de destino antes de recibir la compra");
        }

        Warehouse warehouse = purchase.getWarehouse();

        for (PurchaseDetail detail : purchase.getDetails()) {
            Product product = detail.getProduct();

            WarehouseStock ws = warehouseStockRepository
                    .findByWarehouseAndProduct(warehouse, product)
                    .orElse(WarehouseStock.builder()
                            .warehouse(warehouse)
                            .product(product)
                            .stock(0)
                            .build());

            ws.setStock(ws.getStock() + detail.getQuantity());
            warehouseStockRepository.save(ws);

            Integer totalStock = warehouseStockRepository.sumStockByProduct(product);
            product.setStock(totalStock != null ? totalStock : 0);
            productRepository.save(product);
        }
    }

    private BigDecimal convertToAccountCurrency(BigDecimal amountInSoles, String accountCurrency) {
        if ("USD".equalsIgnoreCase(accountCurrency)) {
            return amountInSoles.divide(USD_EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        }
        return amountInSoles;
    }

    private void validateTransition(PurchaseStatus current, PurchaseStatus next) {
        boolean valid = switch (current) {
            case DRAFT     -> next == PurchaseStatus.SENT      || next == PurchaseStatus.CANCELLED;
            case SENT      -> next == PurchaseStatus.CONFIRMED || next == PurchaseStatus.CANCELLED;
            case CONFIRMED -> next == PurchaseStatus.RECEIVED  || next == PurchaseStatus.CANCELLED;
            case RECEIVED  -> next == PurchaseStatus.PAID;
            default        -> false;
        };

        if (!valid) throw new RuntimeException(
                "Transición inválida: " + current + " → " + next
        );
    }

    private String buildAccountLabel(BankAccountResponse account) {
        String base = account.getBank() + " - " + account.getAccountNumber();
        return account.getAccountName() != null && !account.getAccountName().isBlank()
                ? account.getAccountName() + " (" + base + ")"
                : base;
    }

    private PurchaseResponse toResponse(Purchase p) {
        String bankAccountName = "Sin cuenta asignada";
        if (p.getBankAccountId() != null) {
            try {
                BankAccountResponse account = financeClient.getBankAccount(p.getBankAccountId());
                bankAccountName = buildAccountLabel(account);
            } catch (Exception e) {
                bankAccountName = "Cuenta no disponible";
            }
        }

        return PurchaseResponse.builder()
                .id(p.getId())
                .supplierName(p.getSupplier() != null ? p.getSupplier().getName() : "—")
                .bankAccountName(bankAccountName)
                .warehouseName(p.getWarehouse() != null ? p.getWarehouse().getName() : "Sin almacén asignado")
                .status(p.getStatus())
                .total(p.getTotal())
                .createdAt(p.getCreatedAt())
                .paymentProofUrl(p.getPaymentProofUrl())
                .items(p.getDetails() != null ? p.getDetails().stream()
                                                .map(d -> PurchaseItemResponse.builder()
                                                          .productName(d.getProduct().getName())
                                                          .quantity(d.getQuantity())
                                                          .price(d.getPrice())
                                                          .subtotal(d.getSubtotal())
                                                          .build())
                                                .toList() : List.of())
                .build();
    }
}