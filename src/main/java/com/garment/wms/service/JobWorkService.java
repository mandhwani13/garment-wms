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
public class JobWorkService {

    private final JobWorkChallanRepository challanRepository;
    private final CuttingLotRepository cuttingLotRepository;
    private final PartyRepository partyRepository;

    public List<JobWorkChallan> getAllChallans() {
        return challanRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public JobWorkChallan issueChallan(Long cuttingLotId, Long jobWorkerId, String componentType, int piecesSent, String notes) {
        CuttingLot lot = cuttingLotRepository.findById(cuttingLotId)
                .orElseThrow(() -> new IllegalArgumentException("Cutting lot not found"));
        Party worker = partyRepository.findById(jobWorkerId)
                .orElseThrow(() -> new IllegalArgumentException("Job worker not found"));

        String challanNo = "JW-" + lot.getLotNumber() + "-" + (System.currentTimeMillis() % 10000);

        JobWorkChallan challan = JobWorkChallan.builder()
                .challanNo(challanNo)
                .cuttingLot(lot)
                .jobWorker(worker)
                .componentType(componentType)
                .piecesSent(piecesSent)
                .piecesReceived(0)
                .damagedPieces(0)
                .status("ISSUED")
                .notes(notes)
                .createdAt(OffsetDateTime.now())
                .build();

        return challanRepository.save(challan);
    }

    @Transactional
    public JobWorkChallan receiveChallan(Long id, int received, int damaged, String notes) {
        JobWorkChallan ch = challanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Challan not found: " + id));

        ch.setPiecesReceived(received);
        ch.setDamagedPieces(damaged);
        ch.setReturnedAt(OffsetDateTime.now());
        ch.setNotes(notes);

        if (received + damaged < ch.getPiecesSent()) {
            ch.setStatus("DISCREPANCY");
        } else {
            ch.setStatus("RECEIVED_COMPLETED");
        }

        return challanRepository.save(ch);
    }
}
