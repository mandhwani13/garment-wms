package com.garment.wms.repository;

import com.garment.wms.model.CuttingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuttingLotRepository extends JpaRepository<CuttingLot, Long> {
    Optional<CuttingLot> findByLotNumber(String lotNumber);
    boolean existsByLotNumber(String lotNumber);
    List<CuttingLot> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(l) FROM CuttingLot l")
    long countTotalLots();
}
