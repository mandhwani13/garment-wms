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
public class FabricProcurementService {

    private final FabricPurchaseOrderRepository poRepository;
    private final FabricRollInwardRepository rollRepository;
    private final PartyRepository partyRepository;
    private final FabricMasterRepository fabricRepository;
    private final StockTransferRepository stockTransferRepository;

    public List<FabricPurchaseOrder> getAllOrders() {
        return poRepository.findAllByOrderByCreatedAtDesc();
    }

    public FabricPurchaseOrder getOrderById(Long id) {
        return poRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fabric PO not found: " + id));
    }

    @Transactional
    public FabricPurchaseOrder createFabricPO(Long millId, Long stitchingUnitId, Long fabricId,
                                            String poBillNo, int rolls, double billedMeters, double ratePerMeter) {
        Party mill = partyRepository.findById(millId)
                .orElseThrow(() -> new IllegalArgumentException("Mill party not found"));
        Party stitch = partyRepository.findById(stitchingUnitId)
                .orElseThrow(() -> new IllegalArgumentException("Stitching unit party not found"));
        FabricMaster fabric = fabricRepository.findById(fabricId)
                .orElseThrow(() -> new IllegalArgumentException("Fabric master not found"));

        String consignmentNo = "FCON-" + System.currentTimeMillis() % 1000000;
        String transitSlipNo = "TRN-" + (int)(Math.random() * 90000 + 10000);

        FabricPurchaseOrder po = FabricPurchaseOrder.builder()
                .consignmentNo(consignmentNo)
                .poBillNo(poBillNo.trim().toUpperCase())
                .fabricMill(mill)
                .destinationStitchingUnit(stitch)
                .fabric(fabric)
                .totalRolls(rolls)
                .totalBilledMeters(billedMeters)
                .ratePerMeter(ratePerMeter)
                .totalAmount(billedMeters * ratePerMeter)
                .status("IN_TRANSIT")
                .transitSlipNo(transitSlipNo)
                .createdAt(OffsetDateTime.now())
                .build();

        FabricPurchaseOrder saved = poRepository.save(po);

        // Pre-create planned roll placeholders
        double avgMeters = billedMeters / rolls;
        for (int i = 1; i <= rolls; i++) {
            FabricRollInward roll = FabricRollInward.builder()
                    .purchaseOrder(saved)
                    .rollNumber(String.format("R%02d", i))
                    .billedMeters(Math.round(avgMeters * 10.0) / 10.0)
                    .receivedMeters(Math.round(avgMeters * 10.0) / 10.0)
                    .weightKg(Math.round(avgMeters * 0.42 * 10.0) / 10.0)
                    .hasDefects(false)
                    .createdAt(OffsetDateTime.now())
                    .build();
            rollRepository.save(roll);
        }

        return saved;
    }

    @Transactional
    public FabricPurchaseOrder processRollInward(Long poId, List<Double> receivedMetersList,
                                                List<Double> weights, List<Boolean> defectsList,
                                                List<String> defectNotesList, String adminNotes) {
        FabricPurchaseOrder po = getOrderById(poId);
        List<FabricRollInward> existingRolls = po.getRolls();

        double totalReceived = 0.0;
        boolean hasAnyDefectOrShortage = false;

        for (int i = 0; i < existingRolls.size(); i++) {
            FabricRollInward roll = existingRolls.get(i);
            if (i < receivedMetersList.size()) {
                double rec = receivedMetersList.get(i);
                roll.setReceivedMeters(rec);
                totalReceived += rec;
                if (Math.abs(rec - roll.getBilledMeters()) > 0.5) {
                    hasAnyDefectOrShortage = true;
                }
            }
            if (i < weights.size()) roll.setWeightKg(weights.get(i));
            if (i < defectsList.size()) {
                boolean def = defectsList.get(i) != null && defectsList.get(i);
                roll.setHasDefects(def);
                if (def) hasAnyDefectOrShortage = true;
            }
            if (i < defectNotesList.size()) roll.setDefectNotes(defectNotesList.get(i));
            rollRepository.save(roll);
        }

        po.setGrnNumber("GRN-" + po.getConsignmentNo().replace("FCON-", ""));
        po.setInwardedAt(OffsetDateTime.now());
        po.setAdminApprovalNotes(adminNotes);

        if (hasAnyDefectOrShortage) {
            po.setStatus("DISCREPANCY_PENDING");
        } else {
            po.setStatus("INWARDED");
        }

        return poRepository.save(po);
    }

    @Transactional
    public FabricPurchaseOrder approveDiscrepancy(Long poId, String approvalNotes) {
        FabricPurchaseOrder po = getOrderById(poId);
        po.setStatus("APPROVED");
        po.setAdminApprovalNotes(approvalNotes);
        return poRepository.save(po);
    }
}
