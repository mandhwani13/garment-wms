package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transfer_no", nullable = false, unique = true, length = 50)
    private String transferNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    @Column(name = "item_type", nullable = false, length = 30)
    private String itemType; // FABRIC, TRIM, CUT_PANELS, WASHED_GARMENTS, FINISHED_PACK

    @Column(name = "reference_id", length = 50)
    private String referenceId;

    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "DISPATCHED"; // DISPATCHED, IN_TRANSIT, RECEIVED

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "received_at")
    private OffsetDateTime receivedAt;
}
