package com.garment.wms.model;

public enum TransactionType {
    INWARD("Inward (Receipt)"),
    OUTWARD("Outward (Dispatch)");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
