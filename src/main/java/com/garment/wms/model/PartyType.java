package com.garment.wms.model;

public enum PartyType {
    FABRIC_MILL("Fabric Mill / Yarn Supplier"),
    STITCHING_UNIT("Stitching Unit / Factory"),
    JOB_WORKER("Job Work Subcontractor (Embroidery / Print)"),
    WASHING_UNIT("Washing & Dyeing Unit"),
    FINISHING_UNIT("Finishing & Packing Plant"),
    CUSTOMER_BUYER("Customer / Retail Buyer");

    private final String displayName;

    PartyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
