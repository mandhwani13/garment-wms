package com.garment.wms.model;

public enum Role {
    ROLE_MASTER("Master Administrator"),
    ROLE_ADMIN("Operations Administrator"),
    ROLE_PRODUCTION_MANAGER("Production Manager"),
    ROLE_WAREHOUSE_MANAGER("Warehouse Manager"),
    ROLE_STITCHING_UNIT("Stitching Unit Operator"),
    ROLE_JOB_WORKER("Job Worker Subcontractor"),
    ROLE_WASHING_UNIT("Washing Plant Operator"),
    ROLE_FINISHING_UNIT("Finishing & Packing Operator"),
    ROLE_WAREHOUSE_USER("Warehouse Floor Operator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

