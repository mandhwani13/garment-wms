package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_code", nullable = false, unique = true, length = 50)
    private String locationCode;

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @Column(name = "location_type", nullable = false, length = 30)
    private String locationType; // INTERNAL_WAREHOUSE, SUBCONTRACTOR_WIP, TRANSIT, CUSTOMER

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
