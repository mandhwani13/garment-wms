package com.garment.wms.service;

import com.garment.wms.dto.ScanRequestDto;
import com.garment.wms.dto.ScanResponseDto;
import com.garment.wms.dto.StockFilterDto;
import com.garment.wms.dto.StockSummaryDto;
import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SetBundleRepository setBundleRepository;
    private final SinglePieceRepository singlePieceRepository;
    private final FinishingUnitRepository finishingUnitRepository;
    private final CustomerRepository customerRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final CuttingLotRepository cuttingLotRepository;

    @Transactional
    public ScanResponseDto processScan(ScanRequestDto request, String username) {
        if (request.getBarcode() == null || request.getBarcode().trim().isEmpty()) {
            return ScanResponseDto.builder()
                    .success(false)
                    .message("Barcode cannot be empty")
                    .build();
        }

        String barcode = request.getBarcode().trim();
        TransactionType transType = request.getTransactionType() != null ? request.getTransactionType() : TransactionType.INWARD;

        if (transType == TransactionType.INWARD) {
            return processInward(barcode, request, username);
        } else {
            return processOutward(barcode, request, username);
        }
    }

    private ScanResponseDto processInward(String barcode, ScanRequestDto request, String username) {
        FinishingUnit unit = null;
        if (request.getFinishingUnitId() != null) {
            unit = finishingUnitRepository.findById(request.getFinishingUnitId()).orElse(null);
        }

        // 1. Check if barcode is a SetBundle
        Optional<SetBundle> bundleOpt = setBundleRepository.findByBarcode(barcode);
        if (bundleOpt.isPresent()) {
            SetBundle bundle = bundleOpt.get();
            InventoryStatus prevStatus = bundle.getStatus();
            bundle.setStatus(InventoryStatus.IN_WAREHOUSE);
            if (unit != null) bundle.setCurrentFinishingUnit(unit);
            bundle.setUpdatedAt(OffsetDateTime.now());
            setBundleRepository.save(bundle);

            List<SinglePiece> children = singlePieceRepository.findByParentSetId(bundle.getId());
            for (SinglePiece piece : children) {
                piece.setStatus(InventoryStatus.IN_WAREHOUSE);
                if (unit != null) piece.setCurrentFinishingUnit(unit);
                piece.setUpdatedAt(OffsetDateTime.now());
                singlePieceRepository.save(piece);
            }

            // Log Transaction
            InventoryTransaction trans = InventoryTransaction.builder()
                    .transactionType(TransactionType.INWARD)
                    .barcodeType(BarcodeType.SET)
                    .barcodeValue(bundle.getBarcode())
                    .piecesCount(children.size())
                    .cuttingLot(bundle.getCuttingLot())
                    .styleCode(bundle.getCuttingLot().getStyle().getStyleCode())
                    .colorName(bundle.getColorName())
                    .sizeName("SET BUNDLE (" + children.size() + " pcs)")
                    .partyName(unit != null ? unit.getUnitName() : "Inward Staging")
                    .referenceNo(request.getReferenceNo())
                    .remarks(request.getRemarks())
                    .performedBy(username)
                    .createdAt(OffsetDateTime.now())
                    .build();
            transactionRepository.save(trans);

            return ScanResponseDto.builder()
                    .success(true)
                    .message("Inward Successful: Set Bundle " + bundle.getBarcode() + " (" + children.size() + " pcs)")
                    .barcodeType(BarcodeType.SET)
                    .barcode(bundle.getBarcode())
                    .piecesUpdated(children.size())
                    .previousStatus(prevStatus)
                    .currentStatus(InventoryStatus.IN_WAREHOUSE)
                    .lotNumber(bundle.getCuttingLot().getLotNumber())
                    .styleCode(bundle.getCuttingLot().getStyle().getStyleCode())
                    .designName(bundle.getCuttingLot().getStyle().getDesignName())
                    .color(bundle.getColorName())
                    .size("BUNDLE (" + children.size() + " pcs)")
                    .partyName(unit != null ? unit.getUnitName() : "Internal")
                    .build();
        }

        // 2. Check if barcode is a SinglePiece
        Optional<SinglePiece> pieceOpt = singlePieceRepository.findByBarcode(barcode);
        if (pieceOpt.isPresent()) {
            SinglePiece piece = pieceOpt.get();
            InventoryStatus prevStatus = piece.getStatus();
            piece.setStatus(InventoryStatus.IN_WAREHOUSE);
            if (unit != null) piece.setCurrentFinishingUnit(unit);
            piece.setUpdatedAt(OffsetDateTime.now());
            singlePieceRepository.save(piece);

            // Log Transaction
            InventoryTransaction trans = InventoryTransaction.builder()
                    .transactionType(TransactionType.INWARD)
                    .barcodeType(BarcodeType.SINGLE)
                    .barcodeValue(piece.getBarcode())
                    .piecesCount(1)
                    .cuttingLot(piece.getCuttingLot())
                    .styleCode(piece.getCuttingLot().getStyle().getStyleCode())
                    .colorName(piece.getColorName())
                    .sizeName(piece.getSizeName())
                    .partyName(unit != null ? unit.getUnitName() : "Inward Staging")
                    .referenceNo(request.getReferenceNo())
                    .remarks(request.getRemarks())
                    .performedBy(username)
                    .createdAt(OffsetDateTime.now())
                    .build();
            transactionRepository.save(trans);

            return ScanResponseDto.builder()
                    .success(true)
                    .message("Inward Successful: Piece " + piece.getBarcode() + " [" + piece.getSizeName() + "]")
                    .barcodeType(BarcodeType.SINGLE)
                    .barcode(piece.getBarcode())
                    .piecesUpdated(1)
                    .previousStatus(prevStatus)
                    .currentStatus(InventoryStatus.IN_WAREHOUSE)
                    .lotNumber(piece.getCuttingLot().getLotNumber())
                    .styleCode(piece.getCuttingLot().getStyle().getStyleCode())
                    .designName(piece.getCuttingLot().getStyle().getDesignName())
                    .color(piece.getColorName())
                    .size(piece.getSizeName())
                    .partyName(unit != null ? unit.getUnitName() : "Internal")
                    .build();
        }

        return ScanResponseDto.builder()
                .success(false)
                .message("Barcode not recognized in system: " + barcode)
                .barcode(barcode)
                .build();
    }

    private ScanResponseDto processOutward(String barcode, ScanRequestDto request, String username) {
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        }

        if (customer == null) {
            return ScanResponseDto.builder()
                    .success(false)
                    .message("Validation Error: Please select an active Customer for Outward dispatch.")
                    .barcode(barcode)
                    .build();
        }

        // 1. Check if barcode is a SetBundle
        Optional<SetBundle> bundleOpt = setBundleRepository.findByBarcode(barcode);
        if (bundleOpt.isPresent()) {
            SetBundle bundle = bundleOpt.get();
            if (bundle.getStatus() != InventoryStatus.IN_WAREHOUSE) {
                return ScanResponseDto.builder()
                        .success(false)
                        .message("Outward Validation Failed: Set Bundle " + barcode + " is NOT in warehouse (Current status: " + bundle.getStatus().getDisplayName() + ").")
                        .barcodeType(BarcodeType.SET)
                        .barcode(bundle.getBarcode())
                        .currentStatus(bundle.getStatus())
                        .build();
            }

            bundle.setStatus(InventoryStatus.DISPATCHED);
            bundle.setCurrentCustomer(customer);
            bundle.setUpdatedAt(OffsetDateTime.now());
            setBundleRepository.save(bundle);

            List<SinglePiece> children = singlePieceRepository.findByParentSetId(bundle.getId());
            for (SinglePiece piece : children) {
                piece.setStatus(InventoryStatus.DISPATCHED);
                piece.setCurrentCustomer(customer);
                piece.setUpdatedAt(OffsetDateTime.now());
                singlePieceRepository.save(piece);
            }

            // Log Transaction
            InventoryTransaction trans = InventoryTransaction.builder()
                    .transactionType(TransactionType.OUTWARD)
                    .barcodeType(BarcodeType.SET)
                    .barcodeValue(bundle.getBarcode())
                    .piecesCount(children.size())
                    .cuttingLot(bundle.getCuttingLot())
                    .styleCode(bundle.getCuttingLot().getStyle().getStyleCode())
                    .colorName(bundle.getColorName())
                    .sizeName("SET BUNDLE (" + children.size() + " pcs)")
                    .partyName(customer.getCustomerName())
                    .referenceNo(request.getReferenceNo())
                    .remarks(request.getRemarks())
                    .performedBy(username)
                    .createdAt(OffsetDateTime.now())
                    .build();
            transactionRepository.save(trans);

            return ScanResponseDto.builder()
                    .success(true)
                    .message("Outward Dispatched: Set Bundle " + bundle.getBarcode() + " (" + children.size() + " pcs) to " + customer.getCustomerName())
                    .barcodeType(BarcodeType.SET)
                    .barcode(bundle.getBarcode())
                    .piecesUpdated(children.size())
                    .previousStatus(InventoryStatus.IN_WAREHOUSE)
                    .currentStatus(InventoryStatus.DISPATCHED)
                    .lotNumber(bundle.getCuttingLot().getLotNumber())
                    .styleCode(bundle.getCuttingLot().getStyle().getStyleCode())
                    .designName(bundle.getCuttingLot().getStyle().getDesignName())
                    .color(bundle.getColorName())
                    .size("BUNDLE (" + children.size() + " pcs)")
                    .partyName(customer.getCustomerName())
                    .build();
        }

        // 2. Check if barcode is a SinglePiece
        Optional<SinglePiece> pieceOpt = singlePieceRepository.findByBarcode(barcode);
        if (pieceOpt.isPresent()) {
            SinglePiece piece = pieceOpt.get();
            if (piece.getStatus() != InventoryStatus.IN_WAREHOUSE) {
                return ScanResponseDto.builder()
                        .success(false)
                        .message("Outward Validation Failed: Piece " + barcode + " is NOT in warehouse (Current status: " + piece.getStatus().getDisplayName() + ").")
                        .barcodeType(BarcodeType.SINGLE)
                        .barcode(piece.getBarcode())
                        .currentStatus(piece.getStatus())
                        .build();
            }

            piece.setStatus(InventoryStatus.DISPATCHED);
            piece.setCurrentCustomer(customer);
            piece.setUpdatedAt(OffsetDateTime.now());
            singlePieceRepository.save(piece);

            // Log Transaction
            InventoryTransaction trans = InventoryTransaction.builder()
                    .transactionType(TransactionType.OUTWARD)
                    .barcodeType(BarcodeType.SINGLE)
                    .barcodeValue(piece.getBarcode())
                    .piecesCount(1)
                    .cuttingLot(piece.getCuttingLot())
                    .styleCode(piece.getCuttingLot().getStyle().getStyleCode())
                    .colorName(piece.getColorName())
                    .sizeName(piece.getSizeName())
                    .partyName(customer.getCustomerName())
                    .referenceNo(request.getReferenceNo())
                    .remarks(request.getRemarks())
                    .performedBy(username)
                    .createdAt(OffsetDateTime.now())
                    .build();
            transactionRepository.save(trans);

            return ScanResponseDto.builder()
                    .success(true)
                    .message("Outward Dispatched: Piece " + piece.getBarcode() + " [" + piece.getSizeName() + "] to " + customer.getCustomerName())
                    .barcodeType(BarcodeType.SINGLE)
                    .barcode(piece.getBarcode())
                    .piecesUpdated(1)
                    .previousStatus(InventoryStatus.IN_WAREHOUSE)
                    .currentStatus(InventoryStatus.DISPATCHED)
                    .lotNumber(piece.getCuttingLot().getLotNumber())
                    .styleCode(piece.getCuttingLot().getStyle().getStyleCode())
                    .designName(piece.getCuttingLot().getStyle().getDesignName())
                    .color(piece.getColorName())
                    .size(piece.getSizeName())
                    .partyName(customer.getCustomerName())
                    .build();
        }

        return ScanResponseDto.builder()
                .success(false)
                .message("Barcode not recognized in system: " + barcode)
                .barcode(barcode)
                .build();
    }

    public StockSummaryDto getStockSummary() {
        return StockSummaryDto.builder()
                .totalLots(cuttingLotRepository.countTotalLots())
                .totalInWarehousePieces(singlePieceRepository.countByStatus(InventoryStatus.IN_WAREHOUSE))
                .completeSetPieces(singlePieceRepository.countByStatusAndIsLooseFalse(InventoryStatus.IN_WAREHOUSE))
                .brokenLoosePieces(singlePieceRepository.countByStatusAndIsLooseTrue(InventoryStatus.IN_WAREHOUSE))
                .totalDispatchedPieces(singlePieceRepository.countByStatus(InventoryStatus.DISPATCHED))
                .totalPlannedPieces(singlePieceRepository.countByStatus(InventoryStatus.PLANNED))
                .build();
    }

    public List<SinglePiece> filterStock(StockFilterDto filter) {
        return singlePieceRepository.filterStock(
                filter.getLotId(),
                filter.getStyleId(),
                filter.getColor(),
                filter.getSize(),
                filter.getUnitId(),
                filter.getStatus()
        );
    }
}
