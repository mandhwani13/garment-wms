package com.garment.wms.model;

public enum Role {
    ROLE_MASTER("Master Administrator"),
    ROLE_ADMIN("Operations Administrator"),
    ROLE_WAREHOUSE_USER("Warehouse Operator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
