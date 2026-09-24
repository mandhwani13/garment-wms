package com.garment.wms.dto;

import com.garment.wms.model.InventoryStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockFilterDto {
    private Long lotId;
    private Long styleId;
    private String color;
    private String size;
    private Long unitId;
    private InventoryStatus status;
}
