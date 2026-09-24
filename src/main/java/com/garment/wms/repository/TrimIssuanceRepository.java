package com.garment.wms.repository;

import com.garment.wms.model.TrimIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrimIssuanceRepository extends JpaRepository<TrimIssuance, Long> {
    List<TrimIssuance> findByFinishingProgramId(Long finishingProgramId);
}
