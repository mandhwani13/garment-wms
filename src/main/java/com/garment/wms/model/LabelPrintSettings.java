package com.garment.wms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "label_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelPrintSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(name = "preset_name", nullable = false, length = 50)
    private String presetName;

    @Column(name = "width_mm", nullable = false)
    private double widthMm;

    @Column(name = "height_mm", nullable = false)
    private double heightMm;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    @Column(name = "tspl_density", nullable = false)
    @Builder.Default
    private int tsplDensity = 8;

    @Column(name = "tspl_direction", nullable = false)
    @Builder.Default
    private int tsplDirection = 1;
}
