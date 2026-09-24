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
public class FinishingProgramService {

    private final FinishingProgramRepository programRepository;
    private final TrimIssuanceRepository trimIssuanceRepository;
    private final TrimAccessoryRepository trimRepository;
    private final CuttingLotRepository cuttingLotRepository;
    private final BrandRepository brandRepository;
    private final PartyRepository partyRepository;

    public List<FinishingProgram> getAllPrograms() {
        return programRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public FinishingProgram createProgramWithBom(Long cuttingLotId, Long brandId, Long finishingUnitId, int piecesAllocated) {
        CuttingLot lot = cuttingLotRepository.findById(cuttingLotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found"));
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        Party unit = partyRepository.findById(finishingUnitId)
                .orElseThrow(() -> new IllegalArgumentException("Finishing unit not found"));

        String progNo = "FIN-" + lot.getLotNumber() + "-" + brand.getBrandCode() + "-" + (System.currentTimeMillis() % 1000);
        String dispatchSlipNo = "DSP-" + (int)(Math.random() * 90000 + 10000);

        FinishingProgram program = FinishingProgram.builder()
                .programNo(progNo)
                .cuttingLot(lot)
                .brand(brand)
                .finishingUnit(unit)
                .piecesAllocated(piecesAllocated)
                .status("PENDING_TRIMS")
                .dispatchSlipNo(dispatchSlipNo)
                .createdAt(OffsetDateTime.now())
                .build();

        FinishingProgram saved = programRepository.save(program);

        // Auto-calculate Bill of Materials (BOM) based on Brand and Universal trims
        List<TrimAccessory> relevantTrims = trimRepository.findByBrandIdOrBrandIsNull(brand.getId());
        for (TrimAccessory trim : relevantTrims) {
            double reqQty = piecesAllocated * trim.getConsumptionPerPiece();
            TrimIssuance issuance = TrimIssuance.builder()
                    .finishingProgram(saved)
                    .trim(trim)
                    .requiredQty(reqQty)
                    .issuedQty(0.0)
                    .issuedAt(OffsetDateTime.now())
                    .build();
            trimIssuanceRepository.save(issuance);
        }

        return saved;
    }

    @Transactional
    public FinishingProgram issueTrims(Long programId) {
        FinishingProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Finishing program not found: " + programId));

        // Deduct trim inventory stock from Central Trims Warehouse
        for (TrimIssuance ti : program.getTrimIssuances()) {
            TrimAccessory trim = ti.getTrim();
            double qtyToIssue = ti.getRequiredQty();
            trim.setStockQuantity(Math.max(0.0, trim.getStockQuantity() - qtyToIssue));
            trimRepository.save(trim);

            ti.setIssuedQty(qtyToIssue);
            trimIssuanceRepository.save(ti);
        }

        program.setStatus("TRIMS_ISSUED");
        return programRepository.save(program);
    }

    @Transactional
    public FinishingProgram completeFinishing(Long programId) {
        FinishingProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found: " + programId));
        program.setStatus("PACKED_COMPLETED");
        return programRepository.save(program);
    }
}
