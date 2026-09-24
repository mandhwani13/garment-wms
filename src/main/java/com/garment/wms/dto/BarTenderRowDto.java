package com.garment.wms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarTenderRowDto {
    private String barcode;
    private String lotNo;
    private String styleCode;
    private String designName;
    private String color;
    private String size;
    private String itemType;
    private String setCode;
    private String setRatio;
    private String finishingUnit;
}
