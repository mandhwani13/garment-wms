package com.garment.wms.dto;

import com.garment.wms.model.TransactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanRequestDto {
    private String barcode;
    private TransactionType transactionType;
    private Long finishingUnitId;
    private Long customerId;
    private String referenceNo;
    private String remarks;
}
