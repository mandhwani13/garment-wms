package com.garment.wms.repository;

import com.garment.wms.model.FinishingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinishingUnitRepository extends JpaRepository<FinishingUnit, Long> {
    Optional<FinishingUnit> findByUnitCode(String unitCode);
    List<FinishingUnit> findByActiveTrue();
    boolean existsByUnitCode(String unitCode);
}
