package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cutting_lots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuttingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lot_number", nullable = false, unique = true, length = 50)
    private String lotNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_set_id", nullable = false)
    private SizeSet sizeSet;

    @Column(name = "total_pieces", nullable = false)
    private int totalPieces;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PLANNED";

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @OneToMany(mappedBy = "cuttingLot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CuttingLotColor> colors = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public void addColor(CuttingLotColor color) {
        colors.add(color);
        color.setCuttingLot(this);
    }
}
