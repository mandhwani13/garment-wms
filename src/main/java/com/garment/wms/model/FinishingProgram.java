package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "finishing_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "program_no", nullable = false, unique = true, length = 50)
    private String programNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cutting_lot_id", nullable = false)
    private CuttingLot cuttingLot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "finishing_unit_id", nullable = false)
    private Party finishingUnit;

    @Column(name = "pieces_allocated", nullable = false)
    private int piecesAllocated;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING_TRIMS"; // PENDING_TRIMS, TRIMS_ISSUED, PACKED_COMPLETED

    @Column(name = "dispatch_slip_no", length = 50)
    private String dispatchSlipNo;

    @OneToMany(mappedBy = "finishingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrimIssuance> trimIssuances = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
