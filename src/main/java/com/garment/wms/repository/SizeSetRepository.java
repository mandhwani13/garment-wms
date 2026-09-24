package com.garment.wms.repository;

import com.garment.wms.model.SizeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SizeSetRepository extends JpaRepository<SizeSet, Long> {
    Optional<SizeSet> findBySetName(String setName);
    boolean existsBySetName(String setName);
}
