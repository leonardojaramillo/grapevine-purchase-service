package com.grapevine.purchase.warehouse_stock;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WarehouseStockResponse {
    private Long   warehouseStockId;
    private Long   warehouseId;
    private String warehouseName;
    private Long   productId;
    private String productName;
    private String productCategory;
    private Integer stock;
}