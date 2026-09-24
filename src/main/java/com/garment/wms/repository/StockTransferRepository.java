package com.garment.wms.repository;

import com.garment.wms.model.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    Optional<StockTransfer> findByTransferNo(String transferNo);
    List<StockTransfer> findAllByOrderByCreatedAtDesc();
    boolean existsByTransferNo(String transferNo);
}
