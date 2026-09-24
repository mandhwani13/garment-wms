package com.garment.wms.model;

public enum BarcodeType {
    SET("Set / Bundle Barcode"),
    SINGLE("Single Piece Barcode");

    private final String displayName;

    BarcodeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
