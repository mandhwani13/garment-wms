package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "size_sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_name", nullable = false, unique = true, length = 50)
    private String setName;

    @Column(length = 255)
    private String description;

    @Column(name = "total_ratio_pieces", nullable = false)
    @Builder.Default
    private int totalRatioPieces = 0;

    @OneToMany(mappedBy = "sizeSet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<SizeSetRatio> ratios = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public void addRatio(SizeSetRatio ratio) {
        ratios.add(ratio);
        ratio.setSizeSet(this);
        recalculateTotalRatioPieces();
    }

    public void removeRatio(SizeSetRatio ratio) {
        ratios.remove(ratio);
        ratio.setSizeSet(null);
        recalculateTotalRatioPieces();
    }

    public void recalculateTotalRatioPieces() {
        this.totalRatioPieces = ratios.stream().mapToInt(SizeSetRatio::getRatioCount).sum();
    }
}
