package com.grapevine.purchase.warehouse_stock;

import com.grapevine.purchase.product.Product;
import com.grapevine.purchase.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_stock",
       uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_id", "product_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer stock;
}