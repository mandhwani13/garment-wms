package com.garment.wms.model;

public enum InventoryStatus {
    PLANNED("Planned / Generated"),
    IN_WAREHOUSE("In Warehouse"),
    DISPATCHED("Dispatched / Outwarded");

    private final String displayName;

    InventoryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
