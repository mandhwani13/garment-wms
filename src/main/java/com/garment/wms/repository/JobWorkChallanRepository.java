package com.garment.wms.repository;

import com.garment.wms.model.JobWorkChallan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobWorkChallanRepository extends JpaRepository<JobWorkChallan, Long> {
    Optional<JobWorkChallan> findByChallanNo(String challanNo);
    List<JobWorkChallan> findAllByOrderByCreatedAtDesc();
    List<JobWorkChallan> findByCuttingLotId(Long cuttingLotId);
    List<JobWorkChallan> findByJobWorkerIdOrderByCreatedAtDesc(Long jobWorkerId);
    boolean existsByChallanNo(String challanNo);
}
