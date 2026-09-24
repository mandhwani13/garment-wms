package com.garment.wms.repository;

import com.garment.wms.model.FabricMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabricMasterRepository extends JpaRepository<FabricMaster, Long> {
    Optional<FabricMaster> findByShortNumber(String shortNumber);
    List<FabricMaster> findByActiveTrue();
    boolean existsByShortNumber(String shortNumber);
}
