package com.grapevine.purchase.supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Supplier create(Supplier supplier) {
        supplier.setActive(true);
        Supplier saved = supplierRepository.save(supplier);

        return saved;
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier toggleActive(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        supplier.setActive(!supplier.getActive());
        Supplier saved = supplierRepository.save(supplier);

        return saved;
    }
}