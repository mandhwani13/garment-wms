package com.garment.wms.dto;

import com.garment.wms.model.BarcodeType;
import com.garment.wms.model.InventoryStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanResponseDto {
    private boolean success;
    private String message;
    private BarcodeType barcodeType;
    private String barcode;
    private int piecesUpdated;
    private InventoryStatus previousStatus;
    private InventoryStatus currentStatus;
    private String lotNumber;
    private String styleCode;
    private String designName;
    private String color;
    private String size;
    private String partyName;
}
