package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trim_accessories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrimAccessory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false, unique = true, length = 50)
    private String itemCode;

    @Column(name = "item_type", nullable = false, length = 50)
    private String itemType; // MAIN_LABEL, SIZE_LABEL, LEATHER_PATCH, BUTTON, RIVET, HANG_TAG, POLYBAG, POCKETING_FABRIC

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String uom = "PIECES"; // PIECES, GROSS, METERS

    @Column(name = "unit_rate", nullable = false)
    @Builder.Default
    private double unitRate = 0.0;

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private double stockQuantity = 0.0;

    @Column(name = "alert_threshold", nullable = false)
    @Builder.Default
    private double alertThreshold = 50.0;

    @Column(name = "consumption_per_piece", nullable = false)
    @Builder.Default
    private double consumptionPerPiece = 1.0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
