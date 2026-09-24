package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "styles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "style_code", nullable = false, unique = true, length = 50)
    private String styleCode;

    @Column(name = "design_name", nullable = false, length = 100)
    private String designName;

    @Column(length = 50)
    private String category;

    @Column(name = "fabric_details", length = 255)
    private String fabricDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_id")
    private FabricMaster fabric;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
