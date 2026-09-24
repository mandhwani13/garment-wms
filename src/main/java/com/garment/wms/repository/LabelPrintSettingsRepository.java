package com.garment.wms.repository;

import com.garment.wms.model.LabelPrintSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelPrintSettingsRepository extends JpaRepository<LabelPrintSettings, Long> {
    Optional<LabelPrintSettings> findByIsDefaultTrue();
    List<LabelPrintSettings> findByUserIdOrUserIsNull(Long userId);
}
