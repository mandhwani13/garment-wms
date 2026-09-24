package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fabric_masters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_number", nullable = false, unique = true, length = 50)
    private String shortNumber;

    @Column(name = "fabric_name", nullable = false, length = 100)
    private String fabricName;

    @Column(length = 50)
    private String weave;

    @Column(name = "width_inches", nullable = false)
    @Builder.Default
    private double widthInches = 58.0;

    @Column(length = 150)
    private String composition;

    @Column(name = "standard_shrinkage_pct", nullable = false)
    @Builder.Default
    private double standardShrinkagePct = 3.0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
