package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "marker_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkerApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "marker_code", nullable = false, unique = true, length = 50)
    private String markerCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stitching_unit_id", nullable = false)
    private Party stitchingUnit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_id", nullable = false)
    private FabricMaster fabric;

    @Column(name = "roll_width_utilized", nullable = false)
    private double rollWidthUtilized;

    @Column(name = "marker_length_meters", nullable = false)
    private double markerLengthMeters;

    @Column(name = "lay_count", nullable = false)
    private int layCount;

    @Column(name = "calculated_average_meters", nullable = false)
    private double calculatedAverageMeters;

    @Column(name = "expected_pieces", nullable = false)
    private int expectedPieces;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "admin_remarks", columnDefinition = "TEXT")
    private String adminRemarks;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;
}
