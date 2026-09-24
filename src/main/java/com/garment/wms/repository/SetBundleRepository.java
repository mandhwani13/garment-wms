package com.garment.wms.repository;

import com.garment.wms.model.InventoryStatus;
import com.garment.wms.model.SetBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetBundleRepository extends JpaRepository<SetBundle, Long> {

    Optional<SetBundle> findByBarcode(String barcode);

    List<SetBundle> findByCuttingLotId(Long cuttingLotId);

    List<SetBundle> findByCuttingLotIdOrderBySetIndexAsc(Long cuttingLotId);

    long countByStatus(InventoryStatus status);

    long countByCuttingLotIdAndStatus(Long cuttingLotId, InventoryStatus status);

    @Query("SELECT s FROM SetBundle s WHERE s.cuttingLot.id = :lotId AND (:color IS NULL OR s.colorName = :color)")
    List<SetBundle> findByLotAndColor(@Param("lotId") Long lotId, @Param("color") String color);
}
