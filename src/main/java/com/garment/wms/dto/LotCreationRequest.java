package com.garment.wms.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotCreationRequest {
    private String lotNumber;
    private Long styleId;
    private Long sizeSetId;
    private int totalPieces;
    @Builder.Default
    private List<ColorAllocationDto> colors = new ArrayList<>();
}
