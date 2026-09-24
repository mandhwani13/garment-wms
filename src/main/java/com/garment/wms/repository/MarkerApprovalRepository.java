package com.garment.wms.repository;

import com.garment.wms.model.MarkerApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkerApprovalRepository extends JpaRepository<MarkerApproval, Long> {
    Optional<MarkerApproval> findByMarkerCode(String markerCode);
    List<MarkerApproval> findAllByOrderByCreatedAtDesc();
    List<MarkerApproval> findByStatusOrderByCreatedAtDesc(String status);
    List<MarkerApproval> findByStitchingUnitIdOrderByCreatedAtDesc(Long stitchingUnitId);
    boolean existsByMarkerCode(String markerCode);
}
