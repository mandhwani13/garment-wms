package com.garment.wms.repository;

import com.garment.wms.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLocationCode(String locationCode);
    List<Location> findByActiveTrue();
    List<Location> findByLocationType(String locationType);
    boolean existsByLocationCode(String locationCode);
}
