package com.garment.wms.service;

import com.garment.wms.dto.BarTenderRowDto;
import com.garment.wms.dto.ColorAllocationDto;
import com.garment.wms.dto.LotCreationRequest;
import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuttingLotService {

    private final CuttingLotRepository cuttingLotRepository;
    private final CuttingLotColorRepository cuttingLotColorRepository;
    private final StyleRepository styleRepository;
    private final SizeSetRepository sizeSetRepository;
    private final SetBundleRepository setBundleRepository;
    private final SinglePieceRepository singlePieceRepository;

    @Transactional
    public CuttingLot createCuttingLot(LotCreationRequest request, String createdBy) {
        if (cuttingLotRepository.existsByLotNumber(request.getLotNumber())) {
            throw new IllegalArgumentException("Lot number '" + request.getLotNumber() + "' already exists!");
        }

        Style style = styleRepository.findById(request.getStyleId())
                .orElseThrow(() -> new IllegalArgumentException("Style not found with ID: " + request.getStyleId()));

        SizeSet sizeSet = sizeSetRepository.findById(request.getSizeSetId())
                .orElseThrow(() -> new IllegalArgumentException("Size Set not found with ID: " + request.getSizeSetId()));

        int totalRatioPieces = sizeSet.getTotalRatioPieces();
        if (totalRatioPieces <= 0) {
            throw new IllegalArgumentException("Size Set '" + sizeSet.getSetName() + "' must have at least one size ratio with count > 0");
        }

        int totalAllocated = request.getColors().stream().mapToInt(ColorAllocationDto::getPiecesAllocated).sum();
        int finalTotalPieces = request.getTotalPieces() > 0 ? request.getTotalPieces() : totalAllocated;

        CuttingLot lot = CuttingLot.builder()
                .lotNumber(request.getLotNumber().trim().toUpperCase())
                .style(style)
                .sizeSet(sizeSet)
                .totalPieces(finalTotalPieces)
                .status("PLANNED")
                .createdBy(createdBy != null ? createdBy : "ADMIN")
                .createdAt(OffsetDateTime.now())
                .build();

        CuttingLot savedLot = cuttingLotRepository.save(lot);

        String ratioSummary = formatRatioSummary(sizeSet);

        // Process each color allocation
        for (ColorAllocationDto colorDto : request.getColors()) {
            int allocated = colorDto.getPiecesAllocated();
            if (allocated <= 0) continue;

            String colorName = colorDto.getColorName().trim();
            String cleanColor = colorName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            String cleanLot = savedLot.getLotNumber().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

            int calculatedSets = allocated / totalRatioPieces;
            int loosePieces = allocated % totalRatioPieces;

            CuttingLotColor lotColor = CuttingLotColor.builder()
                    .cuttingLot(savedLot)
                    .colorName(colorName)
                    .piecesAllocated(allocated)
                    .calculatedSets(calculatedSets)
                    .loosePieces(loosePieces)
                    .build();

            cuttingLotColorRepository.save(lotColor);

            // Generate Complete Sets and Child Single Pieces
            for (int s = 1; s <= calculatedSets; s++) {
                String setBarcode = String.format("SET-%s-%s-%03d", cleanLot, cleanColor, s);

                SetBundle bundle = SetBundle.builder()
                        .barcode(setBarcode)
                        .cuttingLot(savedLot)
                        .colorName(colorName)
                        .setIndex(s)
                        .status(InventoryStatus.PLANNED)
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now())
                        .build();

                SetBundle savedBundle = setBundleRepository.save(bundle);

                // Child pieces for this set according to ratio breakdown
                int pieceInSet = 1;
                for (SizeSetRatio ratio : sizeSet.getRatios()) {
                    for (int r = 0; r < ratio.getRatioCount(); r++) {
                        String pieceBarcode = String.format("PC-%s-%s-%s-S%03d-%02d",
                                cleanLot, cleanColor, ratio.getSizeName().replaceAll("[^a-zA-Z0-9]", ""), s, pieceInSet++);

                        SinglePiece piece = SinglePiece.builder()
                                .barcode(pieceBarcode)
                                .parentSet(savedBundle)
                                .cuttingLot(savedLot)
                                .colorName(colorName)
                                .sizeName(ratio.getSizeName())
                                .isLoose(false)
                                .status(InventoryStatus.PLANNED)
                                .createdAt(OffsetDateTime.now())
                                .updatedAt(OffsetDateTime.now())
                                .build();

                        singlePieceRepository.save(piece);
                    }
                }
            }

            // Generate Loose Pieces if any
            if (loosePieces > 0) {
                List<SizeSetRatio> ratios = sizeSet.getRatios();
                int ratioIdx = 0;
                for (int l = 1; l <= loosePieces; l++) {
                    SizeSetRatio ratio = ratios.get(ratioIdx % ratios.size());
                    ratioIdx++;

                    String looseBarcode = String.format("PC-%s-%s-%s-L%03d",
                            cleanLot, cleanColor, ratio.getSizeName().replaceAll("[^a-zA-Z0-9]", ""), l);

                    SinglePiece loosePiece = SinglePiece.builder()
                            .barcode(looseBarcode)
                            .parentSet(null)
                            .cuttingLot(savedLot)
                            .colorName(colorName)
                            .sizeName(ratio.getSizeName())
                            .isLoose(true)
                            .status(InventoryStatus.PLANNED)
                            .createdAt(OffsetDateTime.now())
                            .updatedAt(OffsetDateTime.now())
                            .build();

                    singlePieceRepository.save(loosePiece);
                }
            }
        }

        return savedLot;
    }

    @Transactional(readOnly = true)
    public List<BarTenderRowDto> getBarTenderRows(Long lotId, String itemTypeFilter) {
        CuttingLot lot = cuttingLotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found: " + lotId));

        String ratioSummary = formatRatioSummary(lot.getSizeSet());
        List<BarTenderRowDto> rows = new ArrayList<>();

        boolean includeSets = itemTypeFilter == null || "ALL".equalsIgnoreCase(itemTypeFilter) || "SETS".equalsIgnoreCase(itemTypeFilter);
        boolean includePieces = itemTypeFilter == null || "ALL".equalsIgnoreCase(itemTypeFilter) || "PIECES".equalsIgnoreCase(itemTypeFilter);

        if (includeSets) {
            List<SetBundle> bundles = setBundleRepository.findByCuttingLotIdOrderBySetIndexAsc(lotId);
            for (SetBundle b : bundles) {
                rows.add(BarTenderRowDto.builder()
                        .barcode(b.getBarcode())
                        .lotNo(lot.getLotNumber())
                        .styleCode(lot.getStyle().getStyleCode())
                        .designName(lot.getStyle().getDesignName())
                        .color(b.getColorName())
                        .size("SET (ALL)")
                        .itemType("SET")
                        .setCode(String.format("SET #%d", b.getSetIndex()))
                        .setRatio(ratioSummary)
                        .finishingUnit(b.getCurrentFinishingUnit() != null ? b.getCurrentFinishingUnit().getUnitName() : "")
                        .build());
            }
        }

        if (includePieces) {
            List<SinglePiece> pieces = singlePieceRepository.findByCuttingLotId(lotId);
            for (SinglePiece p : pieces) {
                String setRef = p.getParentSet() != null ? String.format("SET #%d", p.getParentSet().getSetIndex()) : "LOOSE PIECE";
                rows.add(BarTenderRowDto.builder()
                        .barcode(p.getBarcode())
                        .lotNo(lot.getLotNumber())
                        .styleCode(lot.getStyle().getStyleCode())
                        .designName(lot.getStyle().getDesignName())
                        .color(p.getColorName())
                        .size(p.getSizeName())
                        .itemType(p.isLoose() ? "LOOSE_PIECE" : "SET_PIECE")
                        .setCode(setRef)
                        .setRatio(ratioSummary)
                        .finishingUnit(p.getCurrentFinishingUnit() != null ? p.getCurrentFinishingUnit().getUnitName() : "")
                        .build());
            }
        }

        return rows;
    }

    public String formatRatioSummary(SizeSet sizeSet) {
        if (sizeSet == null || sizeSet.getRatios() == null) return "";
        return sizeSet.getRatios().stream()
                .map(r -> r.getSizeName() + ":" + r.getRatioCount())
                .collect(Collectors.joining(" / ")) + " (Tot " + sizeSet.getTotalRatioPieces() + " pcs)";
    }
}
