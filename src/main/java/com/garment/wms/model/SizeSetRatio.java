package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "size_set_ratios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeSetRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_set_id", nullable = false)
    private SizeSet sizeSet;

    @Column(name = "size_name", nullable = false, length = 30)
    private String sizeName;

    @Column(name = "ratio_count", nullable = false)
    @Builder.Default
    private int ratioCount = 1;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
