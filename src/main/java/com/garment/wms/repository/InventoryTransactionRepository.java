package com.garment.wms.repository;

import com.garment.wms.model.InventoryTransaction;
import com.garment.wms.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findTop50ByOrderByCreatedAtDesc();
    List<InventoryTransaction> findByTransactionTypeOrderByCreatedAtDesc(TransactionType transactionType);
    List<InventoryTransaction> findByBarcodeValueOrderByCreatedAtDesc(String barcodeValue);
}
