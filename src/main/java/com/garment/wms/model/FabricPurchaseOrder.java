package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fabric_purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricPurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consignment_no", nullable = false, unique = true, length = 50)
    private String consignmentNo;

    @Column(name = "po_bill_no", nullable = false, length = 50)
    private String poBillNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_mill_id", nullable = false)
    private Party fabricMill;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_stitching_unit_id", nullable = false)
    private Party destinationStitchingUnit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_id", nullable = false)
    private FabricMaster fabric;

    @Column(name = "total_rolls", nullable = false)
    private int totalRolls;

    @Column(name = "total_billed_meters", nullable = false)
    private double totalBilledMeters;

    @Column(name = "rate_per_meter", nullable = false)
    private double ratePerMeter;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "IN_TRANSIT"; // IN_TRANSIT, INWARDED, DISCREPANCY_PENDING, APPROVED

    @Column(name = "transit_slip_no", length = 100)
    private String transitSlipNo;

    @Column(name = "grn_number", length = 50)
    private String grnNumber;

    @Column(name = "admin_approval_notes", columnDefinition = "TEXT")
    private String adminApprovalNotes;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FabricRollInward> rolls = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "inwarded_at")
    private OffsetDateTime inwardedAt;
}
