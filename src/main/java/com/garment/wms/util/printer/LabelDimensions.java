package com.garment.wms.util.printer;

import lombok.Getter;

@Getter
public enum LabelDimensions {
    SIZE_50X25("50mm x 25mm (Single Piece / Wash Tag)", 50.0, 25.0),
    SIZE_50X38("50mm x 38mm (Garment Price / Barcode Tag)", 50.0, 38.0),
    SIZE_100X50("100mm x 50mm (Set Bundle / Master Carton)", 100.0, 50.0);

    private final String description;
    private final double widthMm;
    private final double heightMm;

    LabelDimensions(String description, double widthMm, double heightMm) {
        this.description = description;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
    }

    public static LabelDimensions fromPreset(String preset) {
        if (preset == null) return SIZE_50X38;
        return switch (preset.toUpperCase()) {
            case "50X25", "SIZE_50X25" -> SIZE_50X25;
            case "100X50", "SIZE_100X50" -> SIZE_100X50;
            default -> SIZE_50X38;
        };
    }
}
