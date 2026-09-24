package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fabric_roll_inwards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricRollInward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private FabricPurchaseOrder purchaseOrder;

    @Column(name = "roll_number", nullable = false, length = 30)
    private String rollNumber;

    @Column(name = "billed_meters", nullable = false)
    private double billedMeters;

    @Column(name = "received_meters", nullable = false)
    private double receivedMeters;

    @Column(name = "weight_kg", nullable = false)
    @Builder.Default
    private double weightKg = 0.0;

    @Column(name = "has_defects", nullable = false)
    @Builder.Default
    private boolean hasDefects = false;

    @Column(name = "defect_notes", columnDefinition = "TEXT")
    private String defectNotes;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
