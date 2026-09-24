package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trim_issuances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrimIssuance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finishing_program_id", nullable = false)
    private FinishingProgram finishingProgram;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trim_id", nullable = false)
    private TrimAccessory trim;

    @Column(name = "required_qty", nullable = false)
    private double requiredQty;

    @Column(name = "issued_qty", nullable = false)
    private double issuedQty;

    @Column(name = "issued_at")
    @Builder.Default
    private OffsetDateTime issuedAt = OffsetDateTime.now();
}
