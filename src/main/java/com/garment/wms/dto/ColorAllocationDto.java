package com.garment.wms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorAllocationDto {
    private String colorName;
    private int piecesAllocated;
}
