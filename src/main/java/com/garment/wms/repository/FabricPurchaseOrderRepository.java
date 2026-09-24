package com.garment.wms.repository;

import com.garment.wms.model.FabricPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabricPurchaseOrderRepository extends JpaRepository<FabricPurchaseOrder, Long> {
    Optional<FabricPurchaseOrder> findByConsignmentNo(String consignmentNo);
    List<FabricPurchaseOrder> findAllByOrderByCreatedAtDesc();
    List<FabricPurchaseOrder> findByDestinationStitchingUnitIdOrderByCreatedAtDesc(Long stitchingUnitId);
    boolean existsByConsignmentNo(String consignmentNo);
}
