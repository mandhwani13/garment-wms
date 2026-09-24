package com.garment.wms.repository;

import com.garment.wms.model.CuttingLotColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuttingLotColorRepository extends JpaRepository<CuttingLotColor, Long> {
    List<CuttingLotColor> findByCuttingLotId(Long cuttingLotId);
}
