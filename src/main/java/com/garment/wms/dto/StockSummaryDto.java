package com.garment.wms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSummaryDto {
    private long totalLots;
    private long totalInWarehousePieces;
    private long completeSetPieces;
    private long brokenLoosePieces;
    private long totalDispatchedPieces;
    private long totalPlannedPieces;
}
