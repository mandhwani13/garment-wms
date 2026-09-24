package com.garment.wms.repository;

import com.garment.wms.model.InventoryStatus;
import com.garment.wms.model.SinglePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SinglePieceRepository extends JpaRepository<SinglePiece, Long>, JpaSpecificationExecutor<SinglePiece> {

    Optional<SinglePiece> findByBarcode(String barcode);

    List<SinglePiece> findByParentSetId(Long parentSetId);

    List<SinglePiece> findByCuttingLotId(Long cuttingLotId);

    long countByStatus(InventoryStatus status);

    long countByCuttingLotIdAndStatus(Long cuttingLotId, InventoryStatus status);

    long countByStatusAndIsLooseFalse(InventoryStatus status);

    long countByStatusAndIsLooseTrue(InventoryStatus status);

    @Query("SELECT p FROM SinglePiece p WHERE " +
           "(:lotId IS NULL OR p.cuttingLot.id = :lotId) AND " +
           "(:styleId IS NULL OR p.cuttingLot.style.id = :styleId) AND " +
           "(:color IS NULL OR :color = '' OR LOWER(p.colorName) = LOWER(:color)) AND " +
           "(:size IS NULL OR :size = '' OR LOWER(p.sizeName) = LOWER(:size)) AND " +
           "(:unitId IS NULL OR p.currentFinishingUnit.id = :unitId) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.id DESC")
    List<SinglePiece> filterStock(
            @Param("lotId") Long lotId,
            @Param("styleId") Long styleId,
            @Param("color") String color,
            @Param("size") String size,
            @Param("unitId") Long unitId,
            @Param("status") InventoryStatus status);
}
