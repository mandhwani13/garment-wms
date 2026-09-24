package com.garment.wms.repository;

import com.garment.wms.model.FabricRollInward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FabricRollInwardRepository extends JpaRepository<FabricRollInward, Long> {
    List<FabricRollInward> findByPurchaseOrderId(Long purchaseOrderId);
}
