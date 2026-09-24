package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "job_work_challans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobWorkChallan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challan_no", nullable = false, unique = true, length = 50)
    private String challanNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cutting_lot_id", nullable = false)
    private CuttingLot cuttingLot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_worker_id", nullable = false)
    private Party jobWorker;

    @Column(name = "component_type", nullable = false, length = 50)
    private String componentType; // BACK_POCKET_EMBROIDERY, WAISTBAND_EMBOSS, FRONT_PANEL_LASER_PRINT, CHEST_EMBROIDERY

    @Column(name = "pieces_sent", nullable = false)
    private int piecesSent;

    @Column(name = "pieces_received", nullable = false)
    @Builder.Default
    private int piecesReceived = 0;

    @Column(name = "damaged_pieces", nullable = false)
    @Builder.Default
    private int damagedPieces = 0;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ISSUED"; // ISSUED, RECEIVED_COMPLETED, DISCREPANCY

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "returned_at")
    private OffsetDateTime returnedAt;
}
