package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "washing_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WashingBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_no", nullable = false, unique = true, length = 50)
    private String batchNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cutting_lot_id", nullable = false)
    private CuttingLot cuttingLot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "washing_unit_id", nullable = false)
    private Party washingUnit;

    @Column(name = "color_name", nullable = false, length = 50)
    private String colorName;

    @Column(name = "pieces_in", nullable = false)
    private int piecesIn;

    @Column(name = "pieces_out", nullable = false)
    @Builder.Default
    private int piecesOut = 0;

    @Column(name = "shrinkage_actual_pct", nullable = false)
    @Builder.Default
    private double shrinkageActualPct = 0.0;

    @Column(name = "rejection_pieces", nullable = false)
    @Builder.Default
    private int rejectionPieces = 0;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "SENT_TO_WASH"; // SENT_TO_WASH, WASHED_COMPLETED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
