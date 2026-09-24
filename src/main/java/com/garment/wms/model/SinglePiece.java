package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "single_pieces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinglePiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_set_id")
    private SetBundle parentSet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cutting_lot_id", nullable = false)
    private CuttingLot cuttingLot;

    @Column(name = "color_name", nullable = false, length = 50)
    private String colorName;

    @Column(name = "size_name", nullable = false, length = 30)
    private String sizeName;

    @Column(name = "is_loose", nullable = false)
    @Builder.Default
    private boolean isLoose = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_finishing_unit_id")
    private FinishingUnit currentFinishingUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_customer_id")
    private Customer currentCustomer;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
