package com.garment.wms.service;

import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WashingService {

    private final WashingBatchRepository batchRepository;
    private final CuttingLotRepository cuttingLotRepository;
    private final PartyRepository partyRepository;

    public List<WashingBatch> getAllBatches() {
        return batchRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public WashingBatch createBatch(Long cuttingLotId, Long washingUnitId, String colorName, int piecesIn, String notes) {
        CuttingLot lot = cuttingLotRepository.findById(cuttingLotId)
                .orElseThrow(() -> new IllegalArgumentException("Cutting lot not found"));
        Party unit = partyRepository.findById(washingUnitId)
                .orElseThrow(() -> new IllegalArgumentException("Washing unit not found"));

        String batchNo = "WASH-" + lot.getLotNumber() + "-" + colorName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase() + "-" + (System.currentTimeMillis() % 1000);

        WashingBatch batch = WashingBatch.builder()
                .batchNo(batchNo)
                .cuttingLot(lot)
                .washingUnit(unit)
                .colorName(colorName)
                .piecesIn(piecesIn)
                .piecesOut(0)
                .shrinkageActualPct(0.0)
                .rejectionPieces(0)
                .status("SENT_TO_WASH")
                .notes(notes)
                .createdAt(OffsetDateTime.now())
                .build();

        return batchRepository.save(batch);
    }

    @Transactional
    public WashingBatch completeWash(Long id, int piecesOut, double shrinkagePct, int rejectionPieces, String notes) {
        WashingBatch batch = batchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + id));

        batch.setPiecesOut(piecesOut);
        batch.setShrinkageActualPct(shrinkagePct);
        batch.setRejectionPieces(rejectionPieces);
        batch.setStatus("WASHED_COMPLETED");
        batch.setCompletedAt(OffsetDateTime.now());
        if (notes != null && !notes.isBlank()) batch.setNotes(notes);

        return batchRepository.save(batch);
    }
}
