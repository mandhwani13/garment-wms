package com.garment.wms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TsplPrintRequestDto {
    private Long lotId;
    private String itemType; // "ALL", "SETS", "PIECES"
    private double widthMm;
    private double heightMm;
    private int density;
    private int direction;
    private String barcodeType; // "CODE128" or "QR"
}
