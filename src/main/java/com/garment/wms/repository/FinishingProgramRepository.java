package com.garment.wms.repository;

import com.garment.wms.model.FinishingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinishingProgramRepository extends JpaRepository<FinishingProgram, Long> {
    Optional<FinishingProgram> findByProgramNo(String programNo);
    List<FinishingProgram> findAllByOrderByCreatedAtDesc();
    List<FinishingProgram> findByCuttingLotId(Long cuttingLotId);
    List<FinishingProgram> findByFinishingUnitIdOrderByCreatedAtDesc(Long finishingUnitId);
    boolean existsByProgramNo(String programNo);
}
