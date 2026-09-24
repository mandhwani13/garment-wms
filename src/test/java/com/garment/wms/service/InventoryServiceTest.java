package com.garment.wms.service;

import com.garment.wms.dto.ScanRequestDto;
import com.garment.wms.dto.ScanResponseDto;
import com.garment.wms.model.*;
import com.garment.wms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private SetBundleRepository setBundleRepository;
    @Mock
    private SinglePieceRepository singlePieceRepository;
    @Mock
    private FinishingUnitRepository finishingUnitRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InventoryTransactionRepository transactionRepository;
    @Mock
    private CuttingLotRepository cuttingLotRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Style style;
    private CuttingLot lot;
    private SetBundle setBundle;
    private SinglePiece piece1;
    private SinglePiece piece2;
    private FinishingUnit finishingUnit;
    private Customer customer;

    @BeforeEach
    void setUp() {
        style = Style.builder().id(1L).styleCode("STY-101").designName("Oxford Shirt").build();
        lot = CuttingLot.builder().id(1L).lotNumber("LOT-2026-001").style(style).build();

        finishingUnit = FinishingUnit.builder().id(1L).unitCode("FU-01").unitName("Apex Finishing").build();
        customer = Customer.builder().id(1L).customerCode("CUST-01").customerName("Nordic Retail").build();

        setBundle = SetBundle.builder()
                .id(10L)
                .barcode("SET-LOT01-NAVY-001")
                .cuttingLot(lot)
                .colorName("Navy")
                .setIndex(1)
                .status(InventoryStatus.PLANNED)
                .build();

        piece1 = SinglePiece.builder()
                .id(101L)
                .barcode("PC-LOT01-NAVY-30-001")
                .cuttingLot(lot)
                .parentSet(setBundle)
                .colorName("Navy")
                .sizeName("30")
                .status(InventoryStatus.PLANNED)
                .build();

        piece2 = SinglePiece.builder()
                .id(102L)
                .barcode("PC-LOT01-NAVY-32-002")
                .cuttingLot(lot)
                .parentSet(setBundle)
                .colorName("Navy")
                .sizeName("32")
                .status(InventoryStatus.PLANNED)
                .build();
    }

    @Test
    void testInwardSetBundleTransitionsAllChildPieces() {
        when(setBundleRepository.findByBarcode("SET-LOT01-NAVY-001")).thenReturn(Optional.of(setBundle));
        when(finishingUnitRepository.findById(1L)).thenReturn(Optional.of(finishingUnit));
        when(singlePieceRepository.findByParentSetId(10L)).thenReturn(List.of(piece1, piece2));

        ScanRequestDto req = ScanRequestDto.builder()
                .barcode("SET-LOT01-NAVY-001")
                .transactionType(TransactionType.INWARD)
                .finishingUnitId(1L)
                .build();

        ScanResponseDto res = inventoryService.processScan(req, "test_operator");

        assertTrue(res.isSuccess());
        assertEquals(InventoryStatus.IN_WAREHOUSE, res.getCurrentStatus());
        assertEquals(2, res.getPiecesUpdated());
        assertEquals(InventoryStatus.IN_WAREHOUSE, setBundle.getStatus());
        assertEquals(InventoryStatus.IN_WAREHOUSE, piece1.getStatus());
        assertEquals(InventoryStatus.IN_WAREHOUSE, piece2.getStatus());

        verify(transactionRepository, times(1)).save(any(InventoryTransaction.class));
    }

    @Test
    void testOutwardRejectsIfItemNotInWarehouse() {
        // Status is still PLANNED, not IN_WAREHOUSE
        when(setBundleRepository.findByBarcode("SET-LOT01-NAVY-001")).thenReturn(Optional.of(setBundle));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        ScanRequestDto req = ScanRequestDto.builder()
                .barcode("SET-LOT01-NAVY-001")
                .transactionType(TransactionType.OUTWARD)
                .customerId(1L)
                .build();

        ScanResponseDto res = inventoryService.processScan(req, "test_operator");

        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("Outward Validation Failed"));
        assertEquals(InventoryStatus.PLANNED, setBundle.getStatus());
    }

    @Test
    void testOutwardSucceedsWhenInWarehouse() {
        setBundle.setStatus(InventoryStatus.IN_WAREHOUSE);
        piece1.setStatus(InventoryStatus.IN_WAREHOUSE);
        piece2.setStatus(InventoryStatus.IN_WAREHOUSE);

        when(setBundleRepository.findByBarcode("SET-LOT01-NAVY-001")).thenReturn(Optional.of(setBundle));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(singlePieceRepository.findByParentSetId(10L)).thenReturn(List.of(piece1, piece2));

        ScanRequestDto req = ScanRequestDto.builder()
                .barcode("SET-LOT01-NAVY-001")
                .transactionType(TransactionType.OUTWARD)
                .customerId(1L)
                .build();

        ScanResponseDto res = inventoryService.processScan(req, "test_operator");

        assertTrue(res.isSuccess());
        assertEquals(InventoryStatus.DISPATCHED, res.getCurrentStatus());
        assertEquals(2, res.getPiecesUpdated());
        assertEquals(InventoryStatus.DISPATCHED, setBundle.getStatus());
        assertEquals(InventoryStatus.DISPATCHED, piece1.getStatus());
        assertEquals(InventoryStatus.DISPATCHED, piece2.getStatus());
    }
}
