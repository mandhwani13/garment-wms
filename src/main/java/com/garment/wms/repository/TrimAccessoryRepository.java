package com.garment.wms.repository;

import com.garment.wms.model.TrimAccessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrimAccessoryRepository extends JpaRepository<TrimAccessory, Long> {
    Optional<TrimAccessory> findByItemCode(String itemCode);
    List<TrimAccessory> findByActiveTrue();
    List<TrimAccessory> findByBrandIdOrBrandIsNull(Long brandId);
    boolean existsByItemCode(String itemCode);
}
