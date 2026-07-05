package com.grapevine.purchase.warehouse_stock;

import com.grapevine.purchase.product.Product;
import com.grapevine.purchase.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {

    List<WarehouseStock> findByWarehouse(Warehouse warehouse);

    List<WarehouseStock> findByProduct(Product product);

    Optional<WarehouseStock> findByWarehouseAndProduct(Warehouse warehouse, Product product);

    @Query("SELECT COALESCE(SUM(ws.stock), 0) FROM WarehouseStock ws WHERE ws.product = :product")
    Integer sumStockByProduct(Product product);
}