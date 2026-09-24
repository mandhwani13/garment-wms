package com.garment.wms.repository;

import com.garment.wms.model.WashingBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WashingBatchRepository extends JpaRepository<WashingBatch, Long> {
    Optional<WashingBatch> findByBatchNo(String batchNo);
    List<WashingBatch> findAllByOrderByCreatedAtDesc();
    List<WashingBatch> findByCuttingLotId(Long cuttingLotId);
    List<WashingBatch> findByWashingUnitIdOrderByCreatedAtDesc(Long washingUnitId);
    boolean existsByBatchNo(String batchNo);
}
