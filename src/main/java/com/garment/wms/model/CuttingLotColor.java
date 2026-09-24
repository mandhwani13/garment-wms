package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cutting_lot_colors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuttingLotColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cutting_lot_id", nullable = false)
    private CuttingLot cuttingLot;

    @Column(name = "color_name", nullable = false, length = 50)
    private String colorName;

    @Column(name = "pieces_allocated", nullable = false)
    private int piecesAllocated;

    @Column(name = "calculated_sets", nullable = false)
    private int calculatedSets;

    @Column(name = "loose_pieces", nullable = false)
    private int loosePieces;
}
